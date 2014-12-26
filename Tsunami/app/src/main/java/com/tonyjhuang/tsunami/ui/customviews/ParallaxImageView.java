package com.tonyjhuang.tsunami.ui.customviews;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tonyjhuang.tsunami.logging.Timber;

/**
 * Created by tony on 12/25/14.
 */
public class ParallaxImageView extends ImageView {
    private float baseWidth = 0;
    private float baseHeight = 0.0f;
    private float currentWidth = baseWidth;
    private float currentHeight = baseHeight;
    private float parallaxFactor = 1.0f;

    public ParallaxImageView(Context context) {
        this(context, null);
    }

    public ParallaxImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        setScaleType(ScaleType.MATRIX);
    }

    public void setParallaxFactor(float parallaxFactor) {
        this.parallaxFactor = parallaxFactor;
    }

    // How far right or down should we place the upper-left corner of the cropbox? [0, 1]
    public void setBaseOffset(float baseWidth, float baseHeight) {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }

    private void setCurrentOffset(float currentWidth, float currentHeight) {
        this.currentWidth = currentWidth;
        this.currentHeight = currentHeight;
        requestLayout();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix = getImageMatrix();

        float scale;
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = 0, drawableHeight = 0;
        // Allow for setting the drawable later in code by guarding ourselves here.
        if (getDrawable() != null) {
            drawableWidth = getDrawable().getIntrinsicWidth();
            drawableHeight = getDrawable().getIntrinsicHeight();
        }

        // Get the scale.
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            // Drawable is flatter than view. Scale it to fill the view height.
            // A Top/Bottom crop here should be identical in this case.
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            // Drawable is taller than view. Scale it to fill the view width.
            // Left/Right crop here should be identical in this case.
            scale = (float) viewWidth / (float) drawableWidth;
        }

        float viewToDrawableWidth = viewWidth / scale;
        float viewToDrawableHeight = viewHeight / scale;
        float xOffset = currentWidth * (drawableWidth - viewToDrawableWidth);
        float yOffset = currentHeight * (drawableHeight - viewToDrawableHeight);

        // Define the rect from which to take the image portion.
        RectF drawableRect = new RectF(xOffset, yOffset, xOffset + viewToDrawableWidth,
                yOffset + viewToDrawableHeight);
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);

        setImageMatrix(matrix);

        return super.setFrame(l, t, r, b);
    }

    public void onScroll(int l, int t, int oldl, int oldt) {
        float height = getHeight();
        float hScrollFactor = Math.min(t / height, 1);
        float hParallaxBy = (-1 * parallaxFactor * hScrollFactor) * (1.0f - baseHeight);

        float width = getWidth();
        float wScrollFactor = Math.min(l / width, 1);
        float wParallaxBy = (-1 * parallaxFactor * wScrollFactor) * (1.0f - baseWidth);
        setCurrentOffset(baseWidth + wParallaxBy, baseHeight + hParallaxBy);
    }
}