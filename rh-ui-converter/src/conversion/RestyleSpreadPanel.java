package conversion;

import java.util.ArrayList;

import com.google.common.base.Joiner;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import cpp.CppClass.Visibility;
import cpp.CppClassReaderWriterException;
import dfm.DfmObject;
import dfm.DfmObject.Direction;

public class RestyleSpreadPanel extends AConversionRule {

    DfmObject findTitlePanel(DfmObject panel) {
        // The title panel must be a child of the main panel, with a toolbar inside
        for (DfmObject child : panel) {
            if (child.isInstanceOf("TPanel")) {
                for (DfmObject grandChild : child) {
                    if (grandChild.isInstanceOf("TToolBar")) {
                        return child;
                    }
                }
            }
        }

        return null;
    }

    DfmObject findSpreadPanel(DfmObject panel) {
        // The spread panel must be a child of the main panel, with a spread inside
        for (DfmObject child : panel) {
            if (child.isInstanceOf("TPanel")) {
                for (DfmObject grandChild : child) {
                    if (grandChild.isInstanceOf("TfpSpread")) {
                        return child;
                    }
                }
            }
        }

        return null;
    }
    
    DfmObject findSpreadToolBar(DfmObject panel) {
        // The toolbar must be a child of the title panel
        DfmObject titlePanel = findTitlePanel(panel);
        if (titlePanel == null)
            return null;

        for (DfmObject grandChild : titlePanel) {
            if (grandChild.isInstanceOf("TToolBar")) {
                return grandChild;
            }
        }

        return null;
    }

    DfmObject findSpread(DfmObject panel) {
        // The spread must be a grand child of the panel
        for (DfmObject child : panel) {
            if (child.isInstanceOf("TPanel")) {
                for (DfmObject grandChild : child) {
                    if (grandChild.isInstanceOf("TfpSpread")) {
                        return grandChild;
                    }
                }
            }
        }

        return null;
    }

    DfmObject findTitleImage(DfmObject panel) {
        // The image must be a child of the title panel
        DfmObject titlePanel = findTitlePanel(panel);
        if (titlePanel == null)
            return null;

        for (DfmObject grandChild : titlePanel) {
            if (grandChild.isInstanceOf("TImage")) {
                return grandChild;
            }
        }

        return null;
    }

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        if (dfmObject.getTypeName().compareTo("TPanel") != 0)
            return false;

        if (findSpreadToolBar(dfmObject) == null)
            return false;

        if (findSpread(dfmObject) == null)
            return false;

        if (findTitleImage(dfmObject) == null)
            return false;

        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        boolean result = true;
        DfmObject mainPanel = dfmObject;
        
        // Updating title panel properties
        DfmObject titlePanel = findTitlePanel(mainPanel);
        titlePanel.getProperties().put("Height", "27");
        titlePanel.getProperties().put("Caption", "''");
        titlePanel.getProperties().put("ParentColor", "True");

        // Updating toolbar properties
        DfmObject toolbar = findSpreadToolBar(mainPanel);
        toolbar.getProperties().put("Images", "wPrinc.SpreadToolBarImages");
        toolbar.getProperties().put("Width", "180");
        toolbar.getProperties().put("ParentColor", "True");
        toolbar.getProperties().put("Transparent", "True");
        // Removing separators
        for (int i = toolbar.getChildrenCount() -1; i >= 0; i--) {
            DfmObject button = toolbar.getChild(i);
            String style = button.getProperties().get("Style");
            if (button.isInstanceOf("TToolButton") && style != null && style.compareTo("tbsSeparator") == 0) {
                toolbar.removeChild(i);
                cppClass.removeLineOfCode(CppFile.HEADER, button.getName());
            }
        }
        try {            
            cppClass.appendToApplyStyleMethod(String.format("    Apparence::ApplySpreadTitleBarStyle(%s, %s, useLegacyUI);", titlePanel.getName(), toolbar.getName()));
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            result = false;
        }
        cppClass.addHeader(CppFile.BODY, "ImageManager.h");
       
        
        // Updating title image properties
        DfmObject image = findTitleImage(mainPanel);
        image.getProperties().put("Align", "alRight");
        image.getProperties().put("Transparent", "True");
        image.getProperties().put("Width", "475");
        
        // Updating main panel properties
        ArrayList<String> anchorsList = new ArrayList<String>();
        if (!mainPanel.hasNeighbour(Direction.UP, "TPanel"))
            anchorsList.add("akTop");
        if (!mainPanel.hasNeighbour(Direction.DOWN, "TPanel") || !mainPanel.hasNeighbour(Direction.UP, "TPanel"))
            anchorsList.add("akBottom");
        if (!mainPanel.hasNeighbour(Direction.LEFT, "TPanel"))
            anchorsList.add("akLeft");
        if (!mainPanel.hasNeighbour(Direction.RIGHT, "TPanel"))
            anchorsList.add("akRight");
        String anchors = "[" + Joiner.on(",").join(anchorsList) + "]";
        
        mainPanel.getProperties().put("Anchors", anchors);
        mainPanel.getProperties().put("ParentColor", "True");

        // Updating spread panel properties
        DfmObject spreadPanel = findSpreadPanel(mainPanel);
        spreadPanel.getProperties().put("ParentColor", "True");
        
        // Adding on resize event to resize spread columns
        DfmObject mainForm = mainPanel.getRoot();
        String onResizeMethodName = mainForm.getProperties().get("OnResize");
        if (onResizeMethodName == null || onResizeMethodName.trim().isEmpty())
            onResizeMethodName = "FormResize";
        DfmObject spread = findSpread(mainPanel);        
        String rawCode = "    // on mémorise dans une constante statique la largeur totale des colonnes définie dans l'IDE\r\n" +
                        "    static const double MIN_%1$S_COLUMNS_WIDTH = ComputeVisibleColumnsTotalWidth(%1$s);\r\n" +
                        "    FitColumnsWidthToSpreadWidth(%1$s, MIN_%1$S_COLUMNS_WIDTH);\r\n";
        String onResizeMethodCode = String.format(rawCode, spread.getName());
        try {
            cppClass.createMethodOrAppendTo(Visibility.PUBLISHED, "__fastcall", onResizeMethodName, "TObject *Sender", onResizeMethodCode, "");
            mainForm.getProperties().put("OnResize", onResizeMethodName);
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            result = false;
        }                
        
        return result;
    }

}
