package com.tonyjhuang.tsunami.utils;

import android.os.Bundle;

import com.tonyjhuang.tsunami.R;

/**
 * Created by tony on 1/15/15.
 */
public abstract class SingleFragmentActivity extends TsunamiActivity {

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
}
