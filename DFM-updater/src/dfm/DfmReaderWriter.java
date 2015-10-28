package dfm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import cpp.Utils;

public class DfmReaderWriter {
    static final String TAG_OBJECT_START = "object";
    static final String TAG_OBJECT_END   = "end";

    static Logger       log              = Logger.getLogger(DfmReaderWriter.class.getName());
    DfmObject           rootObject;
    DfmObject           currentObject;
    String              currentTag;
    StringBuilder       currentValue;
    ParsingState        state            = ParsingState.WAITING_OBJECT;

    enum ParsingState {
        UNKNOWN, WAITING_OBJECT, READING_OBJECT, READING_MULTILINE_DATA, READING_MULTILINE_STRING
    };

    public DfmObject read(File dfmFile) throws InterruptedException, IOException, DfmReaderWriterException {
        File txtFile = dfmToTxt(dfmFile);
        currentValue = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(txtFile.getAbsoluteFile()))) {

            String line;
            state = ParsingState.WAITING_OBJECT;

            while ((line = br.readLine()) != null) {
                parseLine(line);
            }
        }

        return rootObject;
    }

    ArrayList<String> parseTokens(String line, String delimitersRegex, int maxTokens) {
        String[] tokens = line.split(delimitersRegex, maxTokens);
        ArrayList<String> prunedTokens = new ArrayList<String>();
        for (String token : tokens) {
            token = token.trim();
            if (!token.isEmpty())
                prunedTokens.add(token);
        }

        return prunedTokens;
    }

    void parseObjectStart(String line) throws DfmReaderWriterException {
        ArrayList<String> tokens = parseTokens(line.trim(), "[ \\:]", 3);

        if (tokens.size() == 0)
            return;

        currentTag = tokens.get(0);
        if (currentTag.compareToIgnoreCase(TAG_OBJECT_START) != 0) {
            throw new DfmReaderWriterException("La ligne ne commençait pas par 'object'  : " + line);
        }

        if (tokens.size() < 3) {
            throw new DfmReaderWriterException("La ligne est incorrectement formatée  : " + line);
        }

        DfmObject parentObject = currentObject;
        currentObject = new DfmObject();
        if (rootObject == null) {
            rootObject = currentObject;
        }
        if (parentObject != null) {
            parentObject.addChild(currentObject);
        }

        currentObject.setName(tokens.get(1));
        currentObject.setTypeName(tokens.get(2));
        state = ParsingState.READING_OBJECT;
    }

    void parseObjectProperty(String line) throws DfmReaderWriterException {
        ArrayList<String> tokens = parseTokens(line, "[=\\:]", 2);
        if (tokens.size() == 0) {
            return;
        }
        
        if (tokens.get(0).startsWith(TAG_OBJECT_START + " ")) {
        	currentTag = tokens.get(0);
            parseObjectStart(line);
        } else if (tokens.get(0).compareToIgnoreCase(TAG_OBJECT_END) == 0) {
        	currentTag = tokens.get(0);
            currentObject = currentObject.getParent();
            state = ParsingState.READING_OBJECT;
        } else if (tokens.size() == 2) {
        	currentTag = tokens.get(0);
            String value = tokens.get(1);
            if (value.startsWith("{") || value.startsWith("(")) {
                state = value.startsWith("{") ? ParsingState.READING_MULTILINE_DATA : ParsingState.READING_MULTILINE_STRING;
                currentValue.setLength(0);
                currentValue.append(value);
            } else {
                currentObject.properties().put(currentTag, value);
            }
        } else if (tokens.size() == 1) {
        	if (tokens.get(0).startsWith("\'")) {
        		// Ce n'set pas un nouveau tag, mais une string sur plusieurs lignes
        		currentObject.properties().put(currentTag, currentObject.properties().get(currentTag) + tokens.get(0));
        	} else {
        		currentTag = tokens.get(0);
        		currentObject.properties().put(currentTag, "");
        	}
        } else {
            throw new DfmReaderWriterException("Incapable de traiter la ligne : " + line);
        }
    }

    void parseMultilineProperty(String line, String endOfPropertyDelim) {
        currentValue.append("\r\n");
        currentValue.append(line.trim());
        if (line.endsWith(endOfPropertyDelim)) {
            currentObject.properties().put(currentTag, currentValue.toString());
            currentValue.setLength(0);
            state = ParsingState.READING_OBJECT;
        }
    }

    void parseLine(String line) throws DfmReaderWriterException {
        switch (state) {
        case WAITING_OBJECT:
        parseObjectStart(line);
            break;
        case READING_OBJECT:
        parseObjectProperty(line);
            break;
        case READING_MULTILINE_STRING:
        parseMultilineProperty(line, ")");
            break;
        case READING_MULTILINE_DATA:
        parseMultilineProperty(line, "}");
            break;
        default:
        throw new DfmReaderWriterException("Etat inconnu : " + state.toString());
        }
    }

    private File convert(File inputFile) throws InterruptedException, IOException, DfmReaderWriterException {
        String extension = FilenameUtils.getExtension(inputFile.getAbsolutePath());
        File outputFile;
        if (extension.endsWith("txt"))
            outputFile = Utils.replaceExtension(inputFile, "dfm");
        else if (extension.endsWith("dfm"))
            outputFile = Utils.replaceExtension(inputFile, "txt");
        else
            throw new DfmReaderWriterException("Unsupported extension for " + inputFile.getAbsolutePath());
        FileUtils.deleteQuietly(outputFile);
        Process process = new ProcessBuilder("C:\\Borland\\CBuilder6\\Bin\\convert.exe", inputFile.getAbsolutePath()).start();
        process.waitFor();
        return outputFile;
    }

    private File dfmToTxt(File dfmFile) throws InterruptedException, IOException, DfmReaderWriterException {
        return convert(dfmFile);
    }

    private File txtToDfm(File txtFile) throws InterruptedException, IOException, DfmReaderWriterException {
        return convert(txtFile);
    }

    private void write(FileWriter writer, DfmObject rootObject, int indentLevel) throws IOException {
        String indent = StringUtils.repeat("  ", indentLevel);
        String line = String.format("%s%s %s : %s%n", indent, TAG_OBJECT_START, rootObject.getName(), rootObject.getTypeName());
        writer.write(line);

        // Writing properties
        for (String propertyName : rootObject.properties().keySet()) {
            line = String.format("%s  %s = %s%n", indent, propertyName, rootObject.properties().get(propertyName));
            writer.write(line);
        }

        // Writing child objects
        for (DfmObject child : rootObject) {
            write(writer, child, indentLevel + 1);
        }

        line = String.format("%s%s%n", indent, TAG_OBJECT_END);
        writer.write(line);
    }

    public void write(File dfmFile, DfmObject rootObject) throws InterruptedException, IOException, DfmReaderWriterException {
        File txtFile = new File(FilenameUtils.removeExtension(dfmFile.getAbsolutePath()) + ".txt");
        try (FileWriter writer = new FileWriter(txtFile.getAbsoluteFile())) {
            write(writer, rootObject, 0);
        }

        txtToDfm(txtFile);
    }

}
