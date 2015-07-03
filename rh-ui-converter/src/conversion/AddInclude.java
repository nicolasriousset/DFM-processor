package conversion;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import dfm.DfmObject;

public class AddInclude extends AConversionRule {
    String newHeader;
    CppFile dest;
    
    public AddInclude(CppFile aDest, String aNewHeader) {
        newHeader = aNewHeader;
        dest = aDest;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.addIncludeHeader(dest, newHeader);
    }

}
