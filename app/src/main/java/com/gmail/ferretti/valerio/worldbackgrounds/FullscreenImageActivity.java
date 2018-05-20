package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by ferretti on 06/12/17.
 */

public class FullscreenImageActivity extends AppCompatActivity {

    private static final String TAG = "FullscreenImageActivity";

    private static final String EXTRA_IMAGE_NAME =
            "com.gmail.ferretti.valerio.worldbackgrounds.image_name";


    private String mImageName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieving picture
        mImageName = getIntent().getStringExtra(EXTRA_IMAGE_NAME);

        //Setting up the title bar color and overlay
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));

        //Setting up window parameters
        setSystemUiParameters(true, actionBar);

        //Setting up the layout
        setContentView(R.layout.fullscreen_image);

        //Setting up title bar to appear or disapper based on clicks on the picture
        ImageView imageView = (ImageView) findViewById(R.id.fullscreen_image_view);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(actionBar.isShowing()){

                    setSystemUiParameters(false, actionBar);

                }else if(!actionBar.isShowing()){

                    setSystemUiParameters(true, actionBar);

                }
            }
        });

        //Retrieving picture from storage
        new StorageUtils(getApplicationContext()).loadImageFromStorage(mImageName, imageView, false);
    }


    //Inflating and setting up menu items...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_fullscreen_image, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                break;

            case R.id.delete_picture:

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to delete this picture from storage?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new StorageUtils(getApplicationContext()).deleteImageFromStorage(mImageName);
                                onBackPressed();
                            }
                        });
                alertDialog.show();
                break;

            case R.id.set_wallpaper:

                Toast.makeText(getApplicationContext(), R.string.setting_wallpaper, Toast.LENGTH_LONG).show();
                StorageUtils storageUtils = new StorageUtils(getApplicationContext());
                storageUtils.setWallpaperSetListener(new StorageUtils.WallpaperSetListener() {
                    @Override
                    public void onWallpaperSet() {
                        Toast.makeText(getApplicationContext(), R.string.wallpaper_set, Toast.LENGTH_LONG).show();
                    }
                });
                storageUtils.setWallpaper(mImageName);
                break;
        }

        return true;
    }


    private void setSystemUiParameters(boolean isShowNavigationBars, ActionBar actionBar){

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        uiOptions += View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if(isShowNavigationBars){
            uiOptions += View.SYSTEM_UI_FLAG_VISIBLE;
            actionBar.show();
        }else{
            uiOptions += View.SYSTEM_UI_FLAG_FULLSCREEN;
            actionBar.hide();
        }

        decorView.setSystemUiVisibility(uiOptions);
    }


    public static Intent newIntent(Context context, String imageName){
        Intent intent = new Intent(context, FullscreenImageActivity.class);
        intent = intent.putExtra(EXTRA_IMAGE_NAME, imageName);
        return intent;
    }

}