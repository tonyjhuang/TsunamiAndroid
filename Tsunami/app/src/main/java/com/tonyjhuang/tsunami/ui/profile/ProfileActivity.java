package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.tonyjhuang.tsunami.injection.ProfileModule;
import com.tonyjhuang.tsunami.logging.Timber;
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

    ProfileFragment fragment;
    Handler handler = new Handler();

    public static void startProfileActivity(TsunamiActivity activity) {
        activity.startActivityForResult(
                new Intent(activity, ProfileActivity.class), TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    public TsunamiFragment getFragment() {
        return ProfileFragment.getInstance(null);
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new ProfileModule());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateToolbar(true);
        setToolbarBackgroundAlpha(0);

        fragment = (ProfileFragment) getActiveFragment();
        handler.post(() -> fragment.scrollView.setOnScrollListener(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(() -> setToolbarBackgroundAlpha(fragment.scrollView.getScrollY()));

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        setToolbarBackgroundAlpha(t);
    }

    protected void setToolbarBackgroundAlpha(int t) {
        handler.post(() -> {
            int coverImageHeight = fragment.coverImage.getHeight();
            float ratio = (((float) Math.min(Math.max(t, 0), coverImageHeight)) / coverImageHeight);
            super.setToolbarBackgroundAlpha((int) (ratio * 255));
        });
    }
}
