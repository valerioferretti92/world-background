package com.gmail.ferretti.valerio.worldbackgrounds;

/**
 * Created by ferretti on 17/10/17.
 * Model class representing holding references to a picture
 */

public class GalleryItem{

    private String mCaption;
    private String mId;
    private String mUrlThumbnail;
    private String mUrlFullsize;
    private String mAuthorName;
    private int mViews;
    private String mDescription;
    private String[] mTags;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrlThumbnail() {
        return mUrlThumbnail;
    }

    public void setUrlThumbanil(String url) {
        mUrlThumbnail = url;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public int getViews() {
        return mViews;
    }

    public void setViews(int views) {
        mViews = views;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String[] getTags() {
        return mTags;
    }

    public void setTags(String tags) {
        if(tags == null) return;
        mTags = tags.split(" ");
        return;
    }

    public String getUrlFullsize() {
        return mUrlFullsize;
    }

    public void setUrlFullsize(String urlFullsize) {
        mUrlFullsize = urlFullsize;
    }
}
