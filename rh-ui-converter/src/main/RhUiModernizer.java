package main;

import java.io.File;

import conversion.*;
import conversion.condition.PropertyValueIsNullOrEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;

import cpp.*;
import cpp.CppClass.CppFile;
import dfm.DfmObject;
import dfm.DfmReaderWriter;
import dfm.DfmReaderWriterException;

public class RhUiModernizer {
    static Logger log = Logger.getLogger(RhUiModernizer.class.getName());

    public RhUiModernizer() {
    }

    public void run(String folder, String dfmFiles) {
        try {
            Path outputFileName = Paths.get(folder, "processedDfms.txt");
            File inputDirectory = new File(folder);
            PatternFilenameFilter dfmFileFilter = new PatternFilenameFilter(".*\\.dfm");
            File[] inputFiles = inputDirectory.listFiles(dfmFileFilter);
            if (inputFiles == null) {
                throw new IOException("Could not find input *.pos files in " + folder);
            }
            log.info("Found " + inputFiles.length + " input files");
            File outputFile = new File(outputFileName.toString());
            Files.createParentDirs(outputFile);
            for (File dfmFile : inputFiles) {
                ProcessDfm(dfmFile);
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    private void updateDfmObjects(DfmObject dfmObject, CppClass cppClass) {
        final String EDIT_BOX_HEIGHT = "24";
        
        ArrayList<AConversionRule> rules = new ArrayList<AConversionRule>();
        rules.add(new RestyleBoutonFermer());
        rules.add(new RestyleForm());        
        rules.add(new CompositeRule().addRule(new ChangeObjectType("TEdit", "TColoredEdit")).addRule(new AddInclude(CppFile.HEADER, "ColoredEdit.h")));
        rules.add(new CompositeRule().addRule(new ChangeObjectType("TMaskEdit", "TColoredMaskEdit")).addRule(new AddInclude(CppFile.HEADER, "ColoredMaskEdit.h")));
        rules.add(new CompositeRule().addRule(new ChangeObjectType("TComboBox", "TComboBoxEx")).addRule(new AddInclude(CppFile.HEADER, "ComboBoxEx.h")));
        rules.add(new CompositeRule().addRule(new ChangeObjectType("TComboBox98", "TComboBoxEx")).addRule(new AddInclude(CppFile.HEADER, "ComboBoxEx.h")));
        rules.add(new ChangePropertyValue("TColoredEdit", "Height", EDIT_BOX_HEIGHT));
        rules.add(new ChangePropertyValue("TColoredMaskEdit", "Height", EDIT_BOX_HEIGHT));
        rules.add(new ChangePropertyValue("TPanel", "ParentColor", "True", new PropertyValueIsNullOrEquals("Color", "clBtnFace")));

        for (AConversionRule rule : rules) {
            rule.apply(dfmObject, cppClass);
        }

        // Recursively apply to children
        for (DfmObject childObject : dfmObject) {
            updateDfmObjects(childObject, cppClass);
        }
    }

    void updateCppCode(DfmObject dfmObject, CppClass cppClass) {
        
        ArrayList<AConversionRule> rules = new ArrayList<AConversionRule>();
        rules.add(new CompositeRule().addRule(new ChangeBaseClass("TFormExtented")).addRule(new AddInclude(CppFile.HEADER, "def_tform.h")));
        rules.add(new RemoveLineOfCode("On empeche la fenetre de se d�placer"));
        rules.add(new RemoveLineOfCode("GetSystemMenu(Handle"));
        rules.add(new RemoveLineOfCode("RemoveMenu"));
        rules.add(new RemoveLineOfCode("center_win"));
        rules.add(new RemoveLineOfCode("wPrinc->Width/2"));        

        for (AConversionRule rule : rules) {
            rule.apply(dfmObject, cppClass);
        }
    }
    
    private void ProcessDfm(File dfmFile) {
        try {
            log.info("Processing " + dfmFile.getAbsolutePath());
            DfmReaderWriter dfmReaderWriter = new DfmReaderWriter();
            CppClassReaderWriter cppReaderWriter = new CppClassReaderWriter();
            DfmObject dfmRoot = dfmReaderWriter.read(dfmFile);
            File headerFile = Utils.replaceExtension(dfmFile, "h");
            File cppFile = Utils.replaceExtension(dfmFile, "cpp");
            CppClass cppClass = cppReaderWriter.read(headerFile, cppFile);            
            
            updateDfmObjects(dfmRoot, cppClass);            
            updateCppCode(dfmRoot, cppClass);
            
            dfmReaderWriter.write(dfmFile, dfmRoot);
            cppReaderWriter.write(cppClass, headerFile, cppFile);
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (InterruptedException e) {
            log.severe(e.getMessage());
        } catch (DfmReaderWriterException e) {
            log.severe(e.getMessage());
        } catch (CppClassReaderWriterException e) {
            log.severe(e.getMessage());
        }
    }
}
