package com.tonyjhuang.tsunami.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.localytics.android.Localytics;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.api.parsers.TsunamiGson;
import com.tonyjhuang.tsunami.logging.TLocalytics;
import com.tonyjhuang.tsunami.utils.FileReader;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;
import com.tonyjhuang.tsunami.utils.TsunamiObservable;

import java.util.Map;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by tony on 2/5/15.
 */
public class SplashFragment extends TsunamiFragment implements SplashTabView.OnSelectedTabChangedListener {

    public static final int DEFAULT_TAB = SplashTabView.TEXT;

    @InjectView(R.id.profile_pic)
    ImageView profilePic;
    @InjectView(R.id.text)
    EditText text;
    @InjectView(R.id.content_tab)
    SplashTabView tabView;
    @InjectView(R.id.content_container)
    FrameLayout contentContainer;

    @Inject
    TsunamiApi api;
    @Inject
    LocationInfo locationInfo;

    private ContentType currentContentType;
    private SplashContentView currentView;

    public static SplashFragment getInstance() {
        return new SplashFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subscribe(FileReader.getRandomLine(getActivity(), "splash_caption_hints.txt"), text::setHint,
                TsunamiObservable.timberErrorLogger());
        tabView.setOnSelectedTabChangedListener(this);
        tabView.selectTab(DEFAULT_TAB);
    }

    @Override
    public void onSelectedTabChanged(int selectedTab) {
        ContentType newContentType = null;
        switch (selectedTab) {
            case SplashTabView.TEXT:
                newContentType = ContentType.text;
                break;
            case SplashTabView.IMAGE:
                newContentType = ContentType.image;
                hideKeyboard();
                break;
            case SplashTabView.LINK:
                newContentType = ContentType.link;
                break;
            case SplashTabView.AUDIO:
                newContentType = ContentType.audio;
                break;
        }

        if (newContentType != null)
            setContentType(newContentType);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }

    @OnClick(R.id.splash_button)
    public void onSplashButtonClick(View view) {
        splash();
    }

    /**
     * Splash a new wave.
     */
    private void splash() {
        if (validateSplash()) {
            Observable<Wave> splash = api.splash(text.getText().toString(),
                    WaveContent.ContentType.text_content,
                    locationInfo.lastLat,
                    locationInfo.lastLong);

            subscribe(splash, (wave) -> {
                Localytics.tagEvent(TLocalytics.TAG_WAVE_SPLASH);
                Intent data = new Intent();
                data.putExtra(TsunamiConstants.WAVE_RESULT_EXTRA, wave);

                getActivity().setResult(TsunamiConstants.SPLASH_CREATED, data);
                getActivity().finish();
            }, (error) -> {
                TLocalytics.tagError(TLocalytics.ERROR_WAVE_SPLASH, error.getMessage());
                showToast("Couldn't splash this wave. Try again?");
            });
        }
    }

    /**
     * Check if the user has filled out enough info to splash a new wave.
     * Returns true if yes.
     */
    private boolean validateSplash() {
        boolean validated = true;
        if (TextUtils.isEmpty(text.getText())) {
            showToast("Add some text to your wave!");
            validated = false;
        }
        return validated;
    }

    private void setContentType(@NonNull ContentType contentType) {
        if (contentType.equals(currentContentType)) return;

        SplashContentView contentView = null;
        switch (contentType) {
            case text:
                contentView = new SplashTextContent(getTsunamiActivity());
                break;
            case image:
                contentView = new SplashImageContent(getTsunamiActivity());
                break;
            case link:
                break;
            case audio:
                break;
        }

        if (contentView != null)
            contentView.setFragment(this);

        setContentContainerView(contentView);
        currentContentType = contentType;
    }

    private void setContentContainerView(SplashContentView view) {
        contentContainer.removeAllViews();
        currentView = view;
        if (view == null) return;

        contentContainer.addView((View) view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentView != null)
            currentView.onActivityResult(requestCode, resultCode, data);
    }

    public static enum ContentType {
        text, image, link, audio
    }
}
