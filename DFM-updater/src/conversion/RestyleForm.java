package conversion;

import cpp.CppClass;
import cpp.Utils;
import dfm.DfmObject;

public class RestyleForm extends AConversionRule {

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        if (dfmObject.getParent() != null)
            return false;

        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.properties().put("Color", "clWhite");
        dfmObject.properties().put("Font.Charset", "ANSI_CHARSET");
        dfmObject.properties().put("Font.Color", "clGray");
        dfmObject.properties().put("Font.Height", "-13");
        dfmObject.properties().put("Font.Name", "'Arial'");
        dfmObject.properties().put("Position", "poDesigned");
        dfmObject.properties().put("TextHeight", "16");
        dfmObject.properties().put("Font.Style", "[]");
        dfmObject.properties().put("Ctl3D", "false");
        
        String borderStyle = dfmObject.properties().get("BorderStyle"); 
        if (borderStyle == null || borderStyle.compareTo("bsNone") != 0) {
            dfmObject.properties().put("Constraints.MinHeight", Utils.add(dfmObject.properties().get("ClientHeight"), 39));
            dfmObject.properties().put("Constraints.MinWidth", Utils.add(dfmObject.properties().get("ClientWidth"), 16));
        }
        
        return true;
    }

}
