package com.tonyjhuang.tsunami.ui.main.contentview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjhuang.tsunami.R;
import com.tonyjhuang.tsunami.TsunamiFragment;
import com.tonyjhuang.tsunami.api.models.Wave;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by tonyjhuang on 9/6/14.
 */
public class ContentFragment extends TsunamiFragment implements ContentScrollView.WaveService {
    @InjectView(R.id.content_scrollview)
    ContentScrollView content;

    private List<Wave> waves = new ArrayList<Wave>();
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Wave.WaveBuilder builder = Wave.WaveBuilder.getInstance();
        Wave wave;
        wave = builder.message("hah wow look at this wave!")
                .title("My super sweet TIIIITLE")
                .build();
        waves.add(wave);

        wave = builder.message(getString(R.string.lorem_ipsum_ext))
                .title("cla cla cla im figgity what")
                .build();
        waves.add(wave);

        wave = builder.message("another crazy awesome wave that you should look at")
                .title("A slightly less clever title")
                .build();
        waves.add(wave);

        wave = builder.message(getString(R.string.lorem_ipsum))
                .title("hooah! lorem bitch!")
                .build();
        waves.add(wave);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        content.setWaveService(this);
    }

    public void toggleSplash() {
        content.toggleSplash();
    }

    @Override
    public void getNextWave(Callback<Wave> callback) {
        callback.success(waves.get(i++ % waves.size()), null);
    }

    @Override
    public void splashWave() {

    }

    @Override
    public void rippleWave(Wave wave) {

    }
}
