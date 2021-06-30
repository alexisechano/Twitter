package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    // attributes for user - public for Parcel
    public String name;
    public String screenName;
    public String imageUrl;

    // empty constructor for Parcel
    public User() {}

    // method sets the User's attributes from given JSON Object
    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.imageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
