package conversion.condition;

import cpp.CppClass;
import dfm.DfmObject;

public class PropertyValueIsNullOrEquals implements IConversionCondition{
    String propertyName;
    String propertyValue;
    
    public PropertyValueIsNullOrEquals(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }
    
    @Override
    public boolean isVerified(DfmObject dfmObject, CppClass cppClass) {
        if (dfmObject.properties().get(propertyName) == null)
            return true;
        return dfmObject.properties().get(propertyName).compareTo(propertyValue) == 0;
    }

}
