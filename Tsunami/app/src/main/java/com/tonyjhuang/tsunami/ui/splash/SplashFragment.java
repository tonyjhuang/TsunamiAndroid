package com.tonyjhuang.tsunami.ui.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.utils.FileReader;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;
import com.tonyjhuang.tsunami.utils.TsunamiObservable;

import butterknife.InjectView;

/**
 * Created by tony on 2/5/15.
 */
public class SplashFragment extends TsunamiFragment {

    @InjectView(R.id.profile_pic)
    ImageView profilePic;
    @InjectView(R.id.caption)
    EditText caption;

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
        subscribe(FileReader.getRandomLine(getActivity(), "splash_caption_hints.txt"), caption::setHint,
                TsunamiObservable.timberErrorLogger());

    }
}
