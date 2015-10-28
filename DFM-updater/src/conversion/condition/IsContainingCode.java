package conversion.condition;

import cpp.CppClass;
import dfm.DfmObject;

public class IsContainingCode implements IConversionCondition {
    String codeRegEx;
    
    public IsContainingCode(String codeRegEx) {
        this.codeRegEx = codeRegEx;
    }

	@Override
	public boolean isVerified(DfmObject dfmObject, CppClass cppClass) {
		return cppClass.containsCode(codeRegEx);
	}

}
