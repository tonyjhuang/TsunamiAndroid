package com.tonyjhuang.tsunami.ui.splash;

import android.content.Intent;

import com.tonyjhuang.tsunami.utils.TsunamiFragment;

/**
 * Represents a helper view for getting splash content.
 * CONTRACT: Any class that implements this interface must superclass View
 */
public interface SplashContentView {
    public void onActivityResult(int requestCode, int resultCode, Intent data);
    public void setFragment(TsunamiFragment fragment);
}
