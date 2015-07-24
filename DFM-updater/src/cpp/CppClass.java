package cpp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CppClass {
    private String cppBody;
    private String cppHeader;
    private String className;
    private String baseClassName;

    public enum CppFile {
        HEADER, BODY
    };

    public CppClass(String aCppHeader, String aCppBody) throws CppClassReaderWriterException {
        cppBody = aCppBody;
        cppHeader = aCppHeader;
        parseClassInfo();
    }

    public boolean includesHeader(CppFile cppFile, String header) {
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $
        // matchent les débuts et fins de chaque ligne
        // (?i) active l'insensibilité à la casse
        header = StringUtils.strip(header, "\"<>");
        Pattern p = Pattern.compile(String.format("(?m)^\\s*#include\\s+[<\"](?i)%s(?-i)[>\"]$", header));
        Matcher m = p.matcher(cppFile == CppFile.HEADER ? cppHeader : cppBody);
        return m.find();
    }

    public boolean addIncludeHeader(CppFile cppFile, String header) {
        if (includesHeader(cppFile, header))
            return true;

        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $
        // matchent les débuts et fins de chaque ligne
        Pattern p = Pattern.compile("(?m)^\\s*#include\\s+[<\"]\\S+[>\"]$");
        Matcher m = p.matcher(cppFile == CppFile.HEADER ? cppHeader : cppBody);
        int endOfLastHeader = 0;
        while (m.find()) {
            endOfLastHeader = m.end();
        }

        header = header.trim();
        if (!header.startsWith("<") && !header.startsWith("\""))
            header = String.format("\"%s\"", header);

        if (cppFile == CppFile.HEADER)
            cppHeader = Utils.replaceSubString(cppHeader, endOfLastHeader, endOfLastHeader, String.format("\r\n#include %s", header));
        else
            cppBody = Utils.replaceSubString(cppBody, endOfLastHeader, endOfLastHeader, String.format("\r\n#include %s", header));

        return true;
    }

    public boolean replace(CppFile cppFile, String oldString, String newString) {
        if (cppFile == CppFile.HEADER)
            cppHeader = cppHeader.replace(oldString, newString);
        else
            cppBody = cppBody.replace(oldString, newString);

        return true;
    }

    public String getCppBody() {
        return cppBody;
    }

    public String getCppHeader() {
        return cppHeader;
    }

    Matcher getClassNameAndTypeMatcher() {
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $
        // matchent les débuts et fins de chaque ligne
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
        // dans la regexp, (?m) active le mode multiligne, pour que ^ et $
        // matchent les débuts et fins de chaque ligne
        String regEx = String.format("(?m) *(void.*%s *:: *%s\\(.*\\))", className, methodName);
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(cppBody);
        return m;
    }

    public boolean methodExists(String methodName) {
        return getCppBodyMethodMatcher(methodName).find();
    }

    public enum Visibility {
        UNKNOWN(""), PRIVATE("private"), PROTECTED("protected"), PUBLIC("public"), PUBLISHED("__published");

        String value;

        Visibility(String aValue) {
            this.value = aValue;
        }

        static public Visibility getEnum(String value) {
            for (Visibility val : values()) {
                if (val.toString().compareTo(value) == 0)
                    return val;
            }
            return UNKNOWN;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.getValue();
        }
    };

    private int findEndOfVisibilitySection(String cppHeader, int from) {
        int endOfSection = -1;
        int nestedLevel = 0;
        for (int i = from; i < cppHeader.length() && endOfSection == -1; ++i) {
            switch (cppHeader.charAt(i)) {
            case '{':
            nestedLevel++;
                break;
            case '}':
            if (nestedLevel > 0)
                nestedLevel--;
            else
                endOfSection = i;
                break;
            case ':':
            endOfSection = i;
            while (endOfSection > 0 && cppHeader.charAt(endOfSection) != '\n') {
                endOfSection--;
            }
            String section = cppHeader.substring(endOfSection, i).trim();
            if (Visibility.getEnum(section) == Visibility.UNKNOWN)
                endOfSection = -1;
                break;
            }
        }

        return endOfSection;
    }

    public void doAddMethodToCppHeader(Visibility visibility, String callConvention, String methodName, String parameters) throws CppClassReaderWriterException {

        parameters = StringUtils.strip(parameters, "()");
        if (!callConvention.isEmpty())
            callConvention += " ";
        String methodSignature = String.format("    void %s%s(%s);", callConvention, methodName, parameters);

        doAddToHeader(visibility, methodSignature, true);
    }

    public void doAddMethodToCppBody(String callConvention, String methodName, String parameters, String body) throws CppClassReaderWriterException {
        if (!callConvention.isEmpty())
            callConvention += " ";
        String methodBody = String.format("\r\nvoid %s%s::%s(%s) {\r\n%s\r\n}\r\n", callConvention, className, methodName, parameters, body);
        cppBody += methodBody;
    }

    public void doAddMethod(Visibility visibility, String callConvention, String methodName, String parameters, String body)
            throws CppClassReaderWriterException {
        doAddMethodToCppHeader(visibility, callConvention, methodName, parameters);
        doAddMethodToCppBody(callConvention, methodName, parameters, body);
    }

    public void addMethod(Visibility visibility, String callConvention, String methodName, String parameters, String body) throws CppClassReaderWriterException {
        if (methodExists(methodName))
            throw new CppClassReaderWriterException(String.format("La méthode %s::%s existe déjà", className, methodName));
        doAddMethod(visibility, callConvention, methodName, parameters, body);
    }

    public void doAppendToMethodBody(String methodName, String instructions) throws CppClassReaderWriterException {
        Matcher m = getCppBodyMethodMatcher(methodName);
        if (m.find()) {
            int bodyStart = findNextOpeningBracket(cppBody, m.end());
            int bodyEnd = findClosingBracket(cppBody, bodyStart + 1);
            String currentBody = cppBody.substring(bodyStart + 1, bodyEnd).trim();
            instructions = instructions.trim();
            if (!currentBody.contains(instructions)) {
                cppBody = Utils.replaceSubString(cppBody, bodyStart + 1, bodyEnd, "\r\n    " + currentBody + "\r\n\r\n    " + instructions + "\r\n");
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
        if (!methodExists(methodName))
            throw new CppClassReaderWriterException(String.format("La méthode %s::%s n'existe pas", className, methodName));
        doAppendToMethodBody(methodName, instructions);
    }

    public void createMethodOrAppendTo(Visibility visibility, String callConvention, String methodName, String parameters, String body, String bodyPrefix)
            throws CppClassReaderWriterException {
        if (methodExists(methodName))
            doAppendToMethodBody(methodName, body);
        else
            doAddMethod(visibility, callConvention, methodName, parameters, bodyPrefix + "\r\n" + body);
    }

    public void appendToApplyStyleMethod(String instructions) throws CppClassReaderWriterException {
        createMethodOrAppendTo(Visibility.PUBLIC, "", "ApplyStyle", "bool useLegacyUI", instructions, "    TFormExtented::ApplyStyle(useLegacyUI);");
    }

    public void changeBaseClass(String newBaseClass) throws CppClassReaderWriterException {
        if (baseClassName.compareTo(newBaseClass) == 0)
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

    public Matcher getMemberVariableMatcher(String variableName) {
        Pattern p = Pattern.compile(String.format("(?m)^\\s*(\\S*)\\s*\\*?(%s)\\s*;$", variableName));
        return p.matcher(cppHeader);
    }

    public String getMemberVariableType(String variableName) throws CppClassReaderWriterException {
        Matcher m = getMemberVariableMatcher(variableName);
        if (!m.find()) {
            throw new CppClassReaderWriterException(String.format("Failed to find variable '%s' declaration", variableName));
        }

        return cppHeader.substring(m.start(1), m.end(1));
    }

    public void changeMemberVariableType(String variableName, String newTypeName) throws CppClassReaderWriterException {
        Matcher m = getMemberVariableMatcher(variableName);
        if (!m.find()) {
            throw new CppClassReaderWriterException(String.format("Failed to find variable '%s' declaration", variableName));
        }

        cppHeader = Utils.replaceSubString(cppHeader, m.start(1), m.end(1), newTypeName);
    }

    private Matcher getLineOfCodeMatcher(String cppCode, String regex) {
        String lineRegex = String.format("(?m)^%s$", regex);
        Pattern p = Pattern.compile(lineRegex);
        return p.matcher(cppCode);
    }

    public boolean containsLineOfCode(String regex) {
        Matcher m = getLineOfCodeMatcher(cppBody, regex);
        return m.find();
    }

    public void removeLineOfCode(CppFile cppFile, String regex) {
        String cppCode = cppFile == CppFile.HEADER ? cppHeader : cppBody;
        Matcher m = getLineOfCodeMatcher(cppCode, regex);
        while (m.find()) {
            cppCode = Utils.replaceSubString(cppCode, m.start(), m.end() + 2, "");
            m = getLineOfCodeMatcher(cppCode, regex);
        }

        if (cppFile == CppFile.HEADER)
            cppHeader = cppCode;
        else
            cppBody = cppCode;
    }

    public void addVariable(Visibility visibility, String variableType, String variableName) throws CppClassReaderWriterException {
        if (variableExists(variableName))
            throw new CppClassReaderWriterException(String.format("La variable %s::%s existe déjà", className, variableName));
        doAddVariable(visibility, variableType, variableName);
    }

    private void doAddToHeader(Visibility visibility, String declaration, boolean addLast) throws CppClassReaderWriterException {
        Pattern p = Pattern.compile(String.format("(?m)^ *%s:.*$", visibility.toString()));
        Matcher m = p.matcher(cppHeader);
        if (m.find()) {
            int insertPos = m.end() + 1;
            if (addLast)
                insertPos = findEndOfVisibilitySection(cppHeader, m.end());
            cppHeader = cppHeader.substring(0, insertPos) + "    " + declaration + cppHeader.substring(insertPos - 1);
        } else {
            throw new CppClassReaderWriterException(String.format("Unable to locate a place to add declaration %s to class %s", declaration, className));
        }
    }

    private void doAddVariable(Visibility visibility, String variableType, String variableName) throws CppClassReaderWriterException {
        String variableDeclaration = String.format("    %s %s;", variableType, variableName);
        doAddToHeader(visibility, variableDeclaration, false);
    }

    private boolean variableExists(String variableName) {
        String regEx = String.format("(?m)\\Q%s\\E[\\s*]", variableName);
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(cppHeader);
        return m.matches();
    }

}
