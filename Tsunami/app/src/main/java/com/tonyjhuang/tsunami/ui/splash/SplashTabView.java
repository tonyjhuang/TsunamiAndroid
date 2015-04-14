package com.tonyjhuang.tsunami.ui.splash;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tonyjhuang.tsunami.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 2/8/15.
 */
public class SplashTabView extends LinearLayout {

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int LINK = 2;
    public static final int AUDIO = 3;

    @InjectView(R.id.text)
    ImageView textTab;
    @InjectView(R.id.image)
    ImageView imageTab;
    @InjectView(R.id.link)
    ImageView linkTab;
    @InjectView(R.id.audio)
    ImageView audioTab;

    Toast toast;

    private List<SplashTab> tabs = new ArrayList<>();
    private OnSelectedTabChangedListener listener;

    public SplashTabView(Context context) {
        this(context, null);
    }

    public SplashTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.inject(this, inflate(context, R.layout.view_splash_tab, this));
        this.inflateTabs();
    }

    private void inflateTabs() {
        // This is pretty hacky but make sure you add the tabs in the order of the static variable
        // declarations up top. This is so we can use the variables to index into the array.
        tabs.add(new SplashTab(textTab, R.drawable.splash_quote_grey, R.drawable.splash_quote_black));
        tabs.add(new SplashTab(imageTab, R.drawable.splash_camera_grey, R.drawable.splash_camera_black));
        tabs.add(new SplashTab(linkTab, R.drawable.splash_link_grey, R.drawable.splash_link_black));
        tabs.add(new SplashTab(audioTab, R.drawable.splash_mic_grey, R.drawable.splash_mic_black));
    }

    public void setOnSelectedTabChangedListener(OnSelectedTabChangedListener listener) {
        this.listener = listener;
    }

    private void notifyListener(int selectedTab) {
        if (listener != null)
            listener.onSelectedTabChanged(selectedTab);
    }

    public void selectTab(int tabId) {
        if (!tabs.get(tabId).isSelected()) {
            for (SplashTab tab : tabs)
                tab.unselect();
            tabs.get(tabId).select();
            notifyListener(tabId);
        }
    }

    @OnClick(R.id.text)
    public void onTextTabClick(View view) {
        selectTab(TEXT);
    }

    @OnClick(R.id.image)
    public void onImageTabClick(View view) {
        showToast("Images coming soon!");
        //selectTab(IMAGE);
    }

    @OnClick(R.id.link)
    public void onLinkTabClick(View view) {
        showToast("Links coming soon!");
        //selectTab(LINK);
    }

    @OnClick(R.id.audio)
    public void onAudioTabClick(View view) {
        showToast("Sound coming soon!");
        //selectTab(AUDIO);
    }

    private void showToast(String message) {
        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static class SplashTab {
        public static final int TRANSITION_DURATION = 500;
        private ImageView imageView;
        private TransitionDrawable drawable;
        private boolean selected = false;

        public SplashTab(ImageView imageView, int unselectedResource, int selectedResource) {
            Resources resources = imageView.getResources();
            this.imageView = imageView;
            this.drawable = new TransitionDrawable(new Drawable[]{
                    resources.getDrawable(unselectedResource),
                    resources.getDrawable(selectedResource)
            });
            this.imageView.setImageDrawable(this.drawable);
        }

        public void select() {
            if (!selected) {
                selected = true;
                drawable.startTransition(TRANSITION_DURATION);
            }
        }

        public void unselect() {
            if (selected) {
                selected = false;
                drawable.reverseTransition(TRANSITION_DURATION);
            }
        }

        public boolean isSelected() {
            return selected;
        }
    }

    public static interface OnSelectedTabChangedListener {
        public void onSelectedTabChanged(int selectedTab);
    }

}
