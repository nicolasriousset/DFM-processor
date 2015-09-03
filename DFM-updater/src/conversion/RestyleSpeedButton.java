package conversion;

import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import cpp.CppClassReaderWriterException;
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

    private void updateAnchors(DfmObject dfmObject) {
        ArrayList<String> anchorsList = new ArrayList<String>();
        if (dfmObject.isCloseToBottom()) {
            anchorsList.add("akBottom");
        } else {
            anchorsList.add("akTop");
        }

        if (dfmObject.isCloseToRight()) {
            anchorsList.add("akRight");
        } else {
            anchorsList.add("akLeft");
        }
        String anchors = "[" + Joiner.on(",").join(anchorsList) + "]";
        dfmObject.properties().put("Anchors", anchors);        
    }
    
    private void updateSizeAndPos(DfmObject dfmObject) {
        int currentWidth = Integer.parseInt(dfmObject.properties().get("Width"));
        int currentHeight = Integer.parseInt(dfmObject.properties().get("Height"));
        int newHeight = currentWidth > 32 ? 55 : (currentWidth > 16 ? 24 : 16);
        int newWidth = currentWidth > 32 ? 64 : (currentWidth > 16 ? 24 : 16);
        int parentWidth = 0;
        if (dfmObject.getParent().properties().get("ClientWidth") != null) 
            parentWidth = Integer.parseInt(dfmObject.getParent().properties().get("ClientWidth"));
        else
            parentWidth = Integer.parseInt(dfmObject.getParent().properties().get("Width"));
        int parentHeight = 0; 
        if (dfmObject.getParent().properties().get("ClientHeight") != null) 
            parentHeight = Integer.parseInt(dfmObject.getParent().properties().get("ClientHeight"));
        else
            parentHeight = Integer.parseInt(dfmObject.getParent().properties().get("Height"));
        int MARGIN = 7;
        
        double sideRatio = Math.min((double)currentHeight, (double)currentWidth) / Math.max((double)currentHeight, (double)currentWidth);
        if (sideRatio >= 0.6)
        {
            // on ne modifie les dimensions que pour les boutons à peu près carrés 
            dfmObject.properties().put("Height", String.valueOf(newHeight));
            dfmObject.properties().put("Width", String.valueOf(newWidth));            
        }
        else
        {
            newHeight = currentHeight;
            newWidth = currentWidth;
        }

        if (dfmObject.isCloseToBottom()) {
            dfmObject.properties().put("Top", String.valueOf(parentHeight - newHeight - MARGIN));
        }
        
        if (dfmObject.isCloseToRight()) {
            dfmObject.properties().put("Left", String.valueOf(parentWidth - newWidth - MARGIN));
        }
        
        if (dfmObject.isCloseToLeft()) {
            dfmObject.properties().put("Left", String.valueOf(MARGIN));
        }
    }
    
    private void updateImage(DfmObject dfmObject, CppClass cppClass) throws CppClassReaderWriterException {
        if (glyphData != null) {
            dfmObject.properties().put("NumGlyphs", "1");
            dfmObject.properties().put("Glyph.Data", glyphData);
        }

        if (imageId != null) {
            cppClass.appendToApplyStyleMethod(String.format("    ImageManager::GetInstance().LoadBitmap(%s->Glyph, ImageManager::%s);",
                    dfmObject.getName(), imageId));
            cppClass.addIncludeHeader(CppFile.BODY, "ImageManager.h");
        }        
    }
    
    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        try {
            updateSizeAndPos(dfmObject);
            updateAnchors(dfmObject);
            
            dfmObject.properties().put("Transparent", "True");
            dfmObject.properties().put("Layout", "blGlyphTop");
            dfmObject.properties().put("Margin", "-1");
            dfmObject.properties().put("Spacing", "0");
            dfmObject.properties().put("ParentFont", "True");

            updateImage(dfmObject, cppClass);

            return true;
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            return false;
        }

    }
}
