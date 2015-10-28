package conversion.condition;

import cpp.CppClass;
import dfm.DfmObject;

public class IsDerivingFrom implements IConversionCondition {
    String expectedBaseClassName;
    
    public IsDerivingFrom(String expectedBaseClassName) {
        this.expectedBaseClassName = expectedBaseClassName;
    }
    
    @Override
    public boolean isVerified(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.getBaseClassName().compareTo(expectedBaseClassName) == 0;
    }

}
