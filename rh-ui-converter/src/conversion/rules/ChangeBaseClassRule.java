package conversion.rules;

import main.DfmObject;
import conversion.CppClass;
import conversion.CppClassReaderWriterException;

public class ChangeBaseClassRule extends AConversionRule {
    private String newBaseClass;
    public ChangeBaseClassRule(String aNewBaseClass) {
        newBaseClass = aNewBaseClass;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        // La règle n'est appliquée que pour la racine du DFM
        return dfmObject.getParent() == null && cppClass.getBaseClassName().compareTo(newBaseClass) != 0;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        try {
            cppClass.changeBaseClass(newBaseClass);
            return true;
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            return false;
        }
    }

}
