package com.tonyjhuang.tsunami.ui.main.contentview.cards.content;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.models.WaveContent;
import com.tonyjhuang.tsunami.ui.main.comments.CommentsActivity;
import com.tonyjhuang.tsunami.ui.main.comments.CommentsFragment;
import com.tonyjhuang.tsunami.ui.main.contentview.cards.TsunamiCard;
import com.tonyjhuang.tsunami.ui.profile.ProfileActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tonyjhuang on 9/7/14.
 */
public class ContentCard extends TsunamiCard {
    @InjectView(R.id.profile_pic)
    ImageView profilePic;
    @InjectView(R.id.author)
    TextView author;
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
    private WeakReference<TsunamiActivity> activityWeakReference;

    public ContentCard(Context context) {
        this(context, null);
    }

    public ContentCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((TsunamiActivity) context).inject(this);
        activityWeakReference = new WeakReference<>((TsunamiActivity) context);
    }

    @Override
    protected View getInnerView(Context context, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_content, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    public void setWave(Wave wave) {
        if (wave == null) return;
        updateCommentsText(wave);
        // Set the content view. If we are getting an updated version of the wave, skip this.
        if (this.wave == null || wave.getId() != this.wave.getId()) {
            View.OnClickListener userOnClickListener = getOnClickListener(wave.getUser());
            profilePic.setOnClickListener(userOnClickListener);
            author.setOnClickListener(userOnClickListener);

            author.setText(wave.getUser().getName());
            setContentInnerView(wave);
        }
        this.wave = wave;
    }

    private View.OnClickListener getOnClickListener(User user) {
        return (view) -> {
            if (activityWeakReference.get() == null) return;
            ProfileActivity.startProfileActivity(activityWeakReference.get(), user.getId());
        };
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

    private void setContentInnerView(Wave wave) {
        ContentInnerView innerView;
        if (wave.getContent().getType().equals(WaveContent.ContentType.image_link)) {
            innerView = new ContentInnerImage(getContext());
        } else {
            innerView = new ContentInnerText(getContext());
        }
        container.removeAllViews();
        innerView.setWave(wave);
        container.addView((View) innerView);
    }

    @OnClick(R.id.comments_container)
    public void onCommentsContainerClick(View view) {
        CommentsActivity.startCommentsActivity((TsunamiActivity) getContext(), wave.getId());
    }

    @Subscribe
    public void commentAdded(CommentsFragment.CommentAddedEvent event) {
        if (event.wave.getId() == wave.getId())
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
