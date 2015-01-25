package com.tonyjhuang.tsunami.ui.shared;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.injection.MainModule;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by tony on 1/15/15.
 */
public class PhotoViewActivity extends TsunamiActivity {
    public static final String IMAGE_URL = "image_url";

    @InjectView(R.id.photo_view)
    PhotoView photoView;

    public static void startPhotoViewActivity(TsunamiActivity activity, String imageUrl) {
        Intent intent = new Intent(activity, PhotoViewActivity.class);
        intent.putExtra(IMAGE_URL, imageUrl);
        activity.startActivityForResult(intent, TsunamiConstants.PHOTO_VIEW_REQUEST_CODE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        String imageUrl = getIntent().getStringExtra(IMAGE_URL);
        Picasso.with(this).load(imageUrl).into(photoView);
        PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
        attacher.setOnPhotoTapListener((view, x, y) -> finish());
        attacher.setOnViewTapListener((view, x, y) -> finish());

    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new MainModule(this));
    }
}
