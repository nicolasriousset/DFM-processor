package conversion;

import cpp.CppClass;
import dfm.DfmObject;

public class RemoveLineOfCode extends AConversionRule {
    String keywords;

    public RemoveLineOfCode(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.containsLineOfCode(keywords);
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        cppClass.removeLineOfCode(keywords);
        return true;
    }

}
