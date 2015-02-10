package com.tonyjhuang.patio;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

public class PatioThumbnail extends RelativeLayout {
    private Uri mThumbnailUri;
    private ImageView mThumbnailView;
    private ImageButton mRemoveButton;
    private OnRemoveClickListener onRemoveClickListener;

    public PatioThumbnail(Context context) {
        this(context, null);
    }

    public PatioThumbnail(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatioThumbnail(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.patio_thumbnail, this);
        mThumbnailView = (ImageView) findViewById(R.id.image_view);
        mRemoveButton = (ImageButton) findViewById(R.id.remove);
        mRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRemoveClickListener != null)
                    onRemoveClickListener.onRemoveClicked(PatioThumbnail.this);
            }
        });
    }

    public void setUri(Uri thumbnailUri, int width, int height) {
        mThumbnailUri = thumbnailUri;

        Picasso.with(getContext())
                .load(thumbnailUri)
                .resize(width, height)
                .centerCrop()
                .into(mThumbnailView);
    }

    public Uri getThumbnailUri() {
        return mThumbnailUri;
    }

    public ImageView getThumbnailView() {
        return mThumbnailView;
    }

    public void setOnRemoveClickListener(OnRemoveClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    public static interface OnRemoveClickListener {
        public void onRemoveClicked(PatioThumbnail thumbnail);
    }
}
