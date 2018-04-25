package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ferretti on 12/12/17.
 */

public class TaggedPicturesActivity extends AppCompatActivity {

    private static final String TAG = "TaggedPicturesActivty";

    private static final String EXTRA_TAG =
            "com.gmail.ferretti.valerio.worldbackgrounds.tag";

    private List<GalleryItem> mItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private String mTag;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTag = getIntent().getStringExtra(EXTRA_TAG);

        setContentView(R.layout.recycler_view_photo_gallery);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(new PhotoAdapter(mItems));
        mScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                updateItems(false);
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);

        updateItems(true);
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
            Picasso.with(getApplicationContext()).
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

            Intent intent = DetailImageActivity.newIntent(getApplicationContext(), baos.toByteArray(), mGalleryItem);
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
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
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

        if(isClearList){
            mItems.clear();
            FlickrFetcher.setPAGE(1);
        }else {
            FlickrFetcher.setPAGE( FlickrFetcher.getPAGE() + 1 );
        }

        new FetchItemsTask(mItems, mRecyclerView, FetchItemsTask.FETCH_TAGGED_METHOD, mTag).execute();
    }


    public static Intent newIntent(Context context, String tag){
        Intent intent = new Intent(context, TaggedPicturesActivity.class);
        intent = intent.putExtra(EXTRA_TAG, tag);
        return intent;
    }
}
