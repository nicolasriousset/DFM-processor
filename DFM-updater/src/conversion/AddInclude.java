package conversion;

import conversion.condition.IConversionCondition;
import cpp.CppClass;
import cpp.CppClass.CppFile;
import dfm.DfmObject;

public class AddInclude extends AConversionRule {
    String newHeader;
    CppFile dest;
    IConversionCondition condition;
    
    public AddInclude(CppFile aDest, String aNewHeader) {
        newHeader = aNewHeader;
        dest = aDest;
    }
    
    public AddInclude(CppFile aDest, String aNewHeader, IConversionCondition aCondition) {
        newHeader = aNewHeader;
        dest = aDest;
        condition = aCondition;
    }
    
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
    	if (condition != null && !condition.isVerified(dfmObject, cppClass))
    		return false;
    	
        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        return cppClass.addIncludeHeader(dest, newHeader);
    }

}
