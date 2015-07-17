package conversion;

import conversion.condition.IConversionCondition;
import cpp.CppClass;
import cpp.CppClassReaderWriterException;
import dfm.DfmObject;

public class ChangeBaseClass extends AConversionRule {
    private String newBaseClass;
    private IConversionCondition condition;
    
    public ChangeBaseClass(String aNewBaseClass) {
        newBaseClass = aNewBaseClass;
    }
    
    public ChangeBaseClass(String aNewBaseClass, IConversionCondition aCondition) {
        this(aNewBaseClass);
        condition = aCondition;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        // La règle n'est appliquée que pour la racine du DFM
        if (dfmObject.getParent() != null || cppClass.getBaseClassName().compareTo(newBaseClass) == 0)
            return false;
        
        if (condition != null && !condition.isVerified(dfmObject,  cppClass))
            return false;
        
        return true;
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
