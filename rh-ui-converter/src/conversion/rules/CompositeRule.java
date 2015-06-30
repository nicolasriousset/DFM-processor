package conversion.rules;

import java.util.ArrayList;

import main.DfmObject;
import conversion.CppClass;

public class CompositeRule extends AConversionRule {
    ArrayList<AConversionRule> rules;
    
    public CompositeRule() {
        rules = new ArrayList<AConversionRule>();
    }
    
    public CompositeRule addRule(AConversionRule aRule) {
        rules.add(aRule);
        return this;
    }
    
    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        for (AConversionRule rule : rules) {
            if (!rule.isApplicable(dfmObject, cppClass))
                return false;
        }
        return true;
    }

    @Override
    protected boolean doApply(DfmObject dfmObject, CppClass cppClass) {
        for (AConversionRule rule : rules) {
            if (!rule.doApply(dfmObject, cppClass))
                return false;
        }
        return true;
    }

}
