package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by ferretti on 29/11/17.
 */

public class DetailImageActivity extends AppCompatActivity {

    private static final String TAG ="DetailImageActivity";

    private static final String EXTRA_THUMBNAIL =
            "com.gamil.ferretti.valerio.worldbackgrounds.thumbnail";
    private static final String EXTRA_PICTURE_ID =
            "com.gmail.ferretti.valerio.worldbackgrounds.id";
    private static final String EXTRA_PICTURE_TITLE =
            "com.gmail.ferretti.valerio.worldbackground.title";
    private static final String EXTRA_PICTURE_AUTHOR_NAME =
            "com.gmail.ferretti.valerio.worldbackground.author_name";
    private static final String EXTRA_PICTURE_VIEWS =
            "com.gmail.ferretti.valerio.worldbackground.views";
    private static final String EXTRA_PICTURE_TAGS =
            "com.gmail.ferretti.valerio.worldbackground.tags";
    private static final String EXTRA_URL_FULLSIZE =
            "com.gmail.ferretti.valerio.worldbackgrounds.url_fullsize";


    private byte[] mByteArrayThumbnail;
    private Bitmap mBitmapThumbnail;
    private String mId;
    private FloatingActionButton mFloatingActionButton;
    private String mTitle;
    private String mAuthorName;
    private int mViews;
    private String[] mTags;
    private String mUrlFullsize;
    private Target target;
    private List<String> mDownloadedPictures;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting infos from intent
        mByteArrayThumbnail = getIntent().getByteArrayExtra(EXTRA_THUMBNAIL);
        mBitmapThumbnail = BitmapFactory.decodeByteArray(mByteArrayThumbnail, 0, mByteArrayThumbnail.length);
        mId = getIntent().getStringExtra(EXTRA_PICTURE_ID);
        mTitle = getIntent().getStringExtra(EXTRA_PICTURE_TITLE);
        mAuthorName = getIntent().getStringExtra(EXTRA_PICTURE_AUTHOR_NAME);
        mViews = getIntent().getIntExtra(EXTRA_PICTURE_VIEWS, 0);
        mTags = getIntent().getStringArrayExtra(EXTRA_PICTURE_TAGS);
        mUrlFullsize = getIntent().getStringExtra(EXTRA_URL_FULLSIZE);


