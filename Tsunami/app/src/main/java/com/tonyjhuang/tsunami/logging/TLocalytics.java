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
        values.put(ATTR_SPLASH_TAB, tab);
        Localytics.tagEvent(TAG_SPLASH_TAB_ICON_CLICKED, values);
    }

    public static final String TAG_WAVE_SPLASH = "Wave Splashed";
    public static final String TAG_COMMENTS_VIEWED = "Comments Viewed";
    public static final String TAG_COMMENT_CREATED = "Comment Created";

    public static final String TAG_PROFILE_PIC_CLICKED = "Profile Pic Clicked";
    public static final String ATTR_PROFILE_PIC_LOCATION = "location";

    public static void tagProfilePicClicked(String location) {
        Map<String, String> values = new HashMap<>();
        values.put(ATTR_PROFILE_PIC_LOCATION, location);
        Localytics.tagEvent(TAG_PROFILE_PIC_CLICKED, values);
    }


    public static final String ERROR_WAVE_SPLASH = "Error Wave Splashed";
    public static final String ATTR_ERROR_MESSAGE = "message";
    public static void tagError(String error, String message) {
        Map<String, String> values = new HashMap<>();
        values.put(ATTR_ERROR_MESSAGE, message);
        Localytics.tagEvent(ERROR_WAVE_SPLASH, values);
    }

    public static class Session {
        private static Map<String, String> session = new HashMap<>();
        private static final String TAG_SESSION = "Session";
        private static final String ATTR_SESSION_VIEWS = "views";
        private static final String ATTR_SESSION_RIPPLES = "ripples";
        public static void startSession() {
            session = new HashMap<>();
            session.put(ATTR_SESSION_VIEWS, "0");
            session.put(ATTR_SESSION_RIPPLES, "0");
        }
        public static void addViewToSession() {
            int currentViews = Integer.parseInt(session.get(ATTR_SESSION_VIEWS));
            session.put(ATTR_SESSION_VIEWS, String.valueOf(currentViews + 1));
        }
        public static void addRippleToSession() {
            int currentRipples = Integer.parseInt(session.get(ATTR_SESSION_RIPPLES));
            session.put(ATTR_SESSION_RIPPLES, String.valueOf(currentRipples + 1));
        }
        public static void endSession() {
            Localytics.tagEvent(TAG_SESSION, session);
        }
    }
}
