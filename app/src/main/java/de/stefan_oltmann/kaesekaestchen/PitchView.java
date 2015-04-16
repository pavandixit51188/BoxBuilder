
package de.stefan_oltmann.kaesekaestchen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import de.stefan_oltmann.kaesekaestchen.model.Box;
import de.stefan_oltmann.kaesekaestchen.model.Pitch;
import de.stefan_oltmann.kaesekaestchen.model.Stroke;

/**
 * This class draws the pitch and accepts user interactions .
 */
public class PitchView extends View implements OnTouchListener {

    public static int BOX_SIDE_LENGTH = 50;
    public static int       PADDING                = 5;

    private Pitch pitch;

    /**
     * Over the last entry is brought to experience what the user wants .
     * Accessing this value is blocking it were in the game process.
     */
    private volatile Stroke lastEntry;

    public PitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Pitch pitch) {
        this.pitch = pitch;
        setOnTouchListener(this);
    }

    public Stroke getLastEntry() {
        return lastEntry;
    }

    public void resetLastEntry() {
        lastEntry = null;
    }

    /**
     *If the screen resolution changes or initially against known , this method is called .
     * We use to determine how big to make a box depending on the resolution of the display.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (pitch == null)
            return;

        int maxWidth = (w - PADDING * 2) / pitch.getWidthOfBox();
        int maxHeight = (h - PADDING * 2) / pitch.getHeightOfBox();
        BOX_SIDE_LENGTH = Math.min(maxWidth, maxHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.background_color));

        /*
         * If the pitch is not initialized , do not subscribe to this .
         * Otherwise, this would lead to a null pointer exception.
         * This is also required to be displayed correctly in the GUI editor .
         */
        if (pitch == null) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), new Paint());
            return;
        }

        for (Box box : pitch.getBoxList())
            box.onDraw(canvas);
    }

    public boolean onTouch(View view, MotionEvent event) {

        /*
         * There are different motion events, but here we are interested
         * in only the actual pressing on the screen.
         */
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;

        if (lastEntry != null)
            return true;

        int calculateRasterX = (int) event.getX() / BOX_SIDE_LENGTH;
        int calculateRasterY = (int) event.getY() / BOX_SIDE_LENGTH;

        Box box = pitch.getBox(calculateRasterX, calculateRasterY);

        /*
         * If there is no check-box at the touched position or that already has an owner ,
         * ignore the entry.
         */
        if (box == null || box.getOwner() != null)
            return true;

        Stroke stroke = box.getStrokePosition((int) event.getX(), (int) event.getY());

        /*
         * Could not be determined line ,
         * the user has probably made ​​the center of the box.
         * Anyway, it is not clear what line he meant. Therefore, the entry is canceled.
         */
        if (stroke == null)
            return true;

        /*
         * At this point, the user has made ​​its entry successfully .
         * We write its input in a temporary variable to the Communication is used with
         * the Gameloop thread and awaken this via " notifyAll " again .
         * The Gameloop thread was previously with " wait () "
         * and this class as a semaphore " paused "
         */
        lastEntry = stroke;

        synchronized (this) {
            this.notifyAll();
        }

        return true;
    }

    public void updateDisplay() {
        postInvalidate(); // View forcing redraw
    }

}
