package com.tonyjhuang.tsunami.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by tony on 12/27/14.
 */
public class ScreenManager {
    private ScreenManager() {}

    public static Point getScreenDimens(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
