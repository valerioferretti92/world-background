package com.gmail.ferretti.valerio.worldbackgrounds;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialog;


/**
 * Created by ferretti on 12/12/17.
 */

public class DownloadsFragment extends Fragment {

    private static final String TAG = "DownloadsFragment";

    private List<String> mDownloadedPictures;
    private RecyclerView mRecyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDownloadedPictures = new ArrayList<>();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view_photo_gallery, container, false);
        mRecyclerView = view.findViewById(R.id.recyler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(new DownloadsAdapter(mDownloadedPictures));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        mDownloadedPictures.clear();
        mDownloadedPictures.addAll(new StorageUtils(getActivity()).getDownloadedPicturesNames());
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


    //Inflating and setting up menu items...
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_downloads, menu);

        MenuItem toggleItem = menu.findItem(R.id.wallpaper_timer);
        if(WallpaperService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.end_wallpaper_service);
            toggleItem.setIcon(R.drawable.ic_switch_timer_off);
        }else{
            toggleItem.setTitle(R.string.start_wallpaper_service);
            toggleItem.setIcon(R.drawable.ic_switch_timer_on);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.wallpaper_timer:

                final boolean shouldStartAlarm = !WallpaperService.isServiceAlarmOn(getActivity());

                if(shouldStartAlarm) {
                    long initialDuration = QueryPreferences.getStoredDuration(getActivity());
                    if (initialDuration == 0) {
                        initialDuration = 1000 * 3600 * 2; //2 hours in ms
                    }

                    TimeDurationPickerDialog timeDurationPickerDialog = new TimeDurationPickerDialog(
                            getActivity(),
                            new TimeDurationPickerDialog.OnDurationSetListener() {
                                @Override
                                public void onDurationSet(TimeDurationPicker view, long duration) {
                                    QueryPreferences.setStoredDuration(getActivity(), duration);
                                    QueryPreferences.setIsAlarmOn(getActivity(), shouldStartAlarm);
                                    WallpaperService.setServiceAlarm(getActivity(), shouldStartAlarm);
                                    getActivity().invalidateOptionsMenu();
                                }
                            },
                            initialDuration);
                    timeDurationPickerDialog.show();
                }else{
                    QueryPreferences.setIsAlarmOn(getActivity(), shouldStartAlarm);
                    WallpaperService.setServiceAlarm(getActivity(), shouldStartAlarm);
                    getActivity().invalidateOptionsMenu();
                }

                break;

            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return true;
    }


    public class DownloadsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;
        private String mImageName;

        public DownloadsHolder(View view){
            super(view);
            mImageView = view.findViewById(R.id.item_image_view);
            mImageView.setOnClickListener(this);
        }

        public void bind(String imageName) {
            mImageName = imageName;
            new StorageUtils(getActivity()).loadImageFromStorage(imageName, mImageView, true);
        }

        @Override
        public void onClick(View view) {

                Intent intent = FullscreenImageActivity.newIntent(getActivity(), mImageName);
                startActivity(intent);
        }
    }


    public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsHolder>{

        private List<String> downloadedPictures;

        public DownloadsAdapter(List<String> pictures){
            downloadedPictures = pictures;
        }

        @Override
        public DownloadsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_photo_gallery, parent, false);
            return new DownloadsHolder(view);
        }

        @Override
        public void onBindViewHolder(DownloadsHolder holder, int position) {

            //Getting the item
            String imageName = downloadedPictures.get(position);
            //Setting up background and text
            holder.bind(imageName);
        }

        @Override
        public int getItemCount() {
            return downloadedPictures.size();
        }
    }


    public static DownloadsFragment newInstance(){
        return new DownloadsFragment();
    }
}