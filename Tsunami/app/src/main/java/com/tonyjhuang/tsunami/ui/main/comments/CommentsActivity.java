package com.tonyjhuang.tsunami.ui.main.comments;

import android.content.Intent;
import android.os.Bundle;

import com.tonyjhuang.tsunami.injection.MainModule;
import com.tonyjhuang.tsunami.utils.SingleFragmentActivity;
import com.tonyjhuang.tsunami.utils.TsunamiActivity;
import com.tonyjhuang.tsunami.utils.TsunamiConstants;
import com.tonyjhuang.tsunami.utils.TsunamiFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tony on 1/18/15.
 */
public class CommentsActivity extends SingleFragmentActivity {
    public static final String WAVE_ID = "wave_id";

    public static void startCommentsActivity(TsunamiActivity activity, long waveId) {
        Intent intent = new Intent(activity, CommentsActivity.class);
        intent.putExtra(WAVE_ID, waveId);
        activity.startActivityForResult(intent, TsunamiConstants.COMMENTS_REQUEST_CODE);
    }

    @Override
    protected List<Object> getMyModules() {
        return Arrays.asList(new MainModule(this));
    }

    @Override
    public TsunamiFragment getFragment() {
        long waveId = getIntent().getLongExtra(WAVE_ID, -1);
        return CommentsFragment.getInstance(waveId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateToolbar(true);
        setContentBelowToolbar(true);
    }
}
