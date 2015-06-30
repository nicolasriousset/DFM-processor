package conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CppClass {
    private String cppBody;
    private String cppHeader;
    private String className;
    private String baseClassName;
    
    public enum CppFile {HEADER, BODY};

    public CppClass(String aCppHeader, String aCppBody) throws CppClassReaderWriterException {
        cppBody = aCppBody;
        cppHeader = aCppHeader;
        parseClassInfo();
    }

    public boolean includesHeader(CppFile cppFile, String header) {
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $ matchent les débuts et fins de chaque ligne
        // (?i) active l'insensibilité à la casse
        header = StringUtils.strip(header, "\"<>");
        Pattern p = Pattern.compile(String.format("(?m)^\\s*#include\\s+[<\"](?i)%s(?-i)[>\"]$", header));
        Matcher m = p.matcher(cppFile == CppFile.HEADER ? cppHeader : cppBody);
        return m.find();
    }
    
    public boolean addHeader(CppFile cppFile, String header) {
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $ matchent les débuts et fins de chaque ligne
        header = StringUtils.strip(header, "\"<>");
        Pattern p = Pattern.compile("(?m)^\\s*#include\\s+[<\"]\\S+[>\"]$");
        Matcher m = p.matcher(cppFile == CppFile.HEADER ? cppHeader : cppBody);
        int endOfLastHeader = 0;
        while (m.find()) {
            endOfLastHeader = m.end();
        }
        
        if (cppFile == CppFile.HEADER)
            cppHeader = Utils.replaceSubString(cppHeader, endOfLastHeader, endOfLastHeader, String.format("\r\n#include \"%s\"", header));
        else
            cppBody = Utils.replaceSubString(cppBody, endOfLastHeader, endOfLastHeader, String.format("\r\n#include \"%s\"", header));
        
        return true;
    }

    public String getCppBody() {
        return cppBody;
    }

    public String getCppHeader() {
        return cppHeader;
    }

    Matcher getClassNameAndTypeMatcher() {
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $ matchent les débuts et fins de chaque ligne
        Pattern p = Pattern.compile("(?m)^ *class ([^ ]+) *: public (.*)$");
        return p.matcher(cppHeader);
    }

    void parseClassInfo() throws CppClassReaderWriterException {
        Matcher m = getClassNameAndTypeMatcher();
        if (m.find()) {
            className = m.group(1);
            baseClassName = m.group(2);
        } else {
            throw new CppClassReaderWriterException("Unable to identify class name");
        }
    }

    public Matcher getCppBodyMethodMatcher(String methodName) {
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $ matchent les débuts et fins de chaque ligne
        String regEx = String.format("(?m) *(void.*%s *:: *%s\\(.*\\))", className, methodName);
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(cppBody);
        return m;
    }

    public boolean containsMethod(String methodName) {
        return getCppBodyMethodMatcher(methodName).find();
    }

    public void doAddMethodToCppHeader(String methodName, String parameters) throws CppClassReaderWriterException {
        Pattern p = Pattern.compile("(?m)^ *public:.*$");
        Matcher m = p.matcher(cppHeader);
        if (m.find()) {
            int insertPos = m.end();
            StringUtils.strip(parameters, "()");
            String methodSignature = String.format("\r\n    void %s(%s);", methodName, parameters);
            cppHeader = cppHeader.substring(0, insertPos) + methodSignature + cppHeader.substring(insertPos);
        } else {
            throw new CppClassReaderWriterException(String.format("Unable to locate a palce to insert method %s in class %s" + methodName, className));
        }
    }

    public void doAddMethodToCppBody(String methodName, String parameters, String body) throws CppClassReaderWriterException {
        String methodBody = String.format("\r\nvoid %s::%s(%s) {\r\n%s\r\n}", className, methodName, parameters, body);
        cppBody += methodBody;
    }

    public void doAddMethod(String methodName, String parameters, String body) throws CppClassReaderWriterException {
        doAddMethodToCppHeader(methodName, parameters);
        doAddMethodToCppBody(methodName, parameters, body);
    }

    public void addMethod(String methodName, String parameters, String body) throws CppClassReaderWriterException {
        if (containsMethod(methodName))
            throw new CppClassReaderWriterException(String.format("La méthode %s::%s existe déjà", className, methodName));
        doAddMethod(methodName, parameters, body);
    }

    public void doAppendToMethodBody(String methodName, String instructions) throws CppClassReaderWriterException {
        Matcher m = getCppBodyMethodMatcher(methodName);
        if (m.find()) {
            int bodyStart = findNextOpeningBracket(cppBody, m.end());
            int bodyEnd = findClosingBracket(cppBody, bodyStart + 1);
            String currentBody = cppBody.substring(bodyStart + 1, bodyEnd).trim();
            instructions = instructions.trim();
            if (!currentBody.contains(instructions)) {
                cppBody = Utils.replaceSubString(cppBody, bodyStart + 1, bodyEnd, "\r\n    " + currentBody + "\r\n    " + instructions.trim() + "\r\n");
            }
        } else {
            throw new CppClassReaderWriterException(String.format("Unable to find the method %s in class %s" + methodName, className));
        }
    }

    private int findNextOpeningBracket(String cppBody, int from) {
        int nextOpeningBracket = -1;
        for (int i = from; i < cppBody.length() && nextOpeningBracket == -1; ++i) {
            if (Character.isWhitespace(cppBody.charAt(i)))
                continue;
            if (cppBody.charAt(i) == '{')
                nextOpeningBracket = i;
        }
        return nextOpeningBracket;
    }

    private int findClosingBracket(String cppBody, int from) {
        int closingBracket = -1;
        int nestingLevel = 1;
        for (int i = from; i < cppBody.length() && nestingLevel > 0 && closingBracket == -1; ++i) {
            if (cppBody.charAt(i) == '{') {
                nestingLevel++;
            } else if (cppBody.charAt(i) == '}') {
                nestingLevel--;
                if (nestingLevel == 0) {
                    closingBracket = i;
                }
            }
        }
        return closingBracket;
    }

    public void appendToMethod(String methodName, String instructions) throws CppClassReaderWriterException {
        if (!containsMethod(methodName))
            throw new CppClassReaderWriterException(String.format("La méthode %s::%s n'existe pas", className, methodName));
        doAppendToMethodBody(methodName, instructions);
    }

    public void addOrAppendToMethod(String methodName, String parameters, String body, String bodyPrefix) throws CppClassReaderWriterException {
        if (containsMethod(methodName))
            doAppendToMethodBody(methodName, body);
        else
            doAddMethod(methodName, parameters, bodyPrefix + "\r\n" + body);
    }

    public void appendToApplyStyleMethod(String instructions) throws CppClassReaderWriterException {
        addOrAppendToMethod("ApplyStyle", "bool useLegacyUI", instructions, "    TFormExtented::ApplyStyle(useLegacyUI);");
    }

    public void renameBaseClass(String newBaseClass) throws CppClassReaderWriterException {
        if (baseClassName == newBaseClass)
            return;
        
        // Modification du type de base dans le header C++
        Matcher m = getClassNameAndTypeMatcher();
        if (!m.find())
            throw new CppClassReaderWriterException("Unable to identify class name");

        final int baseClassGroup = 2;
        int baseClassStart = m.start(baseClassGroup);
        int baseClassEnd = m.end(baseClassGroup);
        cppHeader = Utils.replaceSubString(cppHeader, baseClassStart, baseClassEnd, newBaseClass);
        
        // Modification de l'appel au constructeur hérité dans le body C++
        Pattern p = Pattern.compile(String.format("(?m)^.*%s\\s*::\\s*%s\\(.*\\)\\s*:\\s*(\\w*)", className, className));
        m = p.matcher(cppBody);
        if (m.find()) {
            baseClassStart = m.start(1);
            baseClassEnd = m.end(1);
            cppBody = Utils.replaceSubString(cppBody, baseClassStart, baseClassEnd, newBaseClass);
        }
        
        

    }
    
    public String getBaseClassName() {
        return baseClassName;
    }

    public String getClassName() {
        return className;
    }
    
}
