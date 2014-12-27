package com.tonyjhuang.tsunami.api.dal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.LruCache;

import com.tonyjhuang.tsunami.api.models.UserStats;
import com.tonyjhuang.tsunami.logging.Timber;

import java.io.IOException;
import java.io.NotSerializableException;
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

    private LruCache<String, UserStats> userStatsCache = new LruCache<>(16);
    /**
     * Note: this is a blocking function, you should never call this from the main thread!
     */
    public UserStats getUserStats(String userId) {
        return get(userId, UserStats.class, userStatsCache);
    }

    public UserStats putUserStats(String userId, UserStats userStats) {
        return put(userId, userStats, userStatsCache);
    }

    private <T> T get(String key, Class<T> clazz, LruCache<String, T> localCache) {
        if (localCache != null && localCache.get(key) != null) {
            Timber.i("retrieved value from localCache");
            return localCache.get(key);
        }

        try {
            T value = cache.getApiObject(key, clazz);
            Timber.i("successfully retrieved " + value + " from cache with key: " + key);
            if (localCache != null)
                localCache.put(key, value);
            return value;
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T put(String key, T value, LruCache<String, T> localCache) {
        try {
            if (localCache != null) localCache.put(key, value);
            cache.put(key, (Serializable) value, value.getClass());
            Timber.i("successfully put " + value + " into cache with key: " + key);
        } catch (NotSerializableException e) {
            Timber.e(e, "object must implement Serializable interface to be placed in DiskCache!");
            return null;
        } catch (IOException | NullPointerException e) {
            Timber.e(e, "uh oh spaghettio! key: " + key + ", value: " + value);
            return null;
        }

        return value;
    }
}
