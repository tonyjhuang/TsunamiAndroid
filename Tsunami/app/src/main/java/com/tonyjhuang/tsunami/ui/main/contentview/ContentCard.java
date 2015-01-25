package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.main.comments.CommentsActivity;
import com.tonyjhuang.tsunami.ui.main.comments.CommentsFragment;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tonyjhuang on 9/7/14.
 */
public class ContentCard extends FrameLayout {
    @InjectView(R.id.alias)
    TextView alias;
    @InjectView(R.id.ripples)
    TextView ripples;
    @InjectView(R.id.comments_text)
    TextView comments;
    @InjectView(R.id.content_container)
    FrameLayout container;

    @Inject
    Bus bus;
    @Inject
    Resources resources;

    /**
     * The wave that we should be displaying currently.
     */
    private Wave wave;

    public ContentCard(Context context) {
        this(context, null);
    }

    public ContentCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    public void setup(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ButterKnife.inject(this, inflater.inflate(R.layout.card_content, this, true));
        ((TsunamiActivity) context).inject(this);
    }

    public void setWave(Wave wave) {
        if (wave == null) return;

        String numRipples;
        if (wave.getRipples().size() < 1000)
            numRipples = String.valueOf(wave.getRipples().size());
        else
            numRipples = String.valueOf(wave.getRipples().size() / 1000f) + "k";

        updateCommentsText(wave);
        alias.setText(wave.getUser().getName());
        ripples.setText(numRipples);

        // Set the content view. If we are getting an updated version, skip this.
        if (this.wave == null || wave.getId() != this.wave.getId()) {
            ContentInnerView innerView;
            if (wave.getContent().getContentType().equals(WaveContent.ContentType.IMAGE_LINK)) {
                innerView = new ContentInnerImage(getContext());
            } else {
                innerView = new ContentInnerText(getContext());
            }
            container.removeAllViews();
            innerView.setWave(wave);
            container.addView((View) innerView);
        }
        this.wave = wave;
    }


    public Wave getWave() {
        return wave;
    }

    private void updateCommentsText(Wave wave) {
        String commentsText;
        int numComments = wave.getComments().size();
        if (numComments == 0) {
            commentsText = resources.getString(R.string.content_comment_prompt_zero);
        } else {
            commentsText = resources.getQuantityString(R.plurals.content_comment_prompts,
                    numComments, numComments);
        }
        comments.setText(commentsText);
    }

    @OnClick(R.id.comments_container)
    public void onCommentsContainerClick(View view) {
        CommentsActivity.startCommentsActivity((TsunamiActivity) getContext(), wave.getId());
    }

    @Subscribe
    public void commentAdded(CommentsFragment.CommentAddedEvent event) {
        setWave(event.wave);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bus.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bus.unregister(this);
    }
}
