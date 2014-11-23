package com.tonyjhuang.tsunami.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by tony on 11/23/14.
 * To use a preference:
 *      prefs.id.get();
 *      prefs.id.set("....");
 *
 * To create a TsunamiPreference, simply create a new class-scoped instance variable with the appropriate constructor
 *
 * Warning: whatever default value you create a TsunamiPreference with will be it's starting value, if one does
 * not exist, after the first get. Meaning, if you look at a newly installed preferences file, it will be empty.
 * If, however, you call id.get() without id.set(), the preferences file will now have a new id written and this
 * id will be passed to all future get() calls.
 *
 */
public class TsunamiPreferences {
    private static final String SHARED_PREFERENCES = "com.tonyjhuang.tsunami.shared_prefs";

    public StringPreference id = new StringPreference("com.tonyjhuang.tsunami.id", UUID.randomUUID().toString());


    private SharedPreferences preferences;

    public TsunamiPreferences(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private abstract class TsunamiPreference<T> {
        public abstract T get();

        public abstract void set(T value);
    }

    private class StringPreference extends TsunamiPreference<String> {
        private String key;
        private String defaultValue;

        public StringPreference(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public String get() {
            String value = preferences.getString(key, defaultValue);
            if(value.equals(defaultValue)) {
                set(defaultValue);
            }
            return value;
        }

        @Override
        public void set(String value) {
            preferences.edit().putString(key, value).apply();
        }
    }
}
