package com.tonyjhuang.tsunami.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by tony on 11/23/14.
 * To use a preference:
 * prefs.id.get();
 * prefs.id.set("....");
 * <p/>
 * To create a TsunamiPreference, simply create a new class-scoped instance variable with the appropriate constructor
 */
public class TsunamiPreferences {
    private static final String SHARED_PREFERENCES = "com.tonyjhuang.tsunami.shared_prefs";

    /**
     * Preference key-value pairs that you can get-set
     */
    public final IdPreference id = new IdPreference("com.tonyjhuang.tsunami.id");

    private SharedPreferences preferences;

    public TsunamiPreferences(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        id.initialize();
    }

    abstract class TsunamiPreference<T> {
        public abstract T get();

        public abstract void set(T value);
    }

    public class IdPreference extends StringPreference {
        public IdPreference(String key) {
            super(key, null);
        }

        public void initialize() {
            if(get() == null) {
                set(UUID.randomUUID().toString());
            }
        }
    }

    public class StringPreference extends TsunamiPreference<String> {
        private String key;
        private String defaultValue;

        public StringPreference(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
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

    public class BooleanPreference extends TsunamiPreference<Boolean> {
        private String key;
        private boolean defaultValue;

        public BooleanPreference(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
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
