package com.tonyjhuang.tsunami.ui.nav;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
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
import com.tonyjhuang.tsunami.logging.Timber;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/10/15.
 */
public class NavDrawerFragment extends TsunamiFragment implements DrawerLayout.DrawerListener {

    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.drawer)
    LinearLayout drawer;
    @InjectView(R.id.cover)
    ImageView cover;

    private OnDrawerItemSelectedListener listener;
    private NavigationItem selectedNavigationItem;
    private WeakReference<DrawerLayout> drawerLayoutWeakReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        drawerLayoutWeakReference = new WeakReference<>(drawerLayout);
        drawerLayout.setDrawerListener(this);
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
        NavigationItem discoverNavItem = addNavigationItem("Discover",
                R.drawable.nav_explore_grey,
                R.drawable.nav_explore_black,
                DrawerItem.DISCOVER);

        addNavigationItem("Stats",
                R.drawable.nav_stats_grey,
                R.drawable.nav_stats_black,
                DrawerItem.STATS);

        selectNavigationItem(discoverNavItem);
    }

    private NavigationItem addNavigationItem(String text, int imageResource, int selectedImageResource, DrawerItem drawerItem) {
        NavigationItem navItem = new NavigationItem(text, imageResource, selectedImageResource, drawerItem);
        View navItemView = navItem.createView(getActivity().getLayoutInflater());
        drawer.addView(navItemView);
        navItemView.setOnClickListener((view) -> selectNavigationItem(navItem));
        return navItem;
    }

    private void selectNavigationItem(NavigationItem navItem) {
        Timber.d("selectNavigationItem");
        if (navItem.isSelected()) {
            closeDrawers();
            return;
        }
        if (selectedNavigationItem != null) selectedNavigationItem.setSelected(false);
        navItem.setSelected(true);
        selectedNavigationItem = navItem;
        closeDrawers();
    }

    private void closeDrawers() {
        if (drawerLayoutWeakReference.get() != null)
            drawerLayoutWeakReference.get().closeDrawers();
    }

    public void setOnDrawerItemSelectedListener(OnDrawerItemSelectedListener listener) {
        this.listener = listener;
        Timber.d("set listener");
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        Timber.d("onDrawerStateChanged; " + newState);
        if (newState == DrawerLayout.STATE_IDLE) {
            Timber.d("idle!");
            if (listener != null) {
                listener.onDrawerItemSelected(selectedNavigationItem.drawerItem);
                Timber.d("notifying listener");
            }
        }
    }

    public static interface OnDrawerItemSelectedListener {
        public void onDrawerItemSelected(DrawerItem drawerItem);
    }

    public static enum DrawerItem {
        DISCOVER, STATS
    }

    private static class NavigationItem {
        String text;
        int imageResource, selectedImageResource;
        int textColorResource, selectedTextColorResource;
        int backgroundColorResource, selectedBackgroundColorResource;
        DrawerItem drawerItem;
        WeakReference<View> view;
        private boolean selected;

        private NavigationItem(String text, int imageResource, int selectedImageResource, DrawerItem drawerItem) {
            this.text = text;
            this.imageResource = imageResource;
            this.selectedImageResource = selectedImageResource;
            this.drawerItem = drawerItem;
        }

        public View createView(LayoutInflater inflater) {
            init(inflater.getContext());

            View view = inflater.inflate(R.layout.row_nav, null);
            ((ImageView) ButterKnife.findById(view, R.id.icon)).setImageResource(imageResource);
            ((TextView) ButterKnife.findById(view, R.id.text)).setText(text);
            this.view = new WeakReference<>(view);
            return view;
        }

        private void init(Context context) {
            Resources resources = context.getResources();
            textColorResource = resources.getColor(R.color.text_primary);
            selectedTextColorResource = resources.getColor(R.color.text_primary);
            backgroundColorResource = resources.getColor(android.R.color.transparent);
            selectedBackgroundColorResource = resources.getColor(R.color.selected_item_overlay);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (view.get() == null) return;
            View view = this.view.get();
            int imageResource = selected ? this.selectedImageResource : this.imageResource;
            int textColorResource = selected ? this.selectedTextColorResource : this.textColorResource;
            int backgroundColorResource = selected ? this.selectedBackgroundColorResource : this.backgroundColorResource;

            ((TextView) ButterKnife.findById(view, R.id.text)).setTextColor(textColorResource);
            ((ImageView) ButterKnife.findById(view, R.id.icon)).setImageResource(imageResource);
            ButterKnife.findById(view, R.id.background).setBackgroundColor(backgroundColorResource);
        }

        public boolean isSelected() {
            return selected;
        }
    }
}
