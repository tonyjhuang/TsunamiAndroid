package com.tonyjhuang.tsunami.ui.main.comments;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Comment;
import com.tonyjhuang.tsunami.logging.Timber;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/22/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static PrettyTime prettyTime = new PrettyTime();

    private final static int COMMENT = 1;

    private List<Comment> comments;
    private RecyclerView.LayoutManager layoutManager;

    public CommentsAdapter(List<Comment> comments, RecyclerView.LayoutManager layoutManager) {
        this.comments = comments;
        this.layoutManager = layoutManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case COMMENT:
                View view = inflater.inflate(R.layout.row_comment, parent, false);
                return new CommentViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case COMMENT:
                Comment comment = comments.get(position);
                bindCommentViewHolder(comment, (CommentViewHolder) holder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return COMMENT;
    }

    private void bindCommentViewHolder(Comment comment, CommentViewHolder holder) {
        holder.author.setText(comment.getAuthor().getName());
        holder.body.setText(comment.getBody());
        holder.timestamp.setText(prettyTime.format(comment.getCreatedAt()));
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
        if(layoutManager instanceof LinearLayoutManager)
            layoutManager.scrollToPosition(comments.size() - 1);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.author)
        TextView author;
        @InjectView(R.id.timestamp)
        TextView timestamp;
        @InjectView(R.id.body)
        TextView body;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}