package com.tonyjhuang.tsunami.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.injection.ProfileModule;
import com.tonyjhuang.tsunami.logging.TLocalytics;
import com.tonyjhuang.tsunami.ui.customviews.scrollview.OnScrollListener;
import com.tonyjhuang.tsunami.utils.SingleFragmentActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by tony on 1/15/15.
 */
public class ProfileActivity extends SingleFragmentActivity implements OnScrollListener {

    @Inject
    TsunamiApi api;

    ProfileFragment fragment;
    Handler handler = new Handler();
    private int toolbarAlpha = 0;

    public static void startProfileActivity(TsunamiActivity activity) {
        startProfileActivity(activity, TsunamiConstants.USER_ID_EXTRA_DEFAULT);
    }

    public static void startProfileActivity(TsunamiActivity activity, long userId) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(TsunamiConstants.USER_ID_EXTRA, userId);
        activity.startActivityForResult(intent, TsunamiConstants.PROFILE_REQUEST_CODE);
    }

    @Override
    public TsunamiFragment getFragment() {
        return ProfileFragment.getInstance(getUserIdExtra());
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new ProfileModule());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateToolbar(true);
        subscribe(api.getCurrentUserId(), (currentUserId) -> {});

        fragment = (ProfileFragment) getActiveFragment();
        handler.post(() -> fragment.scrollView.setOnScrollListener(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        TLocalytics.tagScreen(TLocalytics.SCREEN_PROFILE);
        setToolbarBackgroundAlpha(toolbarAlpha);
    }

    private long getUserIdExtra() {
        return getIntent().getLongExtra(TsunamiConstants.USER_ID_EXTRA, TsunamiConstants.USER_ID_EXTRA_DEFAULT);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        int coverImageHeight = fragment.coverImage.getHeight();
        float ratio = (((float) Math.min(Math.max(t, 0), coverImageHeight)) / coverImageHeight);
        toolbarAlpha = (int) (ratio * 255);
        setToolbarBackgroundAlpha(toolbarAlpha);
    }
}
