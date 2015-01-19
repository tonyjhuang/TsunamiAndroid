package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tonyjhuang.tsunami.injection.ProfileModule;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnScrollListener;
import com.tonyjhuang.tsunami.utils.SingleFragmentActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tony on 1/15/15.
 */
public class ProfileActivity extends SingleFragmentActivity implements OnScrollListener {

    private Toolbar toolbar;
    private ProfileFragment fragment;


    public static void startProfileActivity(TsunamiActivity activity) {
        activity.startActivityForResult(
                new Intent(activity, ProfileActivity.class), TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    public TsunamiFragment getFragment() {
        fragment = ProfileFragment.getInstance(null);
        return fragment;
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new ProfileModule());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = inflateToolbar(true);
        setToolbarBackgroundAlpha(0);

        new Handler().post(() -> fragment.scrollView.setOnScrollListener(this));
    }

    /**
     * @param alpha 0 is transparent, 255 is opaque
     */
    private void setToolbarBackgroundAlpha(int alpha) {
        toolbar.getBackground().setAlpha(alpha);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        int coverImageHeight = fragment.coverImage.getHeight();
        int ratio = (int) (((float) Math.min(Math.max(t, 0), coverImageHeight)) / coverImageHeight);
        setToolbarBackgroundAlpha(ratio * 255);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }*/
}
