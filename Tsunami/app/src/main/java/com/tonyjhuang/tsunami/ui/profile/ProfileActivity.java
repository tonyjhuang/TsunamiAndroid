package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.injection.ProfileModule;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by tony on 12/21/14.
 */
public class ProfileActivity extends TsunamiActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public static void startProfileActivity(TsunamiActivity activity) {
        activity.startActivityForResult(
                new Intent(activity, ProfileActivity.class), TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new ProfileModule());
    }

    /**
     * @param alpha 0 is transparent, 255 is opaque
     */
    private void setToolbarBackgroundAlpha(int alpha) {
        toolbar.getBackground().setAlpha(alpha);
    }
}
