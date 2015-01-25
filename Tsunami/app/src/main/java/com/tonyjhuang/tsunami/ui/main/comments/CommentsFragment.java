package com.tonyjhuang.tsunami.ui.main.comments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Comment;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.logging.Timber;
import com.tonyjhuang.tsunami.ui.utils.Anim;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

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

    @Inject
    TsunamiApi api;

    private RecyclerView.LayoutManager layoutManager;
    private CommentsAdapter adapter;

    public static CommentsFragment getInstance(long waveId) {
        Bundle args = new Bundle();
        args.putLong(CommentsActivity.WAVE_ID, waveId);
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

        long waveId = getArguments().getLong(CommentsActivity.WAVE_ID, -1);
        if (waveId == -1) {
            getWaveError();
        } else {
            subscribe(api.getWave(waveId), this::setAdapter, (error) -> getWaveError());
        }
    }

    private void setAdapter(Wave wave) {
        if (wave == null) return;
        recyclerView.post(() -> {
            Anim.fadeOut(progressBar);
            adapter = new CommentsAdapter(wave.getComments(), layoutManager);
            recyclerView.setAdapter(adapter);
        });
    }

    private void getWaveError() {
        Anim.fadeOut(progressBar);
        showToast("Couldn't get comments for this wave :(");
    }

    @Override
    public void onSendRequested(String string) {
        if (TextUtils.isEmpty(string)) return;
        //hideKeyboard();
        input.clear();
        Comment comment = Comment.createDebugComment("Kevin", string);
        adapter.addComment(comment);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
}
