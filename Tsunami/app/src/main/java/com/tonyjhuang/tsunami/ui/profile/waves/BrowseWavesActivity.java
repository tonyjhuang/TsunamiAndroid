package com.tonyjhuang.tsunami.ui.profile.waves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.injection.BrowseWavesModule;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by tony on 1/27/15.
 */
public class BrowseWavesActivity extends TsunamiActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public static void startBrowseWavesActivity(TsunamiActivity activity) {
        Intent intent = new Intent(activity, BrowseWavesActivity.class);
        activity.startActivityForResult(intent, TsunamiConstants.BROWSE_WAVES_REQUEST_CODE);
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new BrowseWavesModule());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_waves);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.getBackground().setAlpha(255);
    }


}
