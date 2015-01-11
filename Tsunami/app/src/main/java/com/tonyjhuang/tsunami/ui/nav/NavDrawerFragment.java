package com.tonyjhuang.tsunami.ui.nav;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/10/15.
 */
public class NavDrawerFragment extends TsunamiFragment {

    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.drawer)
    LinearLayout drawer;
    @InjectView(R.id.cover)
    ImageView cover;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setNavigationDrawerWidth();
        adjustCoverHeight();
        addNavigationItems();
    }

    private void setNavigationDrawerWidth() {
        // Get screen size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float drawerMaxWidth = getResources().getDimension(R.dimen.drawer_max_width);
        float drawerWidth;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawerWidth = (int) (size.x - getResources().getDimension(R.dimen.action_bar_height));
        } else {
            drawerWidth = size.x / 2;
        }

        drawerWidth = Math.min(drawerWidth, drawerMaxWidth);

        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        layoutParams.width = (int) drawerWidth;
        container.setLayoutParams(layoutParams);
    }

    private void adjustCoverHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusBarHeight = (int) getResources().getDimension(R.dimen.status_bar_height);

            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            layoutParams.height += statusBarHeight;
            cover.setLayoutParams(layoutParams);
        }
    }

    private void addNavigationItems() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View discoverRow = inflater.inflate(R.layout.row_nav, null);
        ((ImageView) ButterKnife.findById(discoverRow, R.id.icon)).setImageResource(R.drawable.nav_discover_unselected);
        ((TextView) ButterKnife.findById(discoverRow, R.id.text)).setText("Discover");
        drawer.addView(discoverRow);
    }
}
