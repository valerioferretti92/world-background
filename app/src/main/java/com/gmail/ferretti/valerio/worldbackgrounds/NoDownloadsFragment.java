package com.gmail.ferretti.valerio.worldbackgrounds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

/**
 * Created by ferretti on 16/03/18.
 */

public class NoDownloadsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_no_downloads, container, false);
        return view;
    }

    public static NoDownloadsFragment newInstance(){
        NoDownloadsFragment noDownloadsFragment = new NoDownloadsFragment();
        return noDownloadsFragment;
    }
}
