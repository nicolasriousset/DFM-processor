package conversion;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import dfm.DfmObject;

public class RemoveLineOfCode extends AConversionRule {
    String keywords;
    CppFile cppFile;

    public RemoveLineOfCode(CppFile cppFile, String keywords) {
        this.keywords = keywords;
        this.cppFile = cppFile;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.containsLineOfCode(keywords);
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        cppClass.removeLineOfCode(cppFile, keywords);
        return true;
    }

}
