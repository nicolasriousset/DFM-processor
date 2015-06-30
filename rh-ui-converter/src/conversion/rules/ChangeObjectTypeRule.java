package conversion.rules;

import main.DfmObject;
import conversion.CppClass;
import conversion.CppClassReaderWriterException;

public class ChangeObjectTypeRule extends AConversionRule {
    String oldObjectType;
    String newObjectType;
    
    public ChangeObjectTypeRule(String currentObjectType, String newObjectType) {
        this.oldObjectType = currentObjectType;
        this.newObjectType = newObjectType;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.getTypeName().compareTo(oldObjectType) == 0;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.setTypeName(newObjectType);
        
        // Updating declaration in the C++ header file
        try {
            cppClass.changeMemberVariableType(dfmObject.getName(), newObjectType);
            return true;
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            return false;
        }        
    }

}
