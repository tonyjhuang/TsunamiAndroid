package com.tonyjhuang.tsunami.ui.profile;

import android.os.Bundle;

import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.injection.ProfileModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tony on 12/21/14.
 */
public class ProfileActivity extends TsunamiActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new ProfileModule());
    }
}
