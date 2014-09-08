package com.tonyjhuang.tsunami.api;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tonyjhuang.tsunami.api.models.WavesResult;
import com.tonyjhuang.tsunami.api.models.parsers.TsunamiGson;
import com.tonyjhuang.tsunami.logging.Timber;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import retrofit.Callback;

/**
 * Created by tonyjhuang on 8/17/14.
 */
public class FileReader {

    public static HashMap<String, String> cache = new HashMap<String, String>();

    public static String loadJSONFromAsset(Context context, String filename) {
        if(cache.containsKey(filename)) {
            return cache.get(filename);
        }
        String json = null;
        try {

            InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        cache.put(filename, json);

        return json;

    }

    public static <T> void getObject(final Context context,
                                     final TsunamiGson gson,
                                     final String filename,
                                     final Class<T> className,
                                     final Callback<T> callback) {
        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... voids) {
                String json = FileReader.loadJSONFromAsset(context, filename);
                return gson.fromJson(json, className);
            }

            @Override
            protected void onPostExecute(T result) {
                callback.success(result, null);
            }
        };
        task.execute();
    }
}
