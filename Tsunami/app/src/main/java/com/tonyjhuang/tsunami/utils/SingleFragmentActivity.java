package com.tonyjhuang.tsunami.utils;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewStub;

import com.tonyjhuang.tsunami.R;

import butterknife.InjectView;

/**
 * Created by tony on 1/15/15.
 */
public abstract class SingleFragmentActivity extends TsunamiActivity {

    @InjectView(R.id.toolbar_stub)
    ViewStub toolbarStub;

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

    protected Toolbar inflateToolbar() {

        return (Toolbar) toolbarStub.inflate();
    }
}