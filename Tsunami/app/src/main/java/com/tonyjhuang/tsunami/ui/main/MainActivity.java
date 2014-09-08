package com.tonyjhuang.tsunami.ui.main;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.tonyjhuang.tsunami.BuildConfig;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiActivity;
import com.tonyjhuang.tsunami.logging.Timber;

import butterknife.InjectView;


public class MainActivity extends TsunamiActivity {
    //@InjectView(R.id.map)
    //MapFragment mapContainer;
    @InjectView(R.id.content_container)
    FrameLayout contentContainer;

    public static void startMainActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        setActionBarTitle("TSUNAMI");
        if (savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_container, new ContentFragment());
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_log_out) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
