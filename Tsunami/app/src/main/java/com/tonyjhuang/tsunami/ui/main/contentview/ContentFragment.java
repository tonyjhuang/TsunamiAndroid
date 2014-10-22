package com.tonyjhuang.tsunami.ui.main.contentview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;
import com.tonyjhuang.tsunami.api.models.Wave;
import com.tonyjhuang.tsunami.api.network.TsunamiApiClient;

import javax.inject.Inject;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class ContentFragment extends TsunamiFragment {
    @InjectView(R.id.content_scrollview)
    ContentScrollView content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
