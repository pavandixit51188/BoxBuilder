package de.stefan_oltmann.kaesekaestchen.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Pitch {

    private int widthOfBox;
    private int heightOfBox;

    private Box[][] boxArray;

    /**
     * For performance reasons, a second list is maintained so that
     * the ' list box ' does not have to be so common - iterates through .
     * This results in large areas of poor performance.
     */
    private List<Box> openBox = new ArrayList<Box>();

    private Set<Stroke> strokesWithoutOwner = new HashSet<Stroke>();

    /**
     * The constructor function is private because
     * to create playing fields generate the Factory Method () should be used.
     */
    private Pitch(int widthOfBox, int heightOfBox) {
        this.widthOfBox = widthOfBox;
        this.heightOfBox = heightOfBox;

        this.boxArray = new Box[widthOfBox][heightOfBox];
    }

    public List<Box> getBoxList() {

        List<Box> list = new ArrayList<Box>();

        for (int rasterX = 0; rasterX < widthOfBox; rasterX++) {
            for (int rasterY = 0; rasterY < heightOfBox; rasterY++) {
                list.add(boxArray[rasterX][rasterY]);
            }
        }

        return Collections.unmodifiableList(list);
    }

    public List<Box> getOpenBoxList() {
        return Collections.unmodifiableList(openBox);
    }

    public Set<Stroke> getStrokesWithoutOwner() {
        return Collections.unmodifiableSet(strokesWithoutOwner);
    }

    private void addBox(Box box) {
        boxArray[box.getRasterX()][box.getRasterY()] = box;
        openBox.add(box);
    }

    private void addStroke(Stroke stroke) {
        strokesWithoutOwner.add(stroke);
    }

    public Box getBox(int rasterX, int rasterY) {

        if (rasterX >= widthOfBox || rasterY >= heightOfBox)
            return null;

        return boxArray[rasterX][rasterY];
    }

    public int getWidthOfBox() {
        return widthOfBox;
    }

    public int getHeightOfBox() {
        return heightOfBox;
    }

    /**
     * Closes all boxes that can be closed.
     * 
     * @param assignedToOwner
     *            The owner assign this box
     * @return Was the box to close it? (Important for gameplay)
     */
    private boolean inferAllPossibleBox(Player assignedToOwner) {

        boolean isClosedBox = false;

        Iterator<Box> openBoxes = openBox.iterator();

        while (openBoxes.hasNext()) {

            Box box = openBoxes.next();

            if (box.isAllStrokesHaveOwner() && box.getOwner() == null) {
                box.setOwner(assignedToOwner);
                openBoxes.remove();
                isClosedBox = true;
            }
        }

        return isClosedBox;
    }

    public boolean isAllBoxesHaveOwner() {
        return openBox.isEmpty();
    }

    public boolean selectStroke(Stroke stroke, Player player) {
        stroke.setOwner(player);
        strokesWithoutOwner.remove(stroke);
        return inferAllPossibleBox(player);
    }

    /**
     * Factory Method for producing a pitch
     */
    public static Pitch generate(int numberH, int numberV) {

        Pitch pitch = new Pitch(numberH, numberV);

        for (int rasterX = 0; rasterX < numberH; rasterX++) {
            for (int rasterY = 0; rasterY < numberV; rasterY++) {

                pitch.addBox(new Box(rasterX, rasterY));
            }
        }

        for (int rasterX = 0; rasterX < numberH; rasterX++) {
            for (int rasterY = 0; rasterY < numberV; rasterY++) {

                Box box = pitch.getBox(rasterX, rasterY);

                Box boxBelow = null;
                Box boxRight = null;

                if (rasterY < numberV - 1)
                    boxBelow = pitch.getBox(rasterX, rasterY + 1);

                if (rasterX < numberH - 1)
                    boxRight = pitch.getBox(rasterX + 1, rasterY);

                Stroke strokeBelow = new Stroke(box, boxBelow, null, null);
                Stroke strokeRight = new Stroke(null, null, box, boxRight);

                if (boxRight != null) {
                    box.setStrokeRight(strokeRight);
                    boxRight.setStrokeLeft(strokeRight);
                    pitch.addStroke(strokeRight);
                }

                if (boxBelow != null) {
                    box.setStrokeDown(strokeBelow);
                    boxBelow.setStrokeAbove(strokeBelow);
                    pitch.addStroke(strokeBelow);
                }
            }
        }

        return pitch;
    }

}
