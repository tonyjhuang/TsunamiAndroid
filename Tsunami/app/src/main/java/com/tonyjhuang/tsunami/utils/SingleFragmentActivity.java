package com.tonyjhuang.tsunami.utils;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tonyjhuang.tsunami.R;

import butterknife.InjectView;

/**
 * Created by tony on 1/15/15.
 */
public abstract class SingleFragmentActivity extends TsunamiActivity {

    @InjectView(R.id.toolbar_stub)
    ViewStub toolbarStub;
    @InjectView(R.id.container)
    FrameLayout container;
    Toolbar toolbar;

    private boolean toolbarInflated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        if (savedInstanceState == null) {
            TsunamiFragment fragment = getFragment();
            if (fragment != null) setFragment(fragment);
        }
    }

    // Instance of the fragment you want in the container.
    public abstract TsunamiFragment getFragment();

    protected void setFragment(TsunamiFragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    public TsunamiFragment getActiveFragment() {
        return (TsunamiFragment) getFragmentManager().findFragmentById(R.id.container);
    }

    /**
     * Should the fragment sit below the toolbar or behind it?
     */
    protected void setContentBelowToolbar(boolean below) {
        if (!toolbarInflated || container == null) return;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
        if (below) {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                layoutParams.removeRule(RelativeLayout.BELOW);
            else
                layoutParams.addRule(RelativeLayout.BELOW, 0);
        }
        container.setLayoutParams(layoutParams);
    }

    protected Toolbar inflateToolbar(boolean setup) {
        if (toolbarInflated) return toolbar;

        toolbar = (Toolbar) toolbarStub.inflate();
        toolbarInflated = true;

        if (setup) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        return toolbar;
    }
}
