package com.tonyjhuang.tsunami.ui.main.contentview.cards.status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.TsunamiCard;

/**
 * Created by tony on 1/26/15.
 */
public class ErrorCard extends TsunamiCard {

    public ErrorCard(Context context) {
        super(context);
    }

    public ErrorCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ErrorCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View getInnerView(Context context, ViewGroup container) {
        return LayoutInflater.from(context).inflate(R.layout.card_error, container, false);
    }
}
