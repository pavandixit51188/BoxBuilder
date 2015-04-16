/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) 2011 - 2012 Stefan Oltmann
 *
 * Contact : dotsandboxes@stefan-oltmann.de
 * Homepage: http://www.stefan-oltmann.de/
 *
 * This file is part of Kaesekaestchen.
 *
 * Kaesekaestchen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kaesekaestchen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kaesekaestchen. If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.kaesekaestchen.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import de.stefan_oltmann.kaesekaestchen.PitchView;

/**
 * Ein Kästchen auf dem Pitch.
 * 
 * @author Stefan Oltmann
 */
public class Box {

    /*
     * Position of the box in the grid. This is identified herein above
     * so that this is also the ID .
     */
    private int     rasterX;
    private int     rasterY;

    /**
     * Was the player include box , it will be the owner of the box.
     * This counts at the end of the game than 1 victory point
     */
    private Player owner;

    /* Underscores the box */
    private Stroke strokeAbove;
    private Stroke strokeDown;
    private Stroke strokeLeft;
    private Stroke strokeRight;

    private Paint framePaint = new Paint();

    /**
     * Constructor to create the box .
     * You must specify the position / ID of the box .
     */
    public Box(int rasterX, int rasterY) {
        this.rasterX = rasterX;
        this.rasterY = rasterY;

        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(5);
    }

    public int getRasterX() {
        return rasterX;
    }

    public int getRasterY() {
        return rasterY;
    }

    public int getPixelX() {
        return rasterX * PitchView.BOX_SIDE_LENGTH + PitchView.PADDING;
    }

