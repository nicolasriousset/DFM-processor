package conversion.condition;

import cpp.CppClass;
import dfm.DfmObject;

public class BaseClassTypeCheck implements IConversionCondition {
    String expectedBaseClassName;
    
    public BaseClassTypeCheck(String expectedBaseClassName) {
        this.expectedBaseClassName = expectedBaseClassName;
    }
    
    @Override
    public boolean isVerified(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.getBaseClassName().compareTo(expectedBaseClassName) == 0;
    }

}
