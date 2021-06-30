package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    // constants
    public static final int MAX_TWEET_LENGTH = 280; // updated from 140
    public static final String TAG = "ComposeActivity";

    // UI elements
    EditText etCompose;
    Button btnTweet;

    // reference to the Twitter client
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize client for use in compose button
        client = TwitterApp.getRestClient(this);

        // set activity information and connect to XML
        setContentView(R.layout.activity_compose);

        // find the ids for compose activity
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        // add click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet cannot be empty!", Toast.LENGTH_LONG).show();
                    return;
                } else if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }

                // make an API call to it
                Toast.makeText(ComposeActivity.this, "YOUR TWEET:" + tweetContent, Toast.LENGTH_LONG).show();

                // use twitter client to publish tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Posted tweet!");
                        try {
                            Tweet tweet = Tweet.fromJSON(json.jsonObject);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Failed to post tweet", throwable);
                    }
                });
            }
        });

    }
}