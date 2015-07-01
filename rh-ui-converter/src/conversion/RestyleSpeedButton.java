package conversion;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import cpp.CppClassReaderWriterException;
import cpp.Utils;
import dfm.DfmObject;

public class RestyleSpeedButton extends AConversionRule {
    String captionFilter;
    String imageId;      // ID de l'image dans la classe ImageManager
    String glyphData;

    public RestyleSpeedButton() {
    }

    public RestyleSpeedButton(String captionFilter, String imageId, String glyphData) {
        this.captionFilter = captionFilter.replace("&", "");
        this.imageId = imageId;
        this.glyphData = glyphData;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        if (dfmObject.getTypeName().compareToIgnoreCase("TSpeedButton") != 0)
            return false;

        String caption = dfmObject.properties().get("Caption");
        if (caption == null)
            return false;

        caption = StringUtils.strip(caption.replace("&", ""), "'");
        if (captionFilter != null && caption.compareToIgnoreCase(captionFilter) != 0)
            return false;

        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        try {
            final int BUTTON_HEIGHT = 55;
            final int BUTTON_WIDTH = 64;
            final int MARGIN = 7;
            dfmObject.properties().put("Height", String.valueOf(BUTTON_HEIGHT));
            dfmObject.properties().put("Width", String.valueOf(BUTTON_WIDTH));
            dfmObject.properties().put("Transparent", "True");
            dfmObject.properties().put("Layout", "blGlyphTop");
            dfmObject.properties().put("Margin", "-1");
            dfmObject.properties().put("Spacing", "0");
            dfmObject.properties().put("ParentFont", "True");

            ArrayList<String> anchorsList = new ArrayList<String>();
            if (dfmObject.isCloseToBottom()) {
                anchorsList.add("akBottom");
                dfmObject.properties().put("Top", Utils.add(dfmObject.getParent().properties().get("ClientHeight"), 0 - BUTTON_HEIGHT - MARGIN));
            } else {
                anchorsList.add("akTop");
            }

            if (dfmObject.isCloseToRight()) {
                anchorsList.add("akRight");
                dfmObject.properties().put("Left", Utils.add(dfmObject.getParent().properties().get("ClientWidth"), 0 - BUTTON_WIDTH - MARGIN));
            } else {
                anchorsList.add("akLeft");
            }
            if (dfmObject.isCloseToLeft()) {
                dfmObject.properties().put("Left", String.valueOf(MARGIN));
            }

            String anchors = "[" + Joiner.on(",").join(anchorsList) + "]";
            dfmObject.properties().put("Anchors", anchors);

            if (glyphData != null) {
                dfmObject.properties().put("NumGlyphs", "1");
                dfmObject.properties().put("Glyph.Data", glyphData);
            }

            if (imageId != null) {
                cppClass.appendToApplyStyleMethod(String.format("    ImageManager::GetInstance().LoadBitmap(%s->Glyph, ImageManager::%s);",
                        dfmObject.getName(), imageId));
                cppClass.addHeader(CppFile.BODY, "ImageManager.h");
            }

            return true;
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            return false;
        }

    }
}
