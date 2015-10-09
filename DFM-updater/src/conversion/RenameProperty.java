package conversion;

import cpp.CppClass;
import dfm.DfmObject;

public class RenameProperty extends AConversionRule {
    String oldPropName;
    String newPropName;
    String objectTypeRegEx;
    
    public RenameProperty(String anObjectTypeRegEx, String oldName, String newName) {
        objectTypeRegEx = anObjectTypeRegEx;
        oldPropName = oldName;
        newPropName = newName;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.getTypeName().matches(objectTypeRegEx) && dfmObject.properties().containsKey(oldPropName);
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        if (!dfmObject.properties().containsKey(oldPropName)) {
            return true;
        }            
        
        String value = dfmObject.properties().get(oldPropName);
        dfmObject.properties().remove(oldPropName);
        dfmObject.properties().put(newPropName, value);
        return false;
    }

}
