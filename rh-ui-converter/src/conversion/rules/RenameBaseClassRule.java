package conversion.rules;

import main.DfmObject;
import conversion.CppClass;
import conversion.CppClassReaderWriterException;

public class RenameBaseClassRule extends AConversionRule {
    private String newBaseClass;
    public RenameBaseClassRule(String aNewBaseClass) {
        newBaseClass = aNewBaseClass;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.getBaseClassName() != newBaseClass;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        try {
            cppClass.renameBaseClass(newBaseClass);
            return true;
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            return false;
        }
    }

}
