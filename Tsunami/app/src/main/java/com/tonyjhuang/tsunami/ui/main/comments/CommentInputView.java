package com.tonyjhuang.tsunami.ui.main.comments;

import android.animation.Animator;
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
import com.tonyjhuang.tsunami.utils.SimpleAnimatorListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tony on 1/18/15.
 */
public class CommentInputView extends RelativeLayout {

    @InjectView(R.id.handle_container)
    LinearLayout handleContainer;
    @InjectView(R.id.input_container)
    LinearLayout inputContainer;
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
                        setHandleContainerBottomPadding(getHandleContainerHiddenPadding());
                        handleContainerHidden = true;
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

    private int getHandleContainerHiddenPadding() {
        return inputContainer.getHeight() - handleContainer.getHeight();
    }

    private int getHandleContainerVisiblePadding() {
        return inputContainer.getHeight();
    }

    private void setHandleContainerBottomPadding(int bottom) {
        int top = handleContainer.getPaddingTop();
        int left = handleContainer.getPaddingLeft();
        int right = handleContainer.getPaddingRight();
        handleContainer.setPadding(left, top, right, bottom);
    }

    /**
     * Some real wonky shit here. To slide the handlecontainer into place, we use padding.
     * Once the animation has finished, we anchor to the input field and remove the padding.
     * To have the container slide back, we unanchor, readd the padding, and then animate.
     *
     * @param hide true if we are hiding, false if revealing.
     */
    private void hideHandleContainer(boolean hide) {
        if (handleContainer == null || (hide == handleContainerHidden)) return;
        handleContainerHidden = hide;

        if (hide) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) handleContainer.getLayoutParams();
            lp.addRule(ABOVE, 0);
            lp.addRule(ALIGN_PARENT_BOTTOM);
            handleContainer.setLayoutParams(lp);
            setHandleContainerBottomPadding(getHandleContainerVisiblePadding());
        }

        int target = hide ? getHandleContainerHiddenPadding() : getHandleContainerVisiblePadding();
        ValueAnimator animator = ValueAnimator.ofInt(handleContainer.getPaddingBottom(), target);
        animator.addUpdateListener((a) -> setHandleContainerBottomPadding((Integer) a.getAnimatedValue()));
        animator.setDuration(250);
        animator.setInterpolator(hide ? new AccelerateInterpolator() : new OvershootInterpolator());
        animator.start();
        if (!hide) {
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    setHandleContainerBottomPadding(0);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) handleContainer.getLayoutParams();
                    lp.addRule(ABOVE, R.id.input_container);
                    lp.addRule(ALIGN_PARENT_BOTTOM, 0);
                    handleContainer.setLayoutParams(lp);
                }
            });
        }
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