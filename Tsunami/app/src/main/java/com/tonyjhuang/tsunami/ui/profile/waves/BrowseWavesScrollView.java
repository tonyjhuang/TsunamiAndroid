package com.tonyjhuang.tsunami.ui.profile.waves;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.FadingSingleTargetScrollView;

/**
 * Created by tony on 1/30/15.
 */
public class BrowseWavesScrollView extends FadingSingleTargetScrollView {
    public BrowseWavesScrollView(Context context) {
        super(context);
    }

    public BrowseWavesScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrowseWavesScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setSpacerHeights(Space topSpacer, Space bottomSpacer) {
        int startingCardHeight = (int) getResources().getDimension(R.dimen.bw_starting_card_height);
        LinearLayout.LayoutParams topSpacerLayoutParams = (LinearLayout.LayoutParams) topSpacer.getLayoutParams();
        topSpacerLayoutParams.height = getHeight() - startingCardHeight;
        topSpacer.setLayoutParams(topSpacerLayoutParams);
    }

}
