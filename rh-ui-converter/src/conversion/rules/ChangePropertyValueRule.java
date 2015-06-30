package conversion.rules;

import main.DfmObject;
import conversion.CppClass;

public class ChangePropertyValueRule extends AConversionRule {
    String objectType;
    String propName; 
    String propValue;
    
    public ChangePropertyValueRule(String anObjectType, String aPropName, String aPropValue) {
        objectType = anObjectType;
        propName = aPropName;
        propValue = aPropValue;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.getTypeName().compareTo(objectType) == 0;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.getProperties().put(propName, propValue);
        return true;
    }

}
