package com.tonyjhuang.tsunami.api.dal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.LruCache;

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

    private SimpleDiskCache diskCache;
    private LruCache<String, Object> memCache = new LruCache<>(1024);

    public TsunamiCache(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int appVersion = packageInfo.versionCode;
            this.diskCache = SimpleDiskCache.open(context.getCacheDir(), appVersion, 4 * BYTE_PER_MEGABYTE);
        } catch (PackageManager.NameNotFoundException e) {
            // Should never happen
        } catch (IOException e) {
            /**
             * Uh, ran into an exception trying to open the diskCache. That's fine, just make sure
             * you watch out for null pointer exceptions
             */

            this.diskCache = null;
        }
    }

    public <T> T get(long key, Class<T> clazz) {
        return get(String.valueOf(key), clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        if (memGet(key, clazz) != null) {
            Timber.i("Got value from mem cache with key: " + key);
            return memGet(key, clazz);
        } else {
            try {
                T value = diskCache.getApiObject(key, clazz);
                Timber.i("Got value from disk cache with key: " + key);
                if (value != null) memPut(key, value);
                return value;
            } catch (IOException e) {
                return null;
            }
        }
    }

    public <T> T put(long key, T value) {
        return put(String.valueOf(key), value);
    }

    public <T> T put(String key, T value) {
        try {
            memPut(key, value);
            diskCache.put(key, (Serializable) value, value.getClass());
            Timber.i("Put value into caches with key: " + key);
        } catch (NotSerializableException e) {
            Timber.e(e, "object must implement Serializable interface to be placed in DiskCache!");
            return null;
        } catch (IOException | NullPointerException e) {
            Timber.e(e, "uh oh spaghettio! key: " + key + ", value: " + value);
            return null;
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private <T> T memGet(String key, Class<T> clazz) {
        return (T) memCache.get(getKey(key, clazz));
    }

    private <T> T memPut(String key, T value) {
        memCache.put(getKey(key, value.getClass()), value);
        return value;
    }

    private String getKey(String key, Class clazz) {
        return clazz.toString() + "_" + key;
    }
}
