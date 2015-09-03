package conversion;

import cpp.CppClass;
import cpp.CppClass.CppFile;
import cpp.CppClass.Visibility;
import cpp.CppClassReaderWriterException;
import dfm.DfmObject;

public class TvaToTfpSpread extends AConversionRule {

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.isInstanceOf("TvaSpread");
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        try {
            replaceTvaSpreadByTfpSpread(dfmObject, cppClass);
            DfmObject pnlFondSpread = embedInSpreadPanel(dfmObject, cppClass);
            RestyleSpreadPanel restyle = new RestyleSpreadPanel();
            restyle.apply(pnlFondSpread, cppClass);
            return true;
        } catch (CppClassReaderWriterException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void replaceTvaSpreadByTfpSpread(DfmObject dfmObject, CppClass cppClass) {
        ChangeObjectType tvaToTfpSpread = new ChangeObjectType("TvaSpread", "TfpSpread");
        tvaToTfpSpread.apply(dfmObject, cppClass);

        cppClass.addIncludeHeader(CppFile.BODY, "FctSpreadAntibia.h");
        cppClass.replace(CppFile.HEADER, "FPSpread_OCX", "FPSpreadADO_OCX");
        cppClass.replace(CppFile.BODY, "FPSpread_OCX", "FPSpreadADO_OCX");
        cppClass.replace(CppFile.BODY, "Fpspread_tlb", "Fpspreadado_tlb");

        cppClass.replace(CppFile.BODY, "ColorUserSpread(", "DesignSpread7(");
        cppClass.replace(CppFile.BODY, "ColorInSpread(", "ColorInSpread7(");
        cppClass.replace(CppFile.BODY, "ActiveCellInSpread(", "ActiveCellInSpread7(");
        cppClass.replace(CppFile.BODY, "RechercheInSpreadExceptLine(", "RechercheInSpread7ExceptLine(");
        cppClass.replace(CppFile.BODY, "RechercheInSpread(", "RechercheInSpread7(");
        cppClass.replace(CppFile.BODY, "TriInSpread(", "TriInSpread7(");
        cppClass.replace(CppFile.BODY, "CentreColonneSpread(", "CentreColonneSpread7(");
        cppClass.replace(CppFile.BODY, "DelRowInSpread(", "DelRowInSpread7(");
        cppClass.replace(CppFile.BODY, "DesignSpread(", "DesignSpread7(");

        cppClass.replace(CppFile.BODY, "SS_CELL_TYPE_COMBOBOX", "CellTypeComboBox");
        cppClass.replace(CppFile.BODY, "SS_CELL_TYPE_STATIC_TEXT", "CellTypeStaticText");
        cppClass.replace(CppFile.BODY, "SS_CELL_TYPE_EDIT", "CellTypeEdit");
        cppClass.replace(CppFile.BODY, "SS_CELL_TYPE_PIC", "CellTypePic");
        cppClass.replace(CppFile.BODY, "SS_CELL_TYPE_INTEGER", "CellTypeInteger");

        cppClass.replace(CppFile.BODY, "SS_CELL_H_ALIGN_CENTER", "TypeHAlignCenter");
        cppClass.replace(CppFile.BODY, "SS_CELL_H_ALIGN_LEFT", "TypeHAlignLeft");

        cppClass.replace(CppFile.BODY, "SS_CELL_EDIT_CHAR_SET_ALPHANUMERIC", "TypeEditCharSetAlphanumeric");
        cppClass.replace(CppFile.BODY, "SS_CELL_EDIT_CHAR_SET_ASCII", "TypeEditCharSetASCII");
    }

    private DfmObject embedInSpreadPanel(DfmObject dfmObject, CppClass cppClass) throws CppClassReaderWriterException {
        if (dfmObject == null || dfmObject.getParent() == null)
            return null;

        DfmObject spread = dfmObject;
        DfmObject parent = dfmObject.getParent();
        String suffixe = spread.getName().replace("Spread_", "").replace("Spread", "");
        parent.removeChild(spread);

        DfmObject pnlFondSpread = new DfmObject("TPanel", "pnlFondSpread" + suffixe);
        parent.addChild(pnlFondSpread);
        cppClass.addVariable(Visibility.PUBLISHED, pnlFondSpread.getTypeName() + "*", pnlFondSpread.getName());
        pnlFondSpread.properties().put("Left", spread.properties().get("Left"));
        pnlFondSpread.properties().put("Top", spread.properties().get("Top"));
        pnlFondSpread.properties().put("Width", spread.properties().get("Width"));
        pnlFondSpread.properties().put("Height", spread.properties().get("Height"));
        pnlFondSpread.properties().put("Anchors", "[akLeft, akTop]");
        pnlFondSpread.properties().put("BevelOuter", "bvNone");
        pnlFondSpread.properties().put("BorderWidth", "4");
        pnlFondSpread.properties().put("Caption", "'pnlFondSpreadCatGrd'");
        pnlFondSpread.properties().put("ParentColor", "True");

        DfmObject pnlTitreSpread = new DfmObject("TPanel", "pnlTitreSpread" + suffixe);
        pnlFondSpread.addChild(pnlTitreSpread);
        cppClass.addVariable(Visibility.PUBLISHED, pnlTitreSpread.getTypeName() + "*", pnlTitreSpread.getName());
        pnlTitreSpread.properties().put("Left", "4");
        pnlTitreSpread.properties().put("Top", "4");
        pnlTitreSpread.properties().put("Width", "442");
        pnlTitreSpread.properties().put("Height", "27");
        pnlTitreSpread.properties().put("Align", "alTop");
        pnlTitreSpread.properties().put("BevelOuter", "bvNone");
        pnlTitreSpread.properties().put("ParentColor", "True");

        DfmObject imgTitreSpread = new DfmObject("TImage", "imgTitreSpread" + suffixe);
        pnlTitreSpread.addChild(imgTitreSpread);
        cppClass.addVariable(Visibility.PUBLISHED, imgTitreSpread.getTypeName() + "*", imgTitreSpread.getName());
        imgTitreSpread.properties().put("Left", "244");
        imgTitreSpread.properties().put("Top", "0");
        imgTitreSpread.properties().put("Width", "198");
        imgTitreSpread.properties().put("Height", "27");
        imgTitreSpread.properties().put("Align", "alRight");
        imgTitreSpread.properties().put("Transparent", "True");

        DfmObject tlbrSpread = new DfmObject("TToolBar", "tlbrSpread" + suffixe);
        pnlTitreSpread.addChild(tlbrSpread);
        cppClass.addVariable(Visibility.PUBLISHED, tlbrSpread.getTypeName() + "*", tlbrSpread.getName());
        tlbrSpread.properties().put("Left", "0");
        tlbrSpread.properties().put("Top", "0");
        tlbrSpread.properties().put("Width", "180");
        tlbrSpread.properties().put("Height", "27");
        tlbrSpread.properties().put("Align", "alLeft");
        tlbrSpread.properties().put("ButtonHeight", "28");
        tlbrSpread.properties().put("ButtonWidth", "29");
        tlbrSpread.properties().put("Caption", "''");
        tlbrSpread.properties().put("EdgeInner", "esNone");
        tlbrSpread.properties().put("EdgeOuter", "esNone");
        tlbrSpread.properties().put("Flat", "True");
        tlbrSpread.properties().put("Images", "wPrinc.SpreadToolBarImages");
        tlbrSpread.properties().put("Transparent", "True");

        DfmObject tlbtnCreation = new DfmObject("TToolButton", "tlbtnCreation" + suffixe);
        tlbrSpread.addChild(tlbtnCreation);
        cppClass.addVariable(Visibility.PUBLISHED, tlbtnCreation.getTypeName() + "*", tlbtnCreation.getName());
        tlbtnCreation.properties().put("Left", "0");
        tlbtnCreation.properties().put("Top", "0");
        tlbtnCreation.properties().put("Cursor", "crHandPoint");
        tlbtnCreation.properties().put("Hint", "'Ajouter'");
        tlbtnCreation.properties().put("Caption", "''");
        tlbtnCreation.properties().put("ImageIndex", "0");
        tlbtnCreation.properties().put("ParentShowHint", "False");
        tlbtnCreation.properties().put("ShowHint", "True");
        tlbtnCreation.properties().put("Visible", "False");

        DfmObject tlbtnSuppression = new DfmObject("TToolButton", "tlbtnSuppression" + suffixe);
        tlbrSpread.addChild(tlbtnSuppression);
        cppClass.addVariable(Visibility.PUBLISHED, tlbtnSuppression.getTypeName() + "*", tlbtnSuppression.getName());
        tlbtnSuppression.properties().put("Left", "29");
        tlbtnSuppression.properties().put("Top", "0");
        tlbtnSuppression.properties().put("Cursor", "crHandPoint");
        tlbtnSuppression.properties().put("Hint", "'Supprimer'");
        tlbtnSuppression.properties().put("Caption", "''");
        tlbtnSuppression.properties().put("ImageIndex", "1");
        tlbtnSuppression.properties().put("ParentShowHint", "False");
        tlbtnSuppression.properties().put("ShowHint", "True");
        tlbtnSuppression.properties().put("Visible", "False");

        DfmObject tlbtnRecherche = new DfmObject("TToolButton", "tlbtnRecherche" + suffixe);
        tlbrSpread.addChild(tlbtnRecherche);
        cppClass.addVariable(Visibility.PUBLISHED, tlbtnRecherche.getTypeName() + "*", tlbtnRecherche.getName());
        tlbtnRecherche.properties().put("Left", "58");
        tlbtnRecherche.properties().put("Top", "0");
        tlbtnRecherche.properties().put("Cursor", "crHandPoint");
        tlbtnRecherche.properties().put("Hint", "'Rechercher'");
        tlbtnRecherche.properties().put("Caption", "''");
        tlbtnRecherche.properties().put("ImageIndex", "2");
        tlbtnRecherche.properties().put("ParentShowHint", "False");
        tlbtnRecherche.properties().put("ShowHint", "True");
        tlbtnRecherche.properties().put("Visible", "False");

        DfmObject tlbtnExcel = new DfmObject("TToolButton", "tlbtnExcel" + suffixe);
        tlbrSpread.addChild(tlbtnExcel);
        cppClass.addVariable(Visibility.PUBLISHED, tlbtnExcel.getTypeName() + "*", tlbtnExcel.getName());
        tlbtnExcel.properties().put("Left", "87");
        tlbtnExcel.properties().put("Top", "0");
        tlbtnExcel.properties().put("Cursor", "crHandPoint");
        tlbtnExcel.properties().put("Hint", "'Exporter vers Excel'");
        tlbtnExcel.properties().put("Caption", "''");
        tlbtnExcel.properties().put("ImageIndex", "3");
        tlbtnExcel.properties().put("ParentShowHint", "False");
        tlbtnExcel.properties().put("ShowHint", "True");
        tlbtnExcel.properties().put("Visible", "False");

        DfmObject tlbtnImpression = new DfmObject("TToolButton", "tlbtnImpression" + suffixe);
        tlbrSpread.addChild(tlbtnImpression);
        cppClass.addVariable(Visibility.PUBLISHED, tlbtnImpression.getTypeName() + "*", tlbtnImpression.getName());
        tlbtnImpression.properties().put("Left", "116");
        tlbtnImpression.properties().put("Top", "0");
        tlbtnImpression.properties().put("Cursor", "crHandPoint");
        tlbtnImpression.properties().put("Hint", "'Imprimer'");
        tlbtnImpression.properties().put("Caption", "''");
        tlbtnImpression.properties().put("ImageIndex", "4");
        tlbtnImpression.properties().put("ParentShowHint", "False");
        tlbtnImpression.properties().put("ShowHint", "True");
        tlbtnImpression.properties().put("Visible", "False");

        DfmObject tlbtnApercu = new DfmObject("TToolButton", "tlbtnApercu" + suffixe);
        tlbrSpread.addChild(tlbtnApercu);
        cppClass.addVariable(Visibility.PUBLISHED, tlbtnApercu.getTypeName() + "*", tlbtnApercu.getName());
        tlbtnApercu.properties().put("Left", "145");
        tlbtnApercu.properties().put("Top", "0");
        tlbtnApercu.properties().put("Cursor", "crHandPoint");
        tlbtnApercu.properties().put("Hint", "'Aperçu'");
        tlbtnApercu.properties().put("Caption", "''");
        tlbtnApercu.properties().put("ImageIndex", "5");
        tlbtnApercu.properties().put("ParentShowHint", "False");
        tlbtnApercu.properties().put("ShowHint", "True");
        tlbtnApercu.properties().put("Visible", "False");

        DfmObject pnlSpread = new DfmObject("TPanel", "pnlSpread" + suffixe);
        pnlFondSpread.addChild(pnlSpread);
        pnlSpread.properties().put("Left", "4");
        pnlSpread.properties().put("Top", "31");
        pnlSpread.properties().put("Width", "442");
        pnlSpread.properties().put("Height", "126");
        pnlSpread.properties().put("Align", "alClient");
        pnlSpread.properties().put("BevelOuter", "bvNone");
        pnlSpread.properties().put("ParentColor", "True");

        pnlSpread.addChild(spread);
        spread.properties().put("Left", "0");
        spread.properties().put("Top", "0");
        spread.properties().put("Align", "alClient");

        return pnlFondSpread;
    }
}
