package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    // variables for a Tweet
    public String body;
    public String createdAt;
    public String mediaURL;
    public User user;

    // empty constructor for Parcel
    public Tweet() {}

    // method sets the Tweet's attributes from given JSON Object
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        // get entities (like for media or hashtags)
        JSONObject ents = jsonObject.getJSONObject("entities");
        tweet.mediaURL = null;

        if(ents.has("media")) {
            // get media array and send to Media class
            JSONArray mediaArray = ents.getJSONArray("media");

            // null check for media
            if (mediaArray != null) {
                tweet.mediaURL = Media.fromJSONArray(mediaArray).imageUrl;
            }
        }
        return tweet;
    }

    // method to extract information from JSON array and put into tweets array
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJSON(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

}
