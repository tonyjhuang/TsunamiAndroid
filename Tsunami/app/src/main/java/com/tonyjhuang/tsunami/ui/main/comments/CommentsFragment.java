package com.tonyjhuang.tsunami.ui.main.comments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Comment;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.utils.Anim;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by tony on 1/18/15.
 */
public class CommentsFragment extends TsunamiFragment implements CommentInputView.SendRequestListener {
    @InjectView(R.id.progress)
    ProgressBar progressBar;
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.input)
    CommentInputView input;
    @InjectView(R.id.message)
    TextView message;

    @Inject
    TsunamiApi api;
    @Inject
    Bus bus;


    private RecyclerView.LayoutManager layoutManager;
    private CommentsAdapter adapter;
    private Wave wave;

    public static CommentsFragment getInstance(long waveId) {
        Bundle args = new Bundle();
        args.putLong(TsunamiConstants.WAVE_ID_EXTRA, waveId);
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        input.setOnSendRequestListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long waveId = getArguments().getLong(TsunamiConstants.WAVE_ID_EXTRA, TsunamiConstants.WAVE_ID_EXTRA_DEFAULT);
        if (waveId == TsunamiConstants.WAVE_ID_EXTRA_DEFAULT) {
            onGetWaveError(null);
        } else {
            subscribe(api.getWave(waveId), this::setAdapter, this::onGetWaveError);
        }
    }

    private void setAdapter(Wave wave) {
        if (wave == null) return;
        this.wave = wave;
        recyclerView.post(() -> {
            Anim.fadeOut(progressBar);
            adapter = new CommentsAdapter(new ArrayList<>(wave.getComments()), layoutManager);
            recyclerView.setAdapter(adapter);
            if (wave.getComments().size() == 0) showNoCommentsMessage(true);
        });
    }

    private void onGetWaveError(Throwable error) {
        Anim.fadeOut(progressBar);
        showToast("Couldn't get comments for this wave :(");
        if (error != null)
            Timber.e(error, "couldn't get wave for comments");
    }

    @Override
    public void onSendRequested(String string) {
        if (TextUtils.isEmpty(string) || wave == null) return;
        input.clear();
        showNoCommentsMessage(false);

        Comment comment = Comment.createDebugComment("Kevin", string);
        adapter.addComment(comment);
        wave.addComment(comment);

        postCommentAddedEvent(wave);
        api.comment(wave.getId(), string).publish().connect();
    }

    private void showNoCommentsMessage(boolean show) {
        message.setText(show ? getString(R.string.comment_no_comments) : "");
    }

    private void postCommentAddedEvent(Wave wave) {
        bus.post(new CommentAddedEvent(wave));
    }

    public static class CommentAddedEvent {
        public Wave wave;

        public CommentAddedEvent(Wave wave) {
            this.wave = wave;
        }
    }
}
