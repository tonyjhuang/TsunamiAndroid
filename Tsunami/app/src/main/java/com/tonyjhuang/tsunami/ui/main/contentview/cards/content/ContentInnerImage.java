package com.tonyjhuang.tsunami.ui.main.contentview.cards.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.ui.shared.PhotoViewActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import org.ocpsoft.prettytime.PrettyTime;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 1/14/15.
 */
public class ContentInnerImage extends LinearLayout implements ContentInnerView{

    private static PrettyTime prettyTime = new PrettyTime();

    @InjectView(R.id.image)
    ImageView imageView;
    @InjectView(R.id.metadata)
    ContentInnerMetadata metadata;
    @InjectView(R.id.text)
    TextView caption;

    private Wave wave;

    public ContentInnerImage(Context context) {
        this(context, null);
    }

    public ContentInnerImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentInnerImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.card_inner_content_image, this, true);
        ButterKnife.inject(this, this);
    }

    @Override
    public void setWave(Wave wave) {
        this.wave = wave;
        if (wave == null) return;

        Picasso.with(getContext())
                .load(wave.getContent().getBody())
                .fit()
                .centerCrop()
                .into(imageView);
        caption.setText(wave.getContent().getTitle());
        metadata.setWave(wave);
    }

    @OnClick(R.id.image)
    public void onImageClick(View view) {
        PhotoViewActivity.startPhotoViewActivity(((TsunamiActivity) getContext()), wave.getContent().getBody());
    }
}
