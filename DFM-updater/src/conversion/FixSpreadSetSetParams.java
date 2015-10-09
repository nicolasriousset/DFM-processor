package conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpp.CppClass;
import cpp.Utils;
import dfm.DfmObject;

public class FixSpreadSetSetParams extends AConversionRule {
    // Le troisième paramètre de SetText doit être converti en Variant 
    private final String REGEX = "-> ?SetText\\((.*),(.*),(?!Variant)(.*)\\)";    
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(cppClass.getCppBody());
        return m.find();
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        Pattern p = Pattern.compile(REGEX);
        String cppCode = cppClass.getCppBody();
        Matcher m = p.matcher(cppCode);
        while (m.find()) {
            String newCode = String.format("->SetText(%1s, %2s,Variant(%3s))", m.group(1), m.group(2), m.group(3));
            cppCode = Utils.replaceSubString(cppCode, m.start(), m.end(), newCode);
            m = p.matcher(cppCode);
        }
        
        cppClass.setCppBody(cppCode);
        return true;
    }

}
