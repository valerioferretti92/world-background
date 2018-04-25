package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class TimerExplanationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer_explanation);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.one_time_timer_screen_checkbox);
        Button button = (Button) findViewById(R.id.one_time_timer_screen_button);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    QueryPreferences.setIsOneTimeScreenToBeShown(TimerExplanationActivity.this, false);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WallpaperTimerUtils.setupWallpaperTimer(TimerExplanationActivity.this);
            }
        });
    }

    public static Intent newIntent(Context context){
        return new Intent(context, TimerExplanationActivity.class);
    }
}
