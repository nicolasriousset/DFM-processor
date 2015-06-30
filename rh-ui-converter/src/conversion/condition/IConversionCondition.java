package conversion.condition;

import cpp.CppClass;
import dfm.DfmObject;

public interface IConversionCondition {
    boolean isVerified(DfmObject dfmObject, CppClass cppClass);
}
