package com.tonyjhuang.tsunami.ui.main.contentview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.ui.main.comments.CommentsActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

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

    /**
     * The wave that we should be displaying currently.
     */
    private Wave wave;

    public ContentCard(Context context) {
        super(context);
        setup(context);
    }

    public ContentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public ContentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    public void setup(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ButterKnife.inject(this, inflater.inflate(R.layout.card_content, this, true));
    }

    public void setWave(Wave wave) {
        this.wave = wave;
        container.removeAllViews();
        if (wave == null) return;

        String numRipples;
        if (wave.getRipples().size() < 1000)
            numRipples = String.valueOf(wave.getRipples().size());
        else
            numRipples = String.valueOf(wave.getRipples().size() / 1000f) + "k";

        Resources resources = getResources();
        String commentsText;
        int numComments = wave.getComments().size();
        if (numComments == 0) {
            commentsText = resources.getString(R.string.content_comment_prompt_zero);
        } else {
            commentsText = resources.getQuantityString(R.plurals.content_comment_prompts,
                    numComments, numComments);
        }

        alias.setText(wave.getUser().getName());
        comments.setText(commentsText);
        ripples.setText(numRipples);

        ContentInnerView innerView;

        if (wave.getContent().getContentType().equals(WaveContent.ContentType.IMAGE_LINK)) {
            innerView = new ContentInnerImage(getContext());
        } else {
            innerView = new ContentInnerText(getContext());
        }

        innerView.setWave(wave);
        container.addView((View) innerView);
    }


    public Wave getWave() {
        return wave;
    }

    @OnClick(R.id.comments_container)
    public void onCommentsContainerClick(View view) {
        CommentsActivity.startCommentsActivity((TsunamiActivity) getContext(), wave.getId());
    }
}
