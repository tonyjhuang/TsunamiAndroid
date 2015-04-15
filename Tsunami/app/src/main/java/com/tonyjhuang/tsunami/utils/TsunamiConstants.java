package com.tonyjhuang.tsunami.utils;

/**
 * Created by tonyhuang on 8/5/14.
 */
public class TsunamiConstants {
    // Request codes
    public static final int PROFILE_REQUEST_CODE = 1;
    public static final int PHOTO_VIEW_REQUEST_CODE = 2;
    public static final int COMMENTS_REQUEST_CODE = 3;
    public static final int BROWSE_WAVES_REQUEST_CODE = 4;
    public static final int SPLASH_REQUEST_CODE = 5;
    public static final int SPLASH_TAKE_PHOTO_REQUEST_CODE = 6;
    public static final int SPLASH_ATTACH_PHOTO_REQUEST_CODE = 7;

    // Result codes
    public static final int SPLASH_NOT_CREATED = 0;
    public static final int SPLASH_CREATED = 1;

    // Intent extras
    public static final String USER_ID_EXTRA = "user_id";
    public static final long USER_ID_EXTRA_DEFAULT = -1;
    public static final String WAVE_ID_EXTRA = "wave_id";
    public static final long WAVE_ID_EXTRA_DEFAULT = -1;

    // Misc
    public static final long NO_CURRENT_USER_ID = -1l;
}
