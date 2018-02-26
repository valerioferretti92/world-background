package com.gmail.ferretti.valerio.worldbackgrounds;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

    /*
     * This class provides the execution of a background task by overriding the method
     * doInBackground(). The third parameter is the type that doInBackground() returns and the value
     * returned is the input parameter of onPostExecute().
     */
public class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

    private static final String TAG = "FlickrFetcher";
    public static final String FETCH_RECENT_METHOD = "fetch_recent_method";
    public static final String SEARCH_METHOD = "search_method";
    public static final String FETCH_TAGGED_METHOD = "fetch_tagged_method";



    private String mQueryParameter;
    private List<GalleryItem> mItems;
    private RecyclerView mRecyclerView;
    private final String mMethod;


    public FetchItemsTask(List<GalleryItem> items, RecyclerView recyclerView, String method, String queryParameter){
        mItems = items;
        mRecyclerView = recyclerView;
        mMethod = method;
        if(mMethod == FETCH_RECENT_METHOD){
            mQueryParameter = null;
        }else{
            mQueryParameter = queryParameter;
        }
    }


    /*
     * This function create an instance of FlickrFetcher and returns the an ArrayList of
     * GalleryItems fetched from Flickr. It executes in a background thread!
     */
    @Override
    protected List<GalleryItem> doInBackground(Void... params) {

        List<GalleryItem> items = null;

        switch(mMethod){
            case FETCH_RECENT_METHOD:
                items = new FlickrFetcher().fetchRecentPhotos();
                break;
            case SEARCH_METHOD:
                items = new FlickrFetcher().searchPhotos(mQueryParameter);
                break;
            case FETCH_TAGGED_METHOD:
                items = new FlickrFetcher().fetchTaggedPhotos(mQueryParameter);
                break;
            default:
                Log.e(TAG, "It was not possible to determine which API method to call!");
                break;
        }
        return items;
    }

    /*
     * It gets executed after the end of doInBackground in the main thread. It initializes the
     * member variable mItems with the values passed by doInBackground(). After that it sets
     * RecyclerView's adapter through the method setupAdapter().
     */
    @Override
    protected void onPostExecute(List<GalleryItem> galleryItems) {
        mItems.addAll(galleryItems);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
}