package com.tonyjhuang.tsunami.logging;

import com.localytics.android.Localytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tony on 4/13/15.
 */
public class TLocalytics {
    public static final String SCREEN_MAIN = "Main";
    public static final String SCREEN_SPLASH = "Splash";
    public static final String SCREEN_PROFILE = "Profile";
    public static final String SCREEN_COMMENTS = "Comments";
    public static final String SCREEN_WAVES = "Waves";

    public static void tagScreen(String screen) {
        Localytics.openSession();
        Localytics.tagScreen(screen);
        Localytics.upload();
    }

    public static final String TAG_SPLASH_TAB_ICON_CLICKED = "Splash Tab Icon Clicked";
    public static final String ATTR_SPLASH_TAB = "tab";

    public static void tagTabClickEvent(String tab) {
        Map<String, String> values = new HashMap<>();
        values.put(TLocalytics.ATTR_SPLASH_TAB, tab);
        Localytics.tagEvent(TLocalytics.TAG_SPLASH_TAB_ICON_CLICKED, values);
    }

    public static final String TAG_WAVE_SPLASH = "Wave Splashed";
    public static final String TAG_WAVE_VIEWED = "Wave Viewed";
    public static final String TAG_WAVE_RIPPLED = "Wave Rippled";
    public static final String TAG_COMMENTS_VIEWED = "Comments Viewed";
    public static final String TAG_COMMENT_CREATED = "Comment Created";

    public static final String TAG_PROFILE_PIC_CLICKED = "Profile Pic Clicked";
    public static final String ATTR_PROFILE_PIC_LOCATION = "location";

    public static void tagProfilePicClicked(String location) {
        Map<String, String> values = new HashMap<>();
        values.put(TLocalytics.ATTR_PROFILE_PIC_LOCATION, location);
        Localytics.tagEvent(TLocalytics.TAG_PROFILE_PIC_CLICKED, values);
    }

}
