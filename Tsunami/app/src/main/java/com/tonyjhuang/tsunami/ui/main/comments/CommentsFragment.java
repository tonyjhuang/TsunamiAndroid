package com.tonyjhuang.tsunami.ui.main.comments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Comment;
import com.tonyjhuang.tsunami.api.network.TsunamiApi;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/18/15.
 */
public class CommentsFragment extends TsunamiFragment implements CommentInputView.SendRequestListener {

    @InjectView(R.id.list)
    ListView list;
    @InjectView(R.id.input)
    CommentInputView input;

    @Inject
    TsunamiApi api;

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long waveId = getArguments().getLong(CommentsActivity.WAVE_ID, -1);
        if (waveId == -1) {
            getWaveError();
        } else {
            subscribe(api.getWave(waveId),
                    (wave) -> {
                        adapter = new CommentsAdapter(wave.getComments());
                        list.setAdapter(adapter);
                    }, (error) -> getWaveError());
        }
    }

    private void getWaveError() {
        showToast("Couldn't get comments for this wave :(");
    }

    @Override
    public void onSendRequested(String string) {
        if (TextUtils.isEmpty(string)) return;
        input.clear();
        boolean shouldScroll = adapter.getCount() == list.getLastVisiblePosition();
        adapter.addComment(Comment.createDebugComment("Kevin", string));
        if (shouldScroll)
            list.post(() -> list.smoothScrollToPosition(adapter.getCount() - 1));
    }

    private static class CommentsAdapter extends BaseAdapter {

        private List<Comment> comments;

        private CommentsAdapter(List<Comment> comments) {
            this.comments = comments;
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Comment getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_comment, parent, false);
                holder = new CommentViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CommentViewHolder) convertView.getTag();
            }
            Comment comment = getItem(position);
            holder.author.setText(comment.getAuthor().getName());
            holder.body.setText(comment.getBody());

            return convertView;
        }

        public void addComment(Comment comment) {
            comments.add(comment);
            notifyDataSetChanged();
        }
    }

    static class CommentViewHolder {
        @InjectView(R.id.author)
        TextView author;
        @InjectView(R.id.timestamp)
        TextView timestamp;
        @InjectView(R.id.body)
        TextView body;

        public CommentViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
