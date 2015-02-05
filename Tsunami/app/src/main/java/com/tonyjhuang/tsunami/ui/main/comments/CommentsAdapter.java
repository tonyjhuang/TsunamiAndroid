package com.tonyjhuang.tsunami.ui.main.comments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.api.models.Comment;
import com.tonyjhuang.tsunami.api.models.User;
import com.tonyjhuang.tsunami.ui.profile.ProfileActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tony on 1/22/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static PrettyTime prettyTime = new PrettyTime();

    private final static int COMMENT = 1;

    private WeakReference<TsunamiActivity> activityWeakReference;
    private List<Comment> comments;
    private RecyclerView.LayoutManager layoutManager;

    public CommentsAdapter(TsunamiActivity activity,
                           List<Comment> comments,
                           RecyclerView.LayoutManager layoutManager) {
        this.activityWeakReference = new WeakReference<>(activity);
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

    private void bindCommentViewHolder(final Comment comment, CommentViewHolder holder) {
        View.OnClickListener userOnClickListener = getOnClickListener(comment.getUser());
        holder.profilePic.setOnClickListener(userOnClickListener);
        holder.author.setOnClickListener(userOnClickListener);

        holder.author.setText(comment.getUser().getName());
        holder.body.setText(comment.getBody());
        holder.timestamp.setText(prettyTime.format(comment.getCreatedAt()));
    }

    private View.OnClickListener getOnClickListener(User user) {
        return (view) -> {
            if (activityWeakReference.get() == null) return;
            ProfileActivity.startProfileActivity(activityWeakReference.get(), user.getId());
        };
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
        if (layoutManager instanceof LinearLayoutManager)
            layoutManager.scrollToPosition(comments.size() - 1);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.profile_pic)
        ImageView profilePic;
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