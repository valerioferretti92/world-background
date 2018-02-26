package com.gmail.ferretti.valerio.worldbackgrounds;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ferretti on 16/10/17.
 * This class deals with HTTP connection to the Flickr server getting pictures out of it.
 */

public class FlickrFetcher {

    private static final String TAG = "FlickrFetcher";
    private static final String API_KEY = "74116ccd22290ed4bf7901639b938fe5";

    private static final String FETCH_RECENT_METHOD = "flickr.groups.pools.getPhotos";
    private static final String SEARCH_METHOD = "flickr.photos.search";

    private static int PAGE = 1;

    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest").
            buildUpon().
            appendQueryParameter("api_key", API_KEY).
            appendQueryParameter("format", "json").
            appendQueryParameter("nojsoncallback", "1").
            appendQueryParameter("group_id", "40961104@N00").
            appendQueryParameter("extras", "url_z, url_k, views, description, tags, owner_name").
            appendQueryParameter("sort", "interestingness-desc").
            build();

    /*
     * This method takes as a parameter a url as a string, opens and HTTP connection with the method
     * HttpURLConnection.getInputStream() and, at every iteration of a while cycle reads 1024 bytes.
     * At each iteration it writes the content of this array of byte in an output stream. When no
     * other chars are left to be written it returns the output stream as a byte array.
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            //Opening and initializing the HTTP connection
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + " with: " + urlSpec);
            }

            //Getting data from server
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        }finally{
            connection.disconnect();
        }
    }

    /*
     * Convenience method which just format and return getUrlBytes(String) as a String
     */
    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }


    /*
     * This method populates the ArrayList items with GalleryItem objects, each of which created
     * to match a JSON object in the hierarchy contained into the input parameter jsonBody
     */
    public void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException{

        JSONObject jsonPhotos = jsonBody.getJSONObject("photos");
        JSONArray jsonPhoto = jsonPhotos.getJSONArray("photo");

        for(int i = 0; i < jsonPhoto.length(); i++){
            JSONObject jsonElement = jsonPhoto.getJSONObject(i);
            GalleryItem galleryItem = new GalleryItem();
            galleryItem.setCaption(jsonElement.getString("title"));
            galleryItem.setId(jsonElement.getString("id"));
            galleryItem.setAuthorName(jsonElement.getString("ownername"));
            if(!jsonElement.has("url_z") || !jsonElement.has("url_k")){
                //With the statement continue we are excluding pictures without URLs
                continue;
            }
            galleryItem.setUrlThumbanil(jsonElement.getString("url_z"));
            galleryItem.setUrlFullsize(jsonElement.getString("url_k"));
            galleryItem.setViews(jsonElement.getInt("views"));
            galleryItem.setDescription(jsonElement.getString("description"));
            galleryItem.setTags(jsonElement.getString("tags"));

            items.add(galleryItem);
        }
    }


    /*
     * This method directly invokes all the methods above. It creates a url string, gets the http
     * response from "api.flickr.com" through getUrlString. The response is a json String which gets
     * parsed to intialized the JSONObject jsonBody. At the end it calls the method paresItems to
     * populated the ArrayList items and it returns it.
     */
    private List<GalleryItem> downloadGalleryItems(String url){

        /*
         * This ArrayList gets created and populated here and will be passed to WorldBackgroundFragment
         * to be displayed into a RecyclerView object. Each time the application starts it gets
         * recreated and repopulated.
         */
        List<GalleryItem> items = new ArrayList<>();

        try{
            String jsonResponse = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonResponse);
            Log.i(TAG, "Json response: " + jsonResponse);
            parseItems(items, jsonBody);

        }catch(IOException exception){
            Log.e(TAG, exception.getMessage(), exception);

        }catch(JSONException exception){
            Log.e(TAG, exception.getMessage(), exception);
        }

        return items;
    }


    public List<GalleryItem> fetchRecentPhotos(){
        Uri.Builder uriBuilder;
        String url;

        //Appendign method
        uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", FETCH_RECENT_METHOD);
        //Appending page
        uriBuilder.appendQueryParameter("page", String.valueOf(PAGE));
        //CREATING URL
        url = uriBuilder.build().toString();
        return downloadGalleryItems(url);
    }


    public List<GalleryItem> searchPhotos(String query){
        Uri.Builder uriBuilder;
        String url;

        //Appendign method
        uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", SEARCH_METHOD);
        //Appending query text parameter
        uriBuilder.appendQueryParameter("text", query);
        //Appending page
        uriBuilder.appendQueryParameter("page", String.valueOf(PAGE));
        //CREATING URL
        url = uriBuilder.build().toString();
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> fetchTaggedPhotos(String tag){
        Uri.Builder uriBuilder;
        String url;

        //Appendign method
        uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", FETCH_RECENT_METHOD);
        //Appending query text parameter
        uriBuilder.appendQueryParameter("tags", tag);
        //Appending page
        uriBuilder.appendQueryParameter("page", String.valueOf(PAGE));
        //CREATING URL
        url = uriBuilder.build().toString();
        return downloadGalleryItems(url);
    }


    public static int getPAGE() {
        return PAGE;
    }

    public static void setPAGE(int PAGE) {
        FlickrFetcher.PAGE = PAGE;
    }
}