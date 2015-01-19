package com.tonyjhuang.tsunami.ui.main.comments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

/**
 * Created by tony on 1/18/15.
 */
public class CommentsFragment extends TsunamiFragment {

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


}
