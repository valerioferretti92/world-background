package com.gmail.ferretti.valerio.worldbackgrounds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ferretti on 12/12/17.
 */

public class MainPagerActivity extends AppCompatActivity {

    private final Integer CATEGORY_SCREEN = 0;
    private final Integer PHOTOS_SCREEN = 1;
    private final Integer DOWNLOADS_SCREEN = 2;

    private ViewPager mViewPager;
    private List<Integer> mMainScreens;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_pager);
        mViewPager = (ViewPager) findViewById(R.id.screen_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        //Setting up action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);
        actionBar.setIcon(R.drawable.action_bar_icon);

        //Setting up the main screen list
        mMainScreens = new ArrayList<>();
        mMainScreens.add(CATEGORY_SCREEN);
        mMainScreens.add(PHOTOS_SCREEN);
        mMainScreens.add(DOWNLOADS_SCREEN);

        //Setting up the view pager
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                int screen = mMainScreens.get(position);

                switch(screen){
                    case 0:
                        return CategoryFragment.newInstance();
                    case 1:
                        return WorldBackgroundsFragment.newInstance();
                    case 2:
                        return DownloadsFragment.newInstance();
                    default:
                        Toast.makeText(
                                getApplicationContext(),
                                R.string.pager_activity_error,
                                Toast.LENGTH_LONG).show();
                        return null;
                }
            }


            @Override
            public int getCount() {
                return mMainScreens.size();
            }


            @Override
            public CharSequence getPageTitle(int position) {
                switch(position){
                    case 0:
                        return getString(R.string.categories_tab);
                    case 1:
                        return getString(R.string.search_tab);
                    case 2:
                        return getString(R.string.downloads_tab);
                    default:
                        return null;
                }
            }
        });

        mViewPager.setCurrentItem(1);

        tabLayout.setupWithViewPager(mViewPager);
    }
}
