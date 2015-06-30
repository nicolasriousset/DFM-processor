package conversion.rules;

import main.DfmObject;
import conversion.CppClass;
import conversion.CppClass.CppFile;

public class AddIncludeRule extends AConversionRule {
    String newHeader;
    CppFile dest;
    
    public AddIncludeRule(CppFile aDest, String aNewHeader) {
        newHeader = aNewHeader;
        dest = aDest;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.addHeader(dest, newHeader);
    }

}
