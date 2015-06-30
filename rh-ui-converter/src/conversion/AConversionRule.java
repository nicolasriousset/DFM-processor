package conversion;

import java.util.logging.Logger;

import cpp.CppClass;
import dfm.DfmObject;


public abstract class AConversionRule {
    static Logger log = Logger.getLogger(AConversionRule.class.getName());
    
    public boolean apply(DfmObject dfmObject, CppClass cppClass) {
        if (!isApplicable(dfmObject, cppClass))
            return false;

        log.info("Applying " + this.getClass().getName());
        return doApply(dfmObject, cppClass);
    }

    abstract public boolean isApplicable(DfmObject dfmObject, CppClass cppClass);

    abstract protected boolean doApply(DfmObject dfmObject, CppClass cppClass);

}