        //Inflating the layout
        setContentView(R.layout.detail_image);
        ImageView imageView= (ImageView) findViewById(R.id.fullsize_image);
        TextView titleTextView = (TextView) findViewById(R.id.title);
        TextView authorTextView = (TextView) findViewById(R.id.author_views);
        FlexboxLayout tagsFlexboxLayout = (FlexboxLayout) findViewById(R.id.tags_flexbox_layout);
        tagsFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);


        //Setting up image
        Drawable drawableImage = new BitmapDrawable(getResources(), mBitmapThumbnail);
        imageView.setImageDrawable(drawableImage);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(!mDownloadedPictures.contains(mId)){
                    Toast.makeText(getApplicationContext(), R.string.download_message, Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = FullscreenImageActivity.newIntent(getApplicationContext(), mId);
                    startActivity(intent);
                }

            }
        });


        //Setting up progress bar to make it fit the FAB
        mProgressBar.setVisibility(View.GONE);


        //Setting up title
        if ( !mTitle.equals("") ) {
            mTitle = mTitle.substring(0, 1).toUpperCase() + mTitle.substring(1);
            titleTextView.setText(mTitle);
        }else{
            titleTextView.setText("Title unknown");
        }

        //Setting up author and views
        if(!mAuthorName.equals("") ) {
            authorTextView.setText("Author: " + mAuthorName + "\n" + "Views: " + mViews);
        }else{
            authorTextView.setText("Author: " + "unknown" + "\n" + "Views: " + mViews);
        }

        //Setting up tags
        for(int i = 0; i < mTags.length; i++){
            if( !(mTags[i].length() >= 20) && !mTags[i].equals("") ){

                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.custom_button, null);
                Button button = view.findViewById(R.id.custom_button);
                final String text = mTags[i];

                button.setText(text);
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = TaggedPicturesActivity.newIntent(getApplicationContext(), text);
                        startActivity(intent);
                    }
                });
                tagsFlexboxLayout.addView(view);
            }
        }

        //Setting up download button
        mFloatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                boolean isPictureDownloaded = mDownloadedPictures.contains(mId);

                if(!isPictureDownloaded){

                    //Starting progress bar
                    mProgressBar.setVisibility(View.VISIBLE);
                    int color = ContextCompat.getColor(getApplicationContext(), R.color.colorProgressBarDownload);
                    mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(color));

                    //Setting up storage utils object to make it able to update the UI
                    Log.i(TAG, "Downloading the image " + mId + " from " + mUrlFullsize);
                    StorageUtils storageUtils = new StorageUtils(getApplicationContext());
                    storageUtils.setPictureDownloadedListener(new StorageUtils.PictureDownloadedListener() {
                        @Override
                        public void onPictureDownloaded() {
                            //Add downloaded picture name to mDownloadedPicture list
                            mDownloadedPictures.add(mId);
                            mFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorSetBackground));
                            mFloatingActionButton.setImageResource(R.drawable.detail_btn_apply);
                            mProgressBar.setVisibility(View.GONE);

                            if(new StorageUtils(getApplicationContext()).getDownloadedPicturesNames().size() == 1){
                                MainPagerActivity.updateViewPager();
                            }
                        }

                        @Override
                        public void onPictureDownloadFailed() {
                            //Stop the progress bar on floating action button and notify the user of download failure
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.bitmap_download_failed, Toast.LENGTH_LONG).show();
                        }
                    });

                    //Setting up a target where to download the image on storage
                    target = storageUtils.setupImageTarget(mId, StorageUtils.mImageDirectory);

                    //Saving thumbnail into storage
                    storageUtils.saveBitmapIntoStorage(mId, mBitmapThumbnail, StorageUtils.mThumbnailDirectory);

                    //Downloading image into target
                    storageUtils.downloadImageIntoStorage(mUrlFullsize, target);

                }else{

                    //Starting the progress bar
                    mProgressBar.setVisibility(View.VISIBLE);
                    int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrograssBarSetBackground);
                    mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(color));

                    Log.i(TAG, "Setting wallpaper");
                    StorageUtils storageUtils = new StorageUtils(getApplicationContext());
                    storageUtils.setWallpaperSetListener(new StorageUtils.WallpaperSetListener() {
                        @Override
                        public void onWallpaperSet() {
                            mFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorDone));
                            mFloatingActionButton.setImageResource(R.drawable.ic_done);
                            mProgressBar.setVisibility(View.GONE);
                            mFloatingActionButton.setEnabled(false);
                        }
                    });
                    storageUtils.setWallpaper(mId);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDownloadedPictures = new StorageUtils(getApplicationContext()).getDownloadedPicturesNames();

        if(!mDownloadedPictures.contains(mId)){
            mFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorDownload));
            mFloatingActionButton.setImageResource(R.drawable.ic_download);
        }else{
            mFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorSetBackground));
            mFloatingActionButton.setImageResource(R.drawable.detail_btn_apply);
        }

        mFloatingActionButton.setEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static Intent newIntent(Context context, byte[] byteArray, GalleryItem item){
        Intent intent = new Intent(context, DetailImageActivity.class);
        intent = intent.putExtra(EXTRA_THUMBNAIL, byteArray);
        intent = intent.putExtra(EXTRA_PICTURE_ID, item.getId());
        intent = intent.putExtra(EXTRA_PICTURE_TITLE, item.getCaption());
        intent = intent.putExtra(EXTRA_PICTURE_AUTHOR_NAME, item.getAuthorName());
        intent = intent.putExtra(EXTRA_PICTURE_VIEWS, item.getViews());
        intent = intent.putExtra(EXTRA_PICTURE_TAGS, item.getTags());
        intent = intent.putExtra(EXTRA_URL_FULLSIZE, item.getUrlFullsize());
        return intent;
    }
}