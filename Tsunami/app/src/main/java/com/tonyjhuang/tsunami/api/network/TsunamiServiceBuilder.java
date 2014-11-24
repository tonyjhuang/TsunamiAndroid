package com.tonyjhuang.tsunami.api.network;

import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.api.parsers.TsunamiGson;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

/**
 * Helper class to build out our TsunamiService network interface.
 * Created by tonyjhuang on 9/28/14.
 */
public class TsunamiServiceBuilder {
    public static final String ENDPOINT = "http://tsunami-mobile.herokuapp.com/api";

    private static RequestInterceptor headerInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", "Android");
        }
    };

    public static TsunamiService build() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(headerInterceptor)
                .setConverter(new GsonConverter(TsunamiGson.buildGson()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("asdsadasd"));

        if(BuildConfig.DEBUG) {
            ;//builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(TsunamiService.class);
    }
}
