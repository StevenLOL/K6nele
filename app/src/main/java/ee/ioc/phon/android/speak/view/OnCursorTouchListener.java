package ee.ioc.phon.android.speak.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import ee.ioc.phon.android.speak.Log;

//
// TODO:
// - single tap
// - double tap
// - long press
// - add newline swipe (?)
// Maybe reuse some code from
// https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/view/GestureDetector.java
public class OnCursorTouchListener implements View.OnTouchListener {

    private float mStartX = 0;
    private float mStartY = 0;

    public OnCursorTouchListener(Context context) {
    }

    public void onMove(int numOfChars) {
        // intentionally empty
    }

    public void onSwipeLeft() {
        // intentionally empty
    }

    public void onSwipeRight() {
        // intentionally empty
    }

    public void onSingleTapMotion() {
        // intentionally empty
    }

    public void onDoubleTapMotion() {
        // intentionally empty
    }

    public void onLongPressMotion() {
        // intentionally empty
    }

    public boolean onTouch(View v, MotionEvent event) {
        //return mGestureDetector.onTouchEvent(event);

        int action = event.getActionMasked();

        float newX = event.getX();
        float newY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = newX;
                mStartY = newY;
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = getDistance(mStartX, mStartY, event);
                // TODO: scale by size of the panel
                int numOfChars = Math.round(distance / 25);
                Log.i("distance: " + numOfChars + " " + distance);
                if (numOfChars > 0) {
                    double atan2 = Math.atan2(mStartY - newY, mStartX - newX);
                    if (atan2 > -0.4 && atan2 < 1.97) {
                        // Swiping left up, allowing +/- 0.4 error
                        onMove(-1 * numOfChars);
                    } else if (atan2 > 2.74 || atan2 < -1.17) {
                        // Swiping right down
                        onMove(numOfChars);
                    }
                    mStartX = newX;
                    mStartY = newY;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private float getDistance(float startX, float startY, MotionEvent ev) {
        float distanceSum = 0;
        final int historySize = ev.getHistorySize();
        Log.i("distance: historySize: " + historySize);
        for (int h = 0; h < historySize; h++) {
            // historical point
            float hx = ev.getHistoricalX(0, h);
            float hy = ev.getHistoricalY(0, h);
            // distance between startX,startY and historical point
            float dx = (hx - startX);
            float dy = 3 * (hy - startY);
            distanceSum += Math.sqrt(dx * dx + dy * dy);
            // make historical point the start point for next loop iteration
            startX = hx;
            startY = hy;
        }
        // add distance from last historical point to event's point
        float dx = (ev.getX(0) - startX);
        float dy = 3 * (ev.getY(0) - startY);
        distanceSum += Math.sqrt(dx * dx + dy * dy);
        return distanceSum;
    }
}