package conversion;

import java.util.ArrayList;

import com.google.common.base.Joiner;

import cpp.CppClass;
import dfm.DfmObject;
import dfm.DfmObject.Direction;

public class RestyleBevel extends AConversionRule {

    @Override
    public boolean isApplicable(DfmObject dfmObject, CppClass cppClass) {
        return dfmObject.isInstanceOf("TBevel");
    }

    @Override
    protected boolean doApply(DfmObject bevel, CppClass cppClass) {
        ArrayList<String> anchorsList = new ArrayList<String>();
        anchorsList.add("akLeft");
        anchorsList.add("akRight");
        if (!bevel.hasNeighbour(Direction.DOWN, "TPanel"))
            anchorsList.add("akBottom");
        String anchors = "[" + Joiner.on(",").join(anchorsList) + "]";
        bevel.properties().put("Anchors", anchors);

        return true;
    }
}
