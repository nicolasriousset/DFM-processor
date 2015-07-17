package conversion;

import cpp.CppClass;
import dfm.DfmObject;

public class UseParentFont extends AConversionRule {

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        if (!dfmObject.propertieEqualsTo("Font.Charset", "ANSI_CHARSET"))
            return false;
        
        if (!dfmObject.propertieEqualsTo("Font.Color", "clWindowText"))
            return false;

        if (!dfmObject.propertieEqualsTo("Font.Height", "-13"))
            return false;

        if (!dfmObject.propertieEqualsTo("Font.Name", "'Arial'"))
            return false;

        if (!dfmObject.propertieEqualsTo("Font.Style", "[]"))
            return false;

        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.properties().put("ParentFont", "True");
        return true;
    }

}
