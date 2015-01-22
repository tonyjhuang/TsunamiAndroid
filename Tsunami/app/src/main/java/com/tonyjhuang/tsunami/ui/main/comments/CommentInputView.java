package com.tonyjhuang.tsunami.ui.main.comments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.logging.Timber;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 1/18/15.
 */
public class CommentInputView extends RelativeLayout {

    @InjectView(R.id.handle_container)
    LinearLayout handleContainer;
    @InjectView(R.id.comment_input)
    EditText input;

    private SendRequestListener listener;
    private boolean handleContainerHidden = false;

    public CommentInputView(Context context) {
        this(context, null);
    }

    public CommentInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.inject(this, inflate(context, R.layout.view_comment_input, this));
        init();
    }

    private void init() {
        handleContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            handleContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            handleContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        hideHandleContainer(true);
                        hideHandleContainer(false);
                        hideHandleContainer(true);
                    }
                });


        input.addTextChangedListener(
                new SimpleTextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        hideHandleContainer(s.length() == 0);
                    }
                }

        );
    }

    private void hideHandleContainer(boolean hide) {
        if (handleContainer == null || (hide == handleContainerHidden)) return;

        handleContainerHidden = hide;
        if (!hide) handleContainer.setVisibility(VISIBLE);
        int target = hide ? -handleContainer.getHeight() : 0;

        final LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) handleContainer.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(params.bottomMargin, target);
        animator.addUpdateListener((a) -> {
            params.bottomMargin = (Integer) a.getAnimatedValue();
            handleContainer.setLayoutParams(params);
        });
        animator.setDuration(250);
        animator.setInterpolator(hide ? new AccelerateInterpolator() : new OvershootInterpolator());
        animator.start();
    }

    @OnClick(R.id.confirm)
    public void onConfirmClick(View view) {
        if (listener != null) listener.onSendRequested(input.getText().toString());
    }

    public void clear() {
        input.setText("");
    }

    public void setOnSendRequestListener(SendRequestListener listener) {
        this.listener = listener;
    }

    public static interface SendRequestListener {
        /**
         * Triggered when the user clicks the send button.
         *
         * @param string The current text entered in the our input field.
         */
        public void onSendRequested(String string);
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}