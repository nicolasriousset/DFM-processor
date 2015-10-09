package conversion;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import dfm.DfmObject;

public class ReplaceCode extends AConversionRule {

    String oldCodeRegex;
    String newCode;
    CppFile cppFile;

    public ReplaceCode(CppFile cppFile, String oldCodeRegex, String newCode) {
        this.oldCodeRegex = oldCodeRegex;
        this.newCode = newCode;
        this.cppFile = cppFile;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.containsCode(oldCodeRegex);
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        cppClass.replaceCode(cppFile, oldCodeRegex, newCode);
        return true;
    }
}
