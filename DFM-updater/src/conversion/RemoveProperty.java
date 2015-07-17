package conversion;

import cpp.CppClass;
import dfm.DfmObject;

public class RemoveProperty extends AConversionRule {
    String propName;
    
    public RemoveProperty(String aPropName) {
        propName = aPropName;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.properties().remove(propName);
        return true;
    }

}
