package conversion;

import conversion.condition.IConversionCondition;
import cpp.CppClass;
import dfm.DfmObject;

public class ChangePropertyValue extends AConversionRule {
    String objectTypeRegEx;
    String propName; 
    String propValue;
    IConversionCondition condition;
    
    public ChangePropertyValue(String anObjectTypeRegEx, String aPropName, String aPropValue) {
        objectTypeRegEx = anObjectTypeRegEx;
        propName = aPropName;
        propValue = aPropValue;
    }
    
    public ChangePropertyValue(String anObjectType, String aPropName, String aPropValue, IConversionCondition aCondition) {
        this(anObjectType, aPropName, aPropValue);
        condition = aCondition; 
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        String typeName = dfmObject.getTypeName();
        if (!typeName.matches(objectTypeRegEx))
            return false;
        
        if (condition != null && !condition.isVerified(dfmObject, cppClass))
            return false;
            
        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.properties().put(propName, propValue);
        return true;
    }

}
