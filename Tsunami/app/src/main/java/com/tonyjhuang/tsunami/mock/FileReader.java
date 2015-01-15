package com.tonyjhuang.tsunami.mock;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tony on 1/12/15.
 */
public class FileReader {
    /**
     * Returns a comma delimited String, where each delimited item is a line from the file.
     */
    public static String readFile(Context context, String file) {
        StringBuilder buf = new StringBuilder();
        BufferedReader in;
        try {
            InputStream stream = context.getAssets().open(file);
            in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while(true) {
                String line = in.readLine();
                if(line == null) break;
                buf.append(line);
                buf.append(",");
            }
            in.close();
        } catch (IOException e) {

        }

        return buf.toString();
    }
}
