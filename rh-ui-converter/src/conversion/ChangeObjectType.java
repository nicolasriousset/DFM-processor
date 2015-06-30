package conversion;

import cpp.CppClass;
import cpp.CppClassReaderWriterException;
import dfm.DfmObject;

public class ChangeObjectType extends AConversionRule {
    String oldObjectType;
    String newObjectType;
    
    public ChangeObjectType(String currentObjectType, String newObjectType) {
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
