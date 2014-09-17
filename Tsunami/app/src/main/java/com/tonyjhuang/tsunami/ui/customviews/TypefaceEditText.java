package com.tonyjhuang.tsunami.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.utils.TypefaceManager;

/**
 * Created by tonyhuang on 7/9/14.
 */
public class TypefaceEditText extends EditText {

    private String base;
    private Context context;

    public TypefaceEditText(Context context) {
        super(context);
        this.context = context;
        setTypeface(base);
    }

    public TypefaceEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.TypefaceTextView,
                0, 0);
        try {
            base = a.getString(R.styleable.TypefaceTextView_typeface) + ".ttf";
        } finally {
            a.recycle();
        }

        setTypeface(base);
    }

    public void setTypeface(String typeface) {
        setTypeface(TypefaceManager.get(context, typeface));
    }

}
