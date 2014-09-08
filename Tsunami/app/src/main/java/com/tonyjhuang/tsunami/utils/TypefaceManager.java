package com.tonyjhuang.tsunami.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.tonyjhuang.tsunami.logging.Timber;

import java.util.Hashtable;

/**
 * Created by Tony on 3/23/14.
 * Contains a static cache of inflated Typefaces to avoid recreating a Typeface each time it's used.
 */
public class TypefaceManager {
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
    private final static String DEFAULT_TYPEFACE = "Oswald-Regular.ttf";

    /**
     * Returns a default typeface if the passed in typeface path is not found
     */
    public static Typeface get(Context c, String assetPath) {
        if (assetPath == null || assetPath.equals("null") || assetPath.equals("null.ttf"))
            assetPath = DEFAULT_TYPEFACE;
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Timber.e("Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}

