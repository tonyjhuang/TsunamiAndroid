package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.injection.ProfileModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tony on 12/21/14.
 */
public class ProfileActivity extends TsunamiActivity {

    public static void startProfileActivity(TsunamiActivity activity) {
        activity.startActivityForResult(
                new Intent(activity, ProfileActivity.class), TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new ProfileModule());
    }
}
