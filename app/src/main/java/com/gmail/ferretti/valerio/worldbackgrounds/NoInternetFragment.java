package com.gmail.ferretti.valerio.worldbackgrounds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by ferretti on 16/03/18.
 */

public class NoInternetFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_no_internet, container, false);

        //Detecting and reacting to swipe down
        RelativeLayout relativeLayout = view.findViewById(R.id.no_internet_relative_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainPagerActivity.updateViewPager();
            }
        });

        return view;
    }

    public static NoInternetFragment newInstance(){
        NoInternetFragment noInternetFragment = new NoInternetFragment();
        return noInternetFragment;
    }
}
