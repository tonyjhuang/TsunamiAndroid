package com.tonyjhuang.tsunami.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;

import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class ContentFragment extends TsunamiFragment {
    @InjectView(R.id.content_scrollview)
    ContentScrollView content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        content.resetContentCard();
    }

    public void showSplashCard() {
        content.showSplashCard();
    }


    public void resetContentCard() {
        content.resetContentCard();
    }

    public boolean isSplashCardShowing() {
        return content.isSplashCardShowing();
    }
}
