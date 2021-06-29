package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Media {
    // attritbutes for user
    public String imageUrl;

    // empty constructor for Parcel
    public Media(){}

    public static Media fromJSON(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.imageUrl = jsonObject.getString("media_url_https");
        if(media.imageUrl == null){
            return null;
        }
        return media;
    }

    public static Media fromJSONArray(JSONArray jsonArray) throws JSONException {
        Media nMedia = new Media();
        JSONObject mediaObj = jsonArray.getJSONObject(0);
        nMedia.imageUrl = mediaObj.getString("media_url_https");
        return nMedia;
    }
}
