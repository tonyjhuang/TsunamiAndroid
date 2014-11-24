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
    public StringPreference id = new IdPreference("com.tonyjhuang.tsunami.id");

    private SharedPreferences preferences;

    public TsunamiPreferences(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private abstract class TsunamiPreference<T> {
        public abstract T get();

        public abstract void set(T value);
    }

    private class IdPreference extends StringPreference {
        /**
         * In memory instance of our id so we don't have to keep fetching it from file. The reason for
         * this is because we'll probably be retrieving the user's id a lot.
         */
        private String id;

        public IdPreference(String key) {
            super(key, null);

            id = get();
            if (id == null) {
                set(UUID.randomUUID().toString());
            }
        }

        @Override
        public String get() {
            return id;
        }

        @Override
        public void set(String value) {
            super.set(value);
            id = value;
        }
    }

    private class StringPreference extends TsunamiPreference<String> {
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

    private class BooleanPreference extends TsunamiPreference<Boolean> {
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
