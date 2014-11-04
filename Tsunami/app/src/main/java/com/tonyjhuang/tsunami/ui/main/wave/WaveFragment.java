package com.tonyjhuang.tsunami.ui.main.wave;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.wave.contentview.WaveContentScrollView;
import com.tonyjhuang.tsunami.ui.main.wave.mapview.WaveMapView;

import java.util.Random;

import butterknife.InjectView;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class WaveFragment extends TsunamiFragment implements WavePresenter {
    @InjectView(R.id.content_scrollview)
    WaveContentScrollView contentView;

    /**
     * Wave that we're hiding while the user is in the process of splashing a new Wave.
     */
    private Wave cachedDuringSplash;

    /**
     * Our lovely map view that will handle drawing on the MapFragment
     */
    private WaveMapView waveMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wave, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentView.setPresenter(this);
        displayNewWave();
    }

    RandomString randomTitleGen = new RandomString(16);
    RandomString randomTextGen = new RandomString(128);

    private void displayNewWave() {
        Wave randomWave = Wave.createDebugWave(randomTitleGen.nextString(), randomTextGen.nextString());
        contentView.showContentCard(randomWave);
    }

    @Override
    public void onContentSwipedUp() {
        Timber.d("onContentSwipedUp");
        displayNewWave();
    }

    @Override
    public void onContentSwipedDown() {
        Timber.d("onContentSwipedDown");
        displayNewWave();
    }

    @Override
    public void onSplashSwipedUp() {
        Timber.d("onSplashSwipedUp");
        contentView.showContentCard(cachedDuringSplash);
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        contentView.showContentCard(cachedDuringSplash);
    }

    @Override
    public void onSplashButtonClicked() {
        Timber.d("onSplashButtonClicked");
        cachedDuringSplash = contentView.getContentWave();
        contentView.showSplashCard();
    }

    public static class RandomString {

        private static final char[] symbols;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }
}
