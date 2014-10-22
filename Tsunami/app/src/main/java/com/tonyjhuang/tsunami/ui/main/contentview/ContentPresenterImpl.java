package com.tonyjhuang.tsunami.ui.main.contentview;

import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.logging.Timber;

import java.util.Random;

/**
 * Created by tonyjhuang on 10/21/14.
 */
public class ContentPresenterImpl implements ContentPresenter {

    private ContentView view;
    private Wave cachedDuringSplash;

    RandomString randomTitleGen = new RandomString(16);
    RandomString randomTextGen = new RandomString(128);

    public ContentPresenterImpl(ContentView view) {
        this.view = view;
        displayNewWave();
    }

    private void displayNewWave() {
        Wave randomWave = Wave.createDebugWave(randomTitleGen.nextString(), randomTextGen.nextString());
        view.showContentCard(randomWave);
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
        view.showContentCard(cachedDuringSplash);
    }

    @Override
    public void onSplashSwipedDown() {
        Timber.d("onSplashSwipedDown");
        view.showContentCard(cachedDuringSplash);
    }

    @Override
    public void onSplashButtonClicked() {
        Timber.d("onSplashButtonClicked");
        cachedDuringSplash = view.getContentWave();
        view.showSplashCard();
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
