package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Movie;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetails extends AppCompatActivity {
    // the movie to display
    Tweet tweet;

    // the view objects
    TextView tvScreenName;
    TextView tvBody;
    TextView tvRelativeTime;
    ImageView ivProfileImage;
    LinearLayout ivOuterLayout;
    ImageView ivMedia;

    // constants
    public final int IMG_RADIUS = 100;
    public final int IMG_MARGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init content view
        setContentView(R.layout.activity_tweet_details);

        // init XML elements to vars
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvRelativeTime = (TextView) findViewById(R.id.tvRelativeTime);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivOuterLayout = findViewById(R.id.ivOuterLayout);
        ivMedia = (ImageView) findViewById(R.id.ivMedia);

        // get tweet from intent
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // bind all of the items
        bind(tweet);
    }

    // adds menu bar with profile/logout to details page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void bind(Tweet tweet) {
        // set Tweet body text
        tvBody.setText(tweet.body);

        // manipulate time to show relative
        String preTime = tweet.createdAt;
        String relativeTime = getRelativeTimeAgo(preTime);

        tvRelativeTime.setText(relativeTime);
        tvScreenName.setText(tweet.user.screenName);
        Glide.with(this).load(tweet.user.imageUrl).centerInside()
                .fitCenter()
                .transform(new RoundedCornersTransformation(IMG_RADIUS, IMG_MARGIN)).into(ivProfileImage);

        // init media url variable
        String mediaUrl = tweet.mediaURL;

        // clear image cache - no repeat photos (fixes bug)
        ivMedia.setImageDrawable(null);

        if(mediaUrl != null){
            // only show the image if the tweet has media
            ivOuterLayout.setVisibility(View.VISIBLE);
            Glide.with(this).load(mediaUrl).into(ivMedia);
        }
    }

    // method to adjust time text to show time ago
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}