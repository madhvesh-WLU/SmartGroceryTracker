package com.example.smartgrocerytracker;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;

public class ZoomableImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector scaleDetector;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        setScaleType(ScaleType.MATRIX); // Ensures transformations are applied to the image
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(1f, Math.min(scale, 5f)); // Limit the zoom scale (1x to 5x)

            matrix.setScale(scale, scale, detector.getFocusX(), detector.getFocusY());
            setImageMatrix(matrix);
            return true;
        }
    }
}