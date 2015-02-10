package com.tonyjhuang.tsunami.ui.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.utils.FileReader;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;
import com.tonyjhuang.tsunami.utils.TsunamiObservable;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 2/5/15.
 */
public class SplashFragment extends TsunamiFragment implements SplashTabView.OnSelectedTabChangedListener {

    @InjectView(R.id.profile_pic)
    ImageView profilePic;
    @InjectView(R.id.text)
    EditText text;
    @InjectView(R.id.content_tab)
    SplashTabView tabView;

    @Inject
    TsunamiApi api;
    @Inject
    LocationInfo locationInfo;

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
    }

    @Override
    public void onSelectedTabChanged(int selectedTab) {
        //showToast("tab selected: " + selectedTab);
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
            api.splash("dummy",
                    text.getText().toString(),
                    WaveContent.ContentType.text,
                    locationInfo.lastLat,
                    locationInfo.lastLong)
                    .publish()
                    .connect();
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
}
