package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ferretti on 03/12/17.
 */

public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";

    List<String> mTags = new ArrayList<String>(
            Arrays.asList("autumn", "sunset", "sea", "mountains", "lake", "river", "desert",
                    "flowers", "nature", "animals", "snow", "art",  "sky",
                    "city", "old", "landscape", "red", "blue", "yellow", "green"
            )
    );


    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        mRecyclerView.setAdapter(new CategoryFragment.CategoryAdapter(mTags));
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;
        private TextView mTextView;
        private String mText;

        public CategoryHolder(View view){
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.category_item);
            mTextView = (TextView) view.findViewById(R.id.image_view_text);
            mTextView.setOnClickListener(this);
        }

        public void bind(int resource, String text) {
            mText = text;
            text = text.substring(0,1).toUpperCase() + text.substring(1);
            mTextView.setText(text);
            Picasso.with(getActivity()).load(resource).into(mImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = TaggedPicturesActivity.newIntent(getActivity(), mText);
            startActivity(intent);
        }
    }


    public class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder>{

        private List<String> mTags;

        public CategoryAdapter(List<String> tags){
            mTags = tags;
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_category, parent, false);
            return new CategoryHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {

            //Getting the item
            String categoryName = mTags.get(position);
            int backgroundId = getResources().getIdentifier(
                    categoryName,
                    "drawable",
                    getActivity().getPackageName());

            //Setting up background and text
            holder.bind(backgroundId, categoryName);
        }

        @Override
        public int getItemCount() {
            return mTags.size();
        }
    }

    public static CategoryFragment newInstance(){
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }
}