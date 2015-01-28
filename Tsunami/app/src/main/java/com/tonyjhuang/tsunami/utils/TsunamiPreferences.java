package com.tonyjhuang.tsunami.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by tony on 11/23/14.
 * To use a preference:
 * prefs.guid.get();
 * prefs.guid.set("....");
 * <p>
 * To create a TsunamiPreference, simply create a new class-scoped instance variable with the appropriate constructor
 */
public class TsunamiPreferences {
    private static final String SHARED_PREFERENCES = "com.tonyjhuang.tsunami.shared_prefs";
    public static final long NO_USER_ID = -1l;

    /**
     * Preference key-value pairs that you can get-set
     */
    public final GuidPreference guid = new GuidPreference("com.tonyjhuang.tsunami.guid");
    public final LongPreference userId = new LongPreference("com.tonyjhuang.tsunami.user_id", NO_USER_ID);
    public final FloatPreference lastSeenLat = new FloatPreference("com.tonyjhuang.tsunami.last_seen_lat", -1f);
    public final FloatPreference lastSeenLng = new FloatPreference("com.tonyjhuang.tsunami.last_seen_lng", -1f);

    private SharedPreferences preferences;

    public TsunamiPreferences(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        guid.initialize();
    }

    abstract class TsunamiPreference<T> {
        String key;
        T defaultValue;

        public TsunamiPreference(String key, T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public abstract T get();

        public abstract void set(T value);

        public T delete() {
            T get = get();
            set(defaultValue);
            return get;
        }
    }

    public class GuidPreference extends StringPreference {
        public GuidPreference(String key) {
            super(key, null);
        }

        public void initialize() {
            if (get() == null) {
                set(UUID.randomUUID().toString());
            }
        }
    }

    public class StringPreference extends TsunamiPreference<String> {

        public StringPreference(String key, String defaultValue) {
            super(key, defaultValue);
        }

        public StringPreference(String key) {
            this(key, null);
        }

        @Override
        public String get() {
            return preferences.getString(key, defaultValue);
        }

        @Override
        public void set(String value) {
            preferences.edit().putString(key, value).apply();
        }
    }

    public class LongPreference extends TsunamiPreference<Long> {
        public LongPreference(String key, Long defaultValue) {
            super(key, defaultValue);
        }

        @Override
        public Long get() {
            return preferences.getLong(key, defaultValue);
        }

        @Override
        public void set(Long value) {
            preferences.edit().putLong(key, value).apply();
        }
    }

    public class FloatPreference extends TsunamiPreference<Float> {
        public FloatPreference(String key, Float defaultValue) {
            super(key, defaultValue);
        }

        @Override
        public Float get() {
            return preferences.getFloat(key, defaultValue);
        }

        @Override
        public void set(Float value) {
            preferences.edit().putFloat(key, value).apply();
        }
    }

    public class BooleanPreference extends TsunamiPreference<Boolean> {

        public BooleanPreference(String key, boolean defaultValue) {
            super(key, defaultValue);
        }

        public BooleanPreference(String key) {
            this(key, false);
        }

        @Override
        public Boolean get() {
            return preferences.getBoolean(key, defaultValue);
        }

        @Override
        public void set(Boolean value) {
            preferences.edit().putBoolean(key, value).apply();
        }
    }
}
