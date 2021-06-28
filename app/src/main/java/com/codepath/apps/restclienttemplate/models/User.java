package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    // attritbutes for user
    public String name;
    public String screenName;
    public String imageUrl;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.imageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
