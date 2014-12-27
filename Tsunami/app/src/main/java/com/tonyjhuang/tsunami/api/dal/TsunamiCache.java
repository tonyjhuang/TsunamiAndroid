package com.tonyjhuang.tsunami.api.dal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.logging.Timber;

import java.io.IOException;
import java.io.Serializable;

import javax.inject.Singleton;

/**
 * Created by tony on 12/26/14.
 */
@Singleton
public class TsunamiCache {
    private static final int BYTE_PER_MEGABYTE = 1000000;

    private SimpleDiskCache cache;

    public TsunamiCache(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int appVersion = packageInfo.versionCode;
            this.cache = SimpleDiskCache.open(context.getCacheDir(), appVersion, 4 * BYTE_PER_MEGABYTE);
        } catch (PackageManager.NameNotFoundException e) {
            // Should never happen
        } catch (IOException e) {
            /**
             * Uh, ran into an exception trying to open the cache. That's fine, just make sure
             * you watch out for null pointer exceptions
             */

            this.cache = null;
        }
    }

    public UserStats getUserStats(String userId) {
        return get(userId, UserStats.class);
    }

    public UserStats putUserStats(String userId, UserStats userStats) {
        put(userId, userStats, UserStats.class);
        return userStats;
    }

    private <T> T get(String key, Class<T> clazz) {
        try {
            T value = cache.getApiObject(key, clazz);
            Timber.i("successfully retrieved " + value + " from cache with key: " + key);
            return value;
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    private void put(String key, Serializable value, Class clazz) {
        try {
            cache.put(key, value, clazz);
            Timber.i("successfully put " + value + " into cache with key: " + key);
        } catch (IOException | NullPointerException e) {
            // silently fail.
        }
    }
}
