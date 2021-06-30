package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Media {
    // attributes for user - public for Parcel
    public String imageUrl;

    // empty constructor for Parcel
    public Media() {}

    // a method to properly extract the media url information
    public static Media fromJSONArray(JSONArray jsonArray) throws JSONException {
        Media nMedia = new Media();
        JSONObject mediaObj = jsonArray.getJSONObject(0);
        nMedia.imageUrl = mediaObj.getString("media_url_https");
        return nMedia;
    }
}
