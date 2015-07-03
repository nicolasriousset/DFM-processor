package conversion;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import dfm.DfmObject;

public class RemoveLineOfCode extends AConversionRule {
    String regexFilter;
    CppFile cppFile;

    public RemoveLineOfCode(CppFile cppFile, String regexFilter) {
        this.regexFilter = regexFilter;
        this.cppFile = cppFile;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.containsLineOfCode(regexFilter);
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        cppClass.removeLineOfCode(cppFile, regexFilter);
        return true;
    }

}
