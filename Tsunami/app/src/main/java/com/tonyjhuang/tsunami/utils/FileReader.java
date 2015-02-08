package com.tonyjhuang.tsunami.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observable;

/**
 * Created by tony on 2/8/15.
 */
public class FileReader {

    /**
     * (potentially) ASYNCHRONOUS observable to get a random line from a file in assets.
     */
    public static Observable<String> getRandomLine(Context context, String fileName) {
        return Observable.create((subscriber) -> {

            BufferedReader reader = null;
            try {
                List<String> lines = new ArrayList<>();
                reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));

                String line = reader.readLine();
                while (line != null) {
                    lines.add(line);
                    line = reader.readLine();
                }

                String randomLine = lines.get(new Random().nextInt(lines.size()));

                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(randomLine);
                if (!subscriber.isUnsubscribed())
                    subscriber.onCompleted();
            } catch (IOException e) {
                if (!subscriber.isUnsubscribed())
                    subscriber.onError(e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onError(e);
                    }
                }
            }
        });
    }
}
