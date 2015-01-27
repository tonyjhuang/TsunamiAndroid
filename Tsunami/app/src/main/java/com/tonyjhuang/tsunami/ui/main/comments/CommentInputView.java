package com.tonyjhuang.tsunami.ui.main.comments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tonyjhuang.tsunami.R;

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

    public CommentInputView(Context context) {
        this(context, null);
    }

    public CommentInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.inject(this, inflate(context, R.layout.view_comment_input, this));
        input.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                notifyListener();
                return true;
            }
            return false;
        });
        input.setMaxLines(4);
        input.setHorizontallyScrolling(false);
    }

    @OnClick(R.id.confirm)
    public void onConfirmClick(View view) {
        notifyListener();
    }

    private void notifyListener() {
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
}