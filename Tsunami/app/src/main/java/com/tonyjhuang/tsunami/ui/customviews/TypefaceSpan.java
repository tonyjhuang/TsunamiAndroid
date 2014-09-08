package com.tonyjhuang.tsunami.ui.customviews;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import com.tonyjhuang.tsunami.utils.TypefaceManager;

/**
 * Used to be used to style/apply a fontface to overflow menu items but caused issues
 * 'Invalid payload type'
 * Created by tonyhuang on 7/9/14.
 */
public class TypefaceSpan extends MetricAffectingSpan {
    private Typeface mTypeface;

    /**
     * Load the {@link Typeface} and apply to a {@link android.text.Spannable}.
     */
    public TypefaceSpan(Context context, String typefaceName) {
        mTypeface = TypefaceManager.get(context, typefaceName + ".ttf");

    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}