package com.tonyjhuang.tsunami.api.dal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.LruCache;

import com.google.gson.Gson;
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
    private Gson gson = new Gson();

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

    /**
     * Helper method for grabbing objects from memcache, generates the most likely key that would
     * map to the object given the id.
     * <p>
     * Note: objects that you store with this method will NOT be stored under the key, @key
     *
     * @param key   id of the object
     * @param clazz class of the stored object
     * @param <T>   type of the stored object
     * @return either the object with the id of key or null
     */
    private <T> T memGet(String key, Class<T> clazz) {
        return memGetExplicit(getKey(key, clazz));
    }

    /**
     * Grab the object in memcache that is stored explicitly with this key.
     *
     * @param key value id
     * @param <T> type of the stored object
     * @return either the object with the id of key or null
     */
    @SuppressWarnings("unchecked")
    private <T> T memGetExplicit(String key) {
        return (T) memCache.get(key);
    }

    /**
     * dual to memGet.
     */
    private <T> T memPut(String key, T value) {
        return memPutExplicit(getKey(key, value.getClass()), value);
    }

    /**
     * dual to memGetExplicit .
     */
    private <T> T memPutExplicit(String key, T value) {
        memCache.put(key, value);
        return value;
    }

    private String getKey(String key, Class clazz) {
        return clazz.toString() + "_" + key;
    }
}
