package edu.UTD.ComputerScience.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The gameplay consists of , to put dashes to close the box .
 * This class represents such a line
 */
public class Stroke {

    /* ID of the stroke */
    private Box boxAbove;
    private Box boxBelow;
    private Box boxLeft;
    private Box boxRight;

    /* Box List*/
    private List<Box> boxList = new ArrayList<Box>();

    private Player owner;

    public Stroke(Box boxAbove, Box boxBelow,
            Box boxLeft, Box boxRight) {

        this.boxAbove = boxAbove;
        this.boxBelow = boxBelow;
        this.boxLeft = boxLeft;
        this.boxRight = boxRight;

        if (boxAbove != null)
            boxList.add(boxAbove);

        if (boxBelow != null)
            boxList.add(boxBelow);

        if (boxLeft != null)
            boxList.add(boxLeft);

        if (boxRight != null)
            boxList.add(boxRight);
    }

    public Box getBoxAbove() {
        return boxAbove;
    }

    public Box getBoxBelow() {
        return boxBelow;
    }

    public Box getBoxLeft() {
        return boxLeft;
    }

    public Box getBoxRight() {
        return boxRight;
    }

    public List<Box> getBoxList() {
        return Collections.unmodifiableList(boxList);
    }

    /**
     * If one of the boxes at this bar has only two owners ,
     * then after setting this stroke only one ...
     * This would give a box to the opponent .
     */
    public boolean isSurroundingBoxClosed() {

        for (Box box : boxList)
            if (box.getStrokeWithoutOwner().size() <= 2)
                return true;

        return false;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Stroke [boxAbove=" + boxAbove + ", boxBelow="
                + boxBelow + ", boxLeft=" + boxLeft
                + ", boxRight=" + boxRight + ", owner="
                + owner + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((boxLeft == null) ? 0 : boxLeft.hashCode());
        result = prime * result + ((boxAbove == null) ? 0 : boxAbove.hashCode());
        result = prime * result + ((boxRight == null) ? 0 : boxRight.hashCode());
        result = prime * result + ((boxBelow == null) ? 0 : boxBelow.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Stroke other = (Stroke) obj;
        if (boxLeft == null) {
            if (other.boxLeft != null)
                return false;
        } else if (!boxLeft.equals(other.boxLeft))
            return false;
        if (boxAbove == null) {
            if (other.boxAbove != null)
                return false;
        } else if (!boxAbove.equals(other.boxAbove))
            return false;
        if (boxRight == null) {
            if (other.boxRight != null)
                return false;
        } else if (!boxRight.equals(other.boxRight))
            return false;
        if (boxBelow == null) {
            if (other.boxBelow != null)
                return false;
        } else if (!boxBelow.equals(other.boxBelow))
            return false;
        return true;
    }

}