    public int getPixelY() {
        return rasterY * PitchView.BOX_SIDE_LENGTH + PitchView.PADDING;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Stroke getStrokeAbove() {
        return strokeAbove;
    }

    public void setStrokeAbove(Stroke strokeAbove) {
        this.strokeAbove = strokeAbove;
    }

    public Stroke getStrokeDown() {
        return strokeDown;
    }

    public void setStrokeDown(Stroke strokeDown) {
        this.strokeDown = strokeDown;
    }

    public Stroke getStrokeLeft() {
        return strokeLeft;
    }

    public void setStrokeLeft(Stroke strokeLeft) {
        this.strokeLeft = strokeLeft;
    }

    public Stroke getStrokeRight() {
        return strokeRight;
    }

    public void setStrokeRight(Stroke strokeRight) {
        this.strokeRight = strokeRight;
    }

    public List<Stroke> getStriche() {

        List<Stroke> striche = new ArrayList<Stroke>();
        if (strokeAbove != null)
            striche.add(strokeAbove);
        if (strokeDown != null)
            striche.add(strokeDown);
        if (strokeLeft != null)
            striche.add(strokeLeft);
        if (strokeRight != null)
            striche.add(strokeRight);
        return striche;
    }

    public List<Stroke> getStrokeWithoutOwner() {

        List<Stroke> strokes = new ArrayList<Stroke>();
        if (strokeAbove != null && strokeAbove.getOwner() == null)
            strokes.add(strokeAbove);
        if (strokeDown != null && strokeDown.getOwner() == null)
            strokes.add(strokeDown);
        if (strokeLeft != null && strokeLeft.getOwner() == null)
            strokes.add(strokeLeft);
        if (strokeRight != null && strokeRight.getOwner() == null)
            strokes.add(strokeRight);
        return strokes;
    }

    public boolean isAllStrokesHaveOwner() {
        return getStrokeWithoutOwner().size() == 0;
    }

    public Rect getRectBarAtTop() {

        if (strokeAbove == null)
            return null;

        return new Rect(getPixelX() + PitchView.BOX_SIDE_LENGTH / 4, getPixelY() - PitchView.BOX_SIDE_LENGTH / 4, getPixelX() + (int) (PitchView.BOX_SIDE_LENGTH * 0.75), getPixelY() + PitchView.BOX_SIDE_LENGTH / 4);
    }

    public Rect getRectBarAtBottom() {

        if (strokeDown == null)
            return null;

        return new Rect(getPixelX() + PitchView.BOX_SIDE_LENGTH / 4, getPixelY() + (int) (PitchView.BOX_SIDE_LENGTH * 0.75), getPixelX() + (int) (PitchView.BOX_SIDE_LENGTH * 0.75), getPixelY() + PitchView.BOX_SIDE_LENGTH + PitchView.BOX_SIDE_LENGTH / 4);
    }

    public Rect getRectBarAtLeft() {

        if (strokeLeft == null)
            return null;

        return new Rect(getPixelX() - PitchView.BOX_SIDE_LENGTH / 4, getPixelY() + PitchView.BOX_SIDE_LENGTH / 4, getPixelX() + PitchView.BOX_SIDE_LENGTH / 4, getPixelY() + (int) (PitchView.BOX_SIDE_LENGTH * 0.75));
    }

    public Rect getRectBarAtRight() {

        if (strokeRight == null)
            return null;

        return new Rect(getPixelX() + (int) (PitchView.BOX_SIDE_LENGTH * 0.75), getPixelY() + PitchView.BOX_SIDE_LENGTH / 4, getPixelX() + PitchView.BOX_SIDE_LENGTH + PitchView.BOX_SIDE_LENGTH / 4, getPixelY() + (int) (PitchView.BOX_SIDE_LENGTH * 0.75));
    }

    /**
     * This method determines which line has been pressed on the box .
     */
    public Stroke getStrokePosition(int pixelX, int pixelY) {

        if (getRectBarAtTop() != null && getRectBarAtTop().contains(pixelX, pixelY))
            return strokeAbove;

        if (getRectBarAtBottom() != null && getRectBarAtBottom().contains(pixelX, pixelY))
            return strokeDown;

        if (getRectBarAtLeft() != null && getRectBarAtLeft().contains(pixelX, pixelY))
            return strokeLeft;

        if (getRectBarAtRight() != null && getRectBarAtRight().contains(pixelX, pixelY))
            return strokeRight;

        return null;
    }

    public void onDraw(Canvas canvas) {

        if (owner != null) {

            Paint painter = new Paint();
            painter.setColor(owner.getColor());

            Rect destRect = new Rect(getPixelX(), getPixelY(), getPixelX() + PitchView.BOX_SIDE_LENGTH, getPixelY() + PitchView.BOX_SIDE_LENGTH);
            canvas.drawBitmap(owner.getSymbol(), null, destRect, framePaint);
        }

        if (strokeAbove == null) {
            framePaint.setColor(Color.BLACK);
            canvas.drawLine(getPixelX(), getPixelY(), getPixelX() + PitchView.BOX_SIDE_LENGTH, getPixelY(), framePaint);
        }

        if (strokeDown != null && strokeDown.getOwner() != null)
            framePaint.setColor(strokeDown.getOwner().getColor());
        else if (strokeDown != null)
            framePaint.setColor(Color.LTGRAY);
        else
            framePaint.setColor(Color.BLACK);

        canvas.drawLine(getPixelX(), getPixelY() + PitchView.BOX_SIDE_LENGTH, getPixelX() + PitchView.BOX_SIDE_LENGTH, getPixelY() + PitchView.BOX_SIDE_LENGTH, framePaint);

        if (strokeLeft == null) {
            framePaint.setColor(Color.BLACK);
            canvas.drawLine(getPixelX(), getPixelY(), getPixelX(), getPixelY() + PitchView.BOX_SIDE_LENGTH, framePaint);
        }

        if (strokeRight != null && strokeRight.getOwner() != null)
            framePaint.setColor(strokeRight.getOwner().getColor());
        else if (strokeRight != null)
            framePaint.setColor(Color.LTGRAY);
        else
            framePaint.setColor(Color.BLACK);

        canvas.drawLine(getPixelX() + PitchView.BOX_SIDE_LENGTH, getPixelY(), getPixelX() + PitchView.BOX_SIDE_LENGTH, getPixelY() + PitchView.BOX_SIDE_LENGTH, framePaint);

        /* distinguished vertices */
        framePaint.setColor(Color.BLACK);
        canvas.drawRect(getPixelX() - 1, getPixelY() - 1, getPixelX() + 1, getPixelY() + 1, framePaint);
        canvas.drawRect(getPixelX() + PitchView.BOX_SIDE_LENGTH - 1, getPixelY() - 1, getPixelX() + PitchView.BOX_SIDE_LENGTH + 1, getPixelY() + 1, framePaint);
        canvas.drawRect(getPixelX() - 1, getPixelY() + PitchView.BOX_SIDE_LENGTH - 1, getPixelX() + 1, getPixelY() + PitchView.BOX_SIDE_LENGTH + 1, framePaint);
        canvas.drawRect(getPixelX() + PitchView.BOX_SIDE_LENGTH - 1, getPixelY() + PitchView.BOX_SIDE_LENGTH - 1, getPixelX() + PitchView.BOX_SIDE_LENGTH + 1, getPixelY() + PitchView.BOX_SIDE_LENGTH + 1, framePaint);
    }

    @Override
    public String toString() {
        return "Box [rasterX=" + rasterX + ", rasterY=" + rasterY + ", owner=" + owner + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rasterX;
        result = prime * result + rasterY;
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
        Box other = (Box) obj;
        if (rasterX != other.rasterX)
            return false;
        if (rasterY != other.rasterY)
            return false;
        return true;
    }

}
