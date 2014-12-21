package com.tonyjhuang.tsunami.utils;

import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.plattysoft.leonids.ParticleSystem;
import com.tonyjhuang.tsunami.R;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by tony on 12/21/14.
 */
public class Celebration {
    public static final int MINIMUM_ROTATION = 100;
    public static final int MAXIMUM_ROTATION = 720;

    public static final int DEFAULT_DURATION = 1000;

    public static final int MINIMUM_PARTICLES = 15;
    public static final int MAXIMUM_PARTICLES = 30;

    public static final float MINIMUM_SPEED = 0.2f;
    public static final float MAXIMUM_SPEED = 0.5f;


    private static Random random = new Random();
    private static Handler handler = new Handler();
    private static Interpolator accelerateInterpolater = new AccelerateInterpolator();

    public static void createRandomOneShot(Activity activity, FrameLayout container) {
        createRandomOneShot(activity, container, DEFAULT_DURATION);
    }

    public static void createRandomOneShot(Activity activity, FrameLayout rootViewGroup, int duration) {
        Pair<Integer, Integer> screenSize = getScreenSize(activity);

        // keep X and Y within the middle 3/4 of the screen.
        int viewX = randInt(oneEighthOf(screenSize.first), sevenEightsOf(screenSize.first));
        int viewY = randInt(oneEighthOf(screenSize.second), sevenEightsOf(screenSize.second));

        View anchorView = new View(activity);
        rootViewGroup.addView(anchorView);
        anchorView.setX(viewX);
        anchorView.setY(viewY);

        int minRotation = viewX < (screenSize.first / 2) ? MINIMUM_ROTATION : -MAXIMUM_ROTATION;
        int maxRotation = viewX < (screenSize.second / 2) ? MAXIMUM_ROTATION : -MINIMUM_ROTATION;
        int numParticles = randInt(MINIMUM_PARTICLES, MAXIMUM_PARTICLES);

        new ParticleSystem(activity, numParticles, R.drawable.profile, duration)
                .setSpeedRange(MINIMUM_SPEED, MAXIMUM_SPEED)
                .setRotationSpeedRange(minRotation, maxRotation)
                .setFadeOut(DEFAULT_DURATION, accelerateInterpolater)
                .oneShot(anchorView, numParticles);

        removeViewFromViewGroupAfter(new WeakReference<>(anchorView), new WeakReference<>(rootViewGroup), duration);
    }

    private static Pair<Integer, Integer> getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Pair<>(size.x, size.y);
    }

    private static int oneEighthOf(int num) {
        return (int) ((float) num / 8);
    }

    private static int sevenEightsOf(int num) {
        return (int) ((float) num * 7 / 8);
    }

    private static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    private static void removeViewFromViewGroupAfter(WeakReference<View> transientView,
                                                     WeakReference<ViewGroup> viewGroup,
                                                     int delay) {
        handler.postDelayed(() -> {
            if (viewGroup != null
                    && viewGroup.get() != null
                    && transientView != null
                    && transientView.get() != null) {
                viewGroup.get().removeView(transientView.get());
            }
        }, delay);
    }
}
