package com.gmail.ferretti.valerio.worldbackgrounds;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ferretti on 13/09/17.
 * Abstract class which subclasses will inherit and implement to instantiate activities
 * with only one fragment from a FrameLayout
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
