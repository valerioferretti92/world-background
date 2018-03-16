package com.gmail.ferretti.valerio.worldbackgrounds;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ferretti on 16/12/17.
 */


public class StorageUtils {

    private static final String TAG = "StorageUtils";

    public static final String mImageDirectory = "pictures";
    public static final String mThumbnailDirectory = "thumbnails";

    private Context mContext;
    private PictureDownloadedListener mPictureDownloadedListener;
    private WallpaperSetListener mWallpaperSetListener;


    //Interface for updating the floating action button once the download is completed
    public interface PictureDownloadedListener {
        void onPictureDownloaded();
        void onPictureDownloadFailed();
    }


    public void setPictureDownloadedListener(PictureDownloadedListener listener){
        mPictureDownloadedListener = listener;
    }


    //Interface for updating the floating action button once the wallpaper has been set
    public interface WallpaperSetListener{
        void onWallpaperSet();
    }


    public void setWallpaperSetListener(WallpaperSetListener wallpaperSetListener){
        mWallpaperSetListener = wallpaperSetListener;
    }


    //Class constructors
    public StorageUtils(Context context){
        mContext = context;
    }


    //Method to set up a target where to download the fullsize wallpaper and upfate the UI
    public Target setupImageTarget(final String imageName, final String directory) {

        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmapWallpaper, Picasso.LoadedFrom from) {

                //Saving bitmap to storage
                saveBitmapIntoStorage(imageName, bitmapWallpaper, directory);

                //Updating floating action button
                updateFabAfterPictureDownloaded();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "Picasso.onBitmapFailed(Drawable) called");

                //Deleting the corresponding thumbnail
                File directoryThumbnail = mContext.getDir(mThumbnailDirectory, Context.MODE_PRIVATE);
                File thumbnail = new File(directoryThumbnail, imageName);
                if (thumbnail.delete()) Log.i(TAG,"Thumbnail on the disk deleted successfully!");

                //Updating ui
                mPictureDownloadedListener.onPictureDownloadFailed();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                    Log.d(TAG, "Picasso.onPrepareLoad(Drawable) called");
                }
            }
        };
    }


    //Method to update FAB in DetailImageActivity after picture has been downloaded to storage
    private void updateFabAfterPictureDownloaded(){

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable(){
            @Override
            public void run() {
                mPictureDownloadedListener.onPictureDownloaded();
            }
        });
    }


    //Set picture as background and update FAB if necessary
    public void setWallpaper(String imageName){

        final ImageView imageView = new ImageView(mContext);
        File directory = mContext.getDir(mImageDirectory, Context.MODE_PRIVATE);
        File myImageFile = new File(directory, imageName);
        Picasso.with(mContext).load(myImageFile).into(imageView, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
                        Bitmap bitmapWallpaper = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        try {
                            wallpaperManager.setBitmap(bitmapWallpaper);
                            updateFabAfterWallpaperSet();
                        }catch(IOException exception){
                            Log.e(TAG, "Error when setting bitmap as wallpaper");
                        }
                    }
                }).start();
            }

            @Override
            public void onError() {
                Log.e(TAG, "Failed when loading image from storage");
            }
        });
    }


    //Method to update FAB in DetailImageActivity after the wallpaper has been set
    public void updateFabAfterWallpaperSet(){

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable(){
            @Override
            public void run() {
                mWallpaperSetListener.onWallpaperSet();
            }
        });
    }


    //Method to download a fullsize image into target
    public void downloadImageIntoStorage(String url, Target target) {
        Picasso.with(mContext).load(url).into(target);
    }


    //Method to save a bitmap into storage
    public void saveBitmapIntoStorage(final String imageName, final Bitmap bitmap, final String pictureDirectory){

        new Thread(new Runnable() {
            @Override
            public void run() {

                File directory = mContext.getDir(pictureDirectory, Context.MODE_PRIVATE);
                File picture = new File(directory, imageName);
                FileOutputStream fileOutputStream = null;

                try{
                    fileOutputStream = new FileOutputStream(picture);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                }catch(IOException exception) {
                    exception.printStackTrace();
                }finally{
                    try{
                        fileOutputStream.close();
                    }catch (IOException exception){
                        exception.printStackTrace();
                    }
                }

                Log.d(TAG, "Image " + imageName + " saved to >>> " + picture.getAbsolutePath());
            }
        }).start();
    }


    //Method to retrieve a picture from storage
    public void loadImageFromStorage(String imageName, ImageView imageView, boolean isThumbnail){

        File directory;
        if (isThumbnail){
            directory = mContext.getDir(mThumbnailDirectory, Context.MODE_PRIVATE);
        }else{
            directory = mContext.getDir(mImageDirectory, Context.MODE_PRIVATE);
        }
        File myImageFile = new File(directory, imageName);
        Picasso.with(mContext).load(myImageFile).into(imageView);
    }


    //Method to delete an image from storage
    public void deleteImageFromStorage(String imageName){

        File directoryFullsize = mContext.getDir(mImageDirectory, Context.MODE_PRIVATE);
        File directoryThumbnail = mContext.getDir(mThumbnailDirectory, Context.MODE_PRIVATE);
        File wallpaper = new File(directoryFullsize, imageName);
        File thumbnail = new File(directoryThumbnail, imageName);
        if (wallpaper.delete()) Log.i(TAG,"Wallpaper on the disk deleted successfully!");
        if (thumbnail.delete()) Log.i(TAG,"Thumbnail on the disk deleted successfully!");

        if(getDownloadedPicturesNames().isEmpty()) {
            MainPagerActivity.updateViewPager();
        }
    }


    //Method to obtain a list of downloaded picture names
    public ArrayList<String> getDownloadedPicturesNames(){

        ArrayList<String> downloadedPictureNames = new ArrayList<>();
        File directory = mContext.getDir(mImageDirectory, Context.MODE_PRIVATE);
        for(File picture: directory.listFiles()){
            downloadedPictureNames.add(picture.getName());
        }

        return downloadedPictureNames;
    }

}