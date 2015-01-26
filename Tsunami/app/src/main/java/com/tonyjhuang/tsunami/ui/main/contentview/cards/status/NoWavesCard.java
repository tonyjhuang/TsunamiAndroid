package com.tonyjhuang.tsunami.ui.main.contentview.cards.status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.TsunamiCard;

import butterknife.ButterKnife;

/**
 * Created by tony on 1/26/15.
 */
public class NoWavesCard extends TsunamiCard {
    public NoWavesCard(Context context) {
        super(context);
    }

    public NoWavesCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoWavesCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View getInnerView(Context context, ViewGroup container) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_no_waves, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
