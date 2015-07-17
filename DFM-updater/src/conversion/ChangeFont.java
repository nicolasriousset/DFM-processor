package conversion;

import cpp.CppClass;
import dfm.DfmObject;

public class ChangeFont extends AConversionRule {
    String objectTypeFilter;
    String fontCharset;
    String fontColor;
    String fontHeight;
    String fontName;
    String fontStyle;

    public ChangeFont(String objectTypeFilter) {
        this(objectTypeFilter, "ANSI_CHARSET", "clBlack", "-13", "'Arial'", "[]");

    }

    public ChangeFont(String objectTypeFilter, String fontCharset, String fontColor, String fontHeight, String fontName, String fontStyle) {
        this.objectTypeFilter = objectTypeFilter;
        this.fontCharset = fontCharset;
        this.fontColor = fontColor;
        this.fontHeight = fontHeight;
        this.fontName = fontName;
        this.fontStyle = fontStyle;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.isInstanceOf(objectTypeFilter);
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        dfmObject.properties().put("Font.Charset", fontCharset);
        dfmObject.properties().put("Font.Color", fontColor);
        dfmObject.properties().put("Font.Height", fontHeight);
        dfmObject.properties().put("Font.Name", fontName);
        dfmObject.properties().put("Font.Style", fontStyle);
        dfmObject.properties().put("ParentFont", "False");
        return true;
    }

}
