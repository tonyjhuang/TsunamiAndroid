package com.tonyjhuang.tsunami.api.network;

import android.content.Context;

import com.tonyjhuang.tsunami.api.FileReader;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.Ripple;
import com.tonyjhuang.tsunami.api.models.RipplesResult;
import com.tonyjhuang.tsunami.api.models.WavesResult;
import com.tonyjhuang.tsunami.api.models.parsers.TsunamiGson;
import com.tonyjhuang.tsunami.logging.Timber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tonyjhuang on 8/17/14.
 */
@Singleton
public class TsunamiApiClient {

    private Context context;
    private TsunamiGson gson;

    @Inject
    public TsunamiApiClient(Context context, TsunamiGson gson) {
        this.context = context;
        this.gson = gson;
    }
/*
    public void getAllWaves(final Callback<List<Wave>> callback) {
        call(new Callback<WavesResult>() {
            @Override
            public void success(WavesResult result, Response response) {
                callback.success(result.getWaves(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.d("failed :(");
                callback.failure(error);
            }

        }, WavesResult.class, "waves.json");
    }

    *//**
     * passes null to callback if wave with waveId does not exist
     *//*
    public void getWave(final long waveId, final Callback<Wave> callback) {
        getAllWaves(new Callback<List<Wave>>() {
            @Override
            public void success(List<Wave> result, Response response) {
                for (Wave wave : result) {
                    if (wave.getId() == waveId) {
                        callback.success(wave, response);
                        return;
                    }
                }
                callback.success(null, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.d("failed :(");
                callback.failure(error);
            }
        });
    }

    public void getAllWaveNodes(final Callback<List<Ripple>> callback) {
        call(new Callback<RipplesResult>() {
            @Override
            public void success(RipplesResult result, Response response) {
                callback.success(result.getRipples(), response);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        }, RipplesResult.class, "wavenodes.json");
    }

    public void getWaveNode(final long waveNodeId, final Callback<Ripple> callback) {
        getAllWaveNodes(new Callback<List<Ripple>>() {
            @Override
            public void success(List<Ripple> ripples, Response response) {
                for (Ripple node : ripples) {
                    if (node.getId() == waveNodeId) {
                        callback.success(node, response);
                        return;
                    }
                }
                callback.success(null, response);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    *//**
     * Return all wavenodes for a given wave
     *//*
    public void getWaveNodes(final long waveId, final Callback<List<Ripple>> callback) {
        getWave(waveId, new Callback<Wave>() {
            @Override
            public void success(Wave wave, Response response) {
                final List<Long> ripples = wave.getRipples();

                getAllWaveNodes(new Callback<List<Ripple>>() {
                    @Override
                    public void success(List<Ripple> result, Response response) {
                        List<Ripple> ripples = new ArrayList<Ripple>();
                        for (Ripple node : result) {
                            if (ripples.contains(node.getId())) {
                                ripples.add(node);
                            }
                        }
                        callback.success(ripples, response);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private <T> void call(final Callback<T> callback, Class<T> className, String fileName) {
        FileReader.getObject(context, gson, fileName, className, callback);
    }*/
}
