package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.facebook.stetho.common.ArrayListAccumulator;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements EditFragmentDialog.EditNameDialogListener {
    // request code
    private final int REQUEST_CODE = 20;

    // for swipe refresh
    private SwipeRefreshLayout swipeContainer;

    // instance vars
    TwitterClient restClient;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    public static final String TAG = "TimelineActivity";
    public static final int MAX_TWEET_LENGTH = 280; // updated from 140

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // init list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // find recycler view
        rvTweets = findViewById(R.id.rvTweets);

        // recycler view adapter setup
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh the list here
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // call rest client method
        restClient = TwitterApp.getRestClient(this);

        // fill up home timeline screen
        populateHomeTimeline();
    }

    private void fetchTimelineAsync(int i) {
        // send the network request to fetch the updated data
        restClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // ...the data has come back, add new items to your adapter...
                restClient.getHomeTimeline(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONArray jsonArray = json.jsonArray;
                        try {
                            adapter.addAll(Tweet.fromJsonArray(jsonArray));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Failed to refresh", throwable);
                    }
                });

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "Timeline refresh failed", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Failed", throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch case to naviagte menu items
        switch (item.getItemId()) {
            case R.id.profile:
                return true;
            case R.id.logout:
                // to logout of twitter clone
                clickToLogout(rvTweets);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if(REQUEST_CODE == requestCode && resultCode == RESULT_OK){
            // get data from the intent
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            // update RV with this new tweet
            tweets.add(0, tweet);

            // update view after
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        restClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(TimelineActivity.this, "Adding tweets to timeline failed", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "JSON Object retrieval failed", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fail!", throwable);
            }
        });
    }

    public void clickToLogout(View view) {
        // forget who's logged in
        restClient.clearAccessToken();
        // navigate backwards to Login screen
        finish();
    }

    public void clickToCompose(View view) {
        // navigate to new activity
        showEditDialog();
    }

    public void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditFragmentDialog editNameDialogFragment = EditFragmentDialog.newInstance();
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditDialog(String tweetContent) {
        if(tweetContent.isEmpty()){
            Toast.makeText(TimelineActivity.this, "Sorry, tweet cannot be empty!", Toast.LENGTH_LONG).show();
            return;
        } else if(tweetContent.length() > MAX_TWEET_LENGTH){
            Toast.makeText(TimelineActivity.this, "Sorry, tweet is too long!", Toast.LENGTH_LONG).show();
            return;
        }

        // use twitter client to publish tweet
        restClient.publishTweet(tweetContent, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Posted tweet!");
                try {
                    Tweet tweet = Tweet.fromJSON(json.jsonObject);
                    // update RV with this new tweet
                    tweets.add(0, tweet);

                    // update view after
                    adapter.notifyItemInserted(0);
                    rvTweets.smoothScrollToPosition(0);

                    finish();
                } catch (JSONException e) {
                    Toast.makeText(TimelineActivity.this, "Opening new activity failed!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "Publishing tweet failed!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to post tweet", throwable);
            }
        });
    }

}