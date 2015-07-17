package conversion;

import cpp.CppClass;
import cpp.CppClassReaderWriterException;
import dfm.DfmObject;

public class ChangeObjectType extends AConversionRule {
    String currentObjectTypeRegEx;
    String newObjectType;
    
    public ChangeObjectType(String currentObjectTypeRegEx, String newObjectType) {
        this.currentObjectTypeRegEx = currentObjectTypeRegEx;
        this.newObjectType = newObjectType;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.getTypeName().matches(currentObjectTypeRegEx);
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
