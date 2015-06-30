package dfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DfmObject implements Iterable<DfmObject> {
    String name;
    String typeName;
    ArrayList<DfmObject> children;
    DfmObject parent;
    LinkedHashMap<String, String> properties;

    public DfmObject() {
        children = new ArrayList<DfmObject>();
        properties = new LinkedHashMap<String, String>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String aTypeName) {
        typeName = aTypeName;
    }

    public void addChild(DfmObject aChild)
    {
        children.add(aChild);
        aChild.parent = this;
    }
    
    public DfmObject getParent()
    {
        return parent;
    }

    @Override
    public Iterator<DfmObject> iterator() {
        return children.iterator();
    }
    
    public HashMap<String, String> getProperties()
    {
        return properties;
    }    
    
}
