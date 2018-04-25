package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ferretti on 16/10/17.
 */

public class WorldBackgroundsFragment extends Fragment {

    private static final String TAG_SV = "SearchView";

    /*
     * It gets initialized with picture coming from Flickr after the execution of the background
     * thread which manages the network by the function AsyncTask<>.onPostExecute()
     */
    private List<GalleryItem> mItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private EndlessRecyclerViewScrollListener mScrollListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        QueryPreferences.setStoredString(getActivity(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(new PhotoAdapter(mItems));
        mScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                updateItems(false);
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);

        updateItems(true);

        return view;
    }


    //Inflating and setting up menu items...
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        //Setting up the search bar showed in the toolbar
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView =  (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG_SV, "New query submitted: " + query);
                QueryPreferences.setStoredString(getActivity(), query);
                updateItems(true);
                return true;
            }
        });

        searchView.setOnSearchClickListener(new SearchView.OnClickListener(){
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //Action for starting or ending the polling
            case R.id.menu_clear_search:
                QueryPreferences.setStoredString(getActivity(), null);
                updateItems(true);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }



    /******************* ViewHolder-Adapter for the RecyclerView ***********************/

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View view){
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.item_image_view);
        }

        public void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem = galleryItem;
            Picasso.with(getActivity()).
                    load(mGalleryItem.getUrlThumbnail()).
                    placeholder(R.drawable.loading_icon).
                    into(mImageView, new com.squareup.picasso.Callback(){
                        @Override
                        public void onSuccess() {
                            mImageView.setClickable(true);
                            mImageView.setOnClickListener(PhotoHolder.this);
                        }

                        @Override
                        public void onError() {
                            //Empty implementation
                        }
                    });
        }

        @Override
        public void onClick(View v) {
            Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            Intent intent = DetailImageActivity.newIntent(getActivity(), baos.toByteArray(), mGalleryItem);
            startActivity(intent);
        }

        public void disableClickability(){
            mImageView.setClickable(false);
        }
    }


    public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mItems;

        public PhotoAdapter(List<GalleryItem> items){
            mItems = items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_photo_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.disableClickability();
            GalleryItem item = mItems.get(position);
            holder.bindGalleryItem(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }


    /***********************************************************************************/


    /*
     * Function which update items in the list. If it is a new search it empities the list and request
     * the first page of results, otherwise it just increments the result page and lets FlickrFetcher
     * to append the results in the list.
     */
    public void updateItems(boolean isClearList){

        String query = QueryPreferences.getStoredQuery(getActivity());

        if(isClearList){
            mItems.clear();
            FlickrFetcher.setPAGE(1);
        }else {
            FlickrFetcher.setPAGE( FlickrFetcher.getPAGE() + 1 );
        }

        if(query == null){
            new FetchItemsTask(mItems, mRecyclerView, FetchItemsTask.FETCH_RECENT_METHOD, null).execute();
        }else {
            new FetchItemsTask(mItems, mRecyclerView, FetchItemsTask.SEARCH_METHOD, query).execute();
        }
    }

    public static WorldBackgroundsFragment newInstance(){
        WorldBackgroundsFragment worldBackgroundsFragment = new WorldBackgroundsFragment();
        return worldBackgroundsFragment;
    }
}