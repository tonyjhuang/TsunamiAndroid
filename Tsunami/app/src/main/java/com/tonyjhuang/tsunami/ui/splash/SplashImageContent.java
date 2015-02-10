package com.tonyjhuang.tsunami.ui.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tonyjhuang.patio.Patio;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 2/10/15.
 */
public class SplashImageContent extends RelativeLayout implements
        Patio.PatioCallbacks,
        SplashContentView {

    @InjectView(R.id.patio)
    Patio patio;

    private WeakReference<TsunamiFragment> fragmentWeakReference;

    public SplashImageContent(Context context) {
        this(context, null);
    }

    public SplashImageContent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashImageContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_splash_content_image, this);
        ButterKnife.inject(this, this);

        setup();
    }

    private void setup() {
        patio.setCallbacksListener(this);
    }

    @Override
    public void setFragment(TsunamiFragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void onTakePictureClick() {
        Intent intent = patio.getTakePictureIntent();
        if (fragmentWeakReference.get() != null)
            fragmentWeakReference.get().startActivityForResult(intent, TsunamiConstants.SPLASH_TAKE_PHOTO_REQUEST_CODE);
    }

    @Override
    public void onAddPictureClick() {
        Intent intent = patio.getAttachPictureIntent();
        if (fragmentWeakReference.get() != null)
            fragmentWeakReference.get().startActivityForResult(intent, TsunamiConstants.SPLASH_ATTACH_PHOTO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult!");
        if (resultCode == Activity.RESULT_OK && requestCode == TsunamiConstants.SPLASH_TAKE_PHOTO_REQUEST_CODE) {
            patio.handleTakePictureResult(data);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == TsunamiConstants.SPLASH_ATTACH_PHOTO_REQUEST_CODE) {
            patio.handleAttachPictureResult(data);
        }
    }
}