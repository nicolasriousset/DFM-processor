package dfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DfmObject implements Iterable<DfmObject> {
    String                        name;
    String                        typeName;
    ArrayList<DfmObject>          children;
    DfmObject                     parent;
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

    public void addChild(DfmObject aChild) {
        children.add(aChild);
        aChild.parent = this;
    }

    public DfmObject getParent() {
        return parent;
    }

    public DfmObject getRoot() {
        DfmObject root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }
    
    @Override
    public Iterator<DfmObject> iterator() {
        return children.iterator();
    }

    public HashMap<String, String> properties() {
        return properties;
    }

    public boolean isInstanceOf(String typeName) {
        if (getTypeName() == null || typeName == null)
            return false;

        return getTypeName().compareTo(typeName) == 0;
    }

    public void removeChild(int index) {
        children.remove(index);
    }

    public int getChildrenCount() {
        return children.size();
    }

    public DfmObject getChild(int index) {
        return children.get(index);
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    };

    public boolean isLeftOf(DfmObject neighbour) {
        if (this == neighbour)
            return false;
        
        int myLeft = Integer.parseInt(properties.get("Left"));
        int neighbourLeft = Integer.parseInt(neighbour.properties.get("Left"));
        return myLeft < neighbourLeft;
    }

    public boolean isRightOf(DfmObject neighbour) {
        if (this == neighbour)
            return false;

        int myLeft = Integer.parseInt(properties.get("Left"));
        int neighbourLeft = Integer.parseInt(neighbour.properties.get("Left"));
        return myLeft > neighbourLeft;        
    }

    public boolean isAbove(DfmObject neighbour) {
        if (this == neighbour)
            return false;

        int myTop = Integer.parseInt(properties.get("Top"));
        int neighbourTop = Integer.parseInt(neighbour.properties.get("Top"));
        return myTop < neighbourTop;        
    }

    public boolean isBelow(DfmObject neighbour) {
        if (this == neighbour)
            return false;

        int myTop = Integer.parseInt(properties.get("Top"));
        int neighbourTop = Integer.parseInt(neighbour.properties.get("Top"));
        return myTop > neighbourTop;
    }

    public boolean hasNeighbour(Direction neighbourDirection, String neighbourType) {
        if (getParent() == null)
            return false;

        for (DfmObject neighbour : getParent()) {
            if (neighbour.isInstanceOf(neighbourType)) {
                if (neighbourDirection == Direction.UP && neighbour.isAbove(this))
                    return true;
                if (neighbourDirection == Direction.DOWN && neighbour.isBelow(this))
                    return true;
                if (neighbourDirection == Direction.LEFT && neighbour.isLeftOf(this))
                    return true;
                if (neighbourDirection == Direction.RIGHT && neighbour.isRightOf(this))
                    return true;
            }
        }
        return false;
    }
    
    public boolean isCloseToRight() {
        if (getParent() == null)
            return false;
        
        int myHCenter = Integer.parseInt(properties.get("Left")) + Integer.parseInt(properties.get("Width")) / 2;
        String parentWidth = getParent().properties().get("ClientWidth");
        if (parentWidth == null)
            parentWidth = getParent().properties().get("Width");
        if (parentWidth == null)
            return false;
        int parentRightLimit = (int)(0.75 * (double)Integer.parseInt(parentWidth));
        
        return myHCenter > parentRightLimit; 
    }
    
    public boolean isCloseToLeft() {
        if (getParent() == null)
            return false;
        
        int myHCenter = Integer.parseInt(properties.get("Left")) + Integer.parseInt(properties.get("Width")) / 2;
        String parentWidth = getParent().properties().get("ClientWidth");
        if (parentWidth == null)
            parentWidth = getParent().properties().get("Width");
        if (parentWidth == null)
            return false;
        int parentLeftLimit = (int)(0.25 * (double)Integer.parseInt(parentWidth));
        
        return myHCenter < parentLeftLimit; 
    }

    public boolean isCloseToTop() {
        if (getParent() == null)
            return false;
        
        int myVCenter = Integer.parseInt(properties.get("Top")) + Integer.parseInt(properties.get("Height")) / 2;
        String parentHeight = getParent().properties().get("ClientHeight");
        if (parentHeight == null)
            parentHeight = getParent().properties().get("Height");
        if (parentHeight == null)
            return false;
        int parentTopLimit = (int) (0.25 * (double)Integer.parseInt(parentHeight));
        
        return myVCenter < parentTopLimit;         
    }
    
    public boolean isCloseToBottom() {
        if (getParent() == null)
            return false;
        
        int myVCenter = Integer.parseInt(properties.get("Top")) + Integer.parseInt(properties.get("Height")) / 2;
        String parentHeight = getParent().properties().get("ClientHeight");
        if (parentHeight == null)
            parentHeight = getParent().properties().get("Height");
        if (parentHeight == null)
            return false;
        int parentBottomLimit = (int) (0.75 * (double)Integer.parseInt(parentHeight));
        
        return myVCenter > parentBottomLimit;         
    }

    public boolean propertieEqualsTo(String propName, String propValue) {
        String val = properties.get(propName);
        if (val == null)
            return false;
        
        return val.compareTo(propValue) == 0;
    }
}
