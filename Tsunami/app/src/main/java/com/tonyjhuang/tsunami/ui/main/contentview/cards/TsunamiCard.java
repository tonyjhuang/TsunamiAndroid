package com.tonyjhuang.tsunami.ui.main.contentview.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.R;

import butterknife.ButterKnife;

/**
 * Created by tony on 1/26/15.
 */
public abstract class TsunamiCard extends FrameLayout {
    FrameLayout container;

    public TsunamiCard(Context context) {
        this(context, null);
    }

    public TsunamiCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TsunamiCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.card_container, this, true);
        container = ButterKnife.findById(this, R.id.container);

        View innerView = getInnerView(context, container);
        if (innerView != null)
            container.addView(innerView);
    }

    protected abstract View getInnerView(Context context, ViewGroup container);
}
