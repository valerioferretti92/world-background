package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialog;

public class TimerExplanationActivity extends AppCompatActivity {

    Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorExplanationActivityBackground));

        setContentView(R.layout.activity_timer_explanation);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.one_time_timer_screen_checkbox);
        Button button = (Button) findViewById(R.id.one_time_timer_screen_button);

        //Setting up checkbox
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    QueryPreferences.setIsOneTimeScreenToBeShown(TimerExplanationActivity.this, false);
                }else{
                    QueryPreferences.setIsOneTimeScreenToBeShown(TimerExplanationActivity.this, true);
                }
            }
        });

        //Setting up button text
        if(QueryPreferences.isAlarmOn(this)){
            button.setText(R.string.disable_timer_button);
        }else{
            button.setText(R.string.enable_timer_button);
        }

        //Setting up button behaviour and time picker
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(!QueryPreferences.isAlarmOn(mContext)){
                    TimeDurationPickerDialog timeDurationPickerDialog = new TimeDurationPickerDialog(
                            mContext,
                            new TimeDurationPickerDialog.OnDurationSetListener() {
                                @Override
                                public void onDurationSet(TimeDurationPicker view, long duration) {
                                    QueryPreferences.setStoredDuration(mContext, duration);
                                    QueryPreferences.setIsAlarmOn(mContext, true);
                                    WallpaperService.setServiceAlarm(mContext, true);
                                    finish();
                                }
                            },
                            1000 * 3600 * 2);
                    timeDurationPickerDialog.show();
                }else{
                    QueryPreferences.setIsAlarmOn(mContext, false);
                    WallpaperService.setServiceAlarm(mContext, false);
                    finish();
                }
            }
        });
    }

    public static Intent newIntent(Context context){
        return new Intent(context, TimerExplanationActivity.class);
    }
}
