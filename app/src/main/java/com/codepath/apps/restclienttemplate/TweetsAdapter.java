package com.codepath.apps.restclienttemplate;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // define global variables
    Context context;
    List<Tweet> tweets;

    // adapter constructor
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind value based on pos
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data
        Tweet tweet = tweets.get(position);

        // bind tweet with VH
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // constants
        public final int IMG_RADIUS = 80;
        public final int IMG_MARGIN = 0;

        // variables
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTime;
        LinearLayout ivOuterLayout;
        ImageView ivMedia;

        // attach all of the variables to respective XML elements
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // connect variables to XML elems
            setViewElements(itemView);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show Tweet details
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the Tweet at the position, this won't work if the class is static
                Tweet tweet = tweets.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, TweetDetails.class);
                // serialize the tweet using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the activity
                context.startActivity(intent);
            }
        }

        public void bind(Tweet tweet) {
            // set Tweet body text
            tvBody.setText(tweet.body);

            // manipulate time to show relative
            String preTime = tweet.createdAt;
            String relativeTime = getRelativeTimeAgo(preTime);

            tvRelativeTime.setText(relativeTime);
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.imageUrl).centerInside()
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(IMG_RADIUS, IMG_MARGIN)).into(ivProfileImage);

            // init media url variable
            String mediaUrl = tweet.mediaURL;

            // clear image cache - no repeat photos (fixes bug)
            ivMedia.setImageDrawable(null);

            if(mediaUrl != null){
                // only show the image if the tweet has media
                ivOuterLayout.setVisibility(View.VISIBLE);
                Glide.with(context).load(mediaUrl).into(ivMedia);
            }
        }
        public void setViewElements(@NonNull View itemView) {
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivOuterLayout = itemView.findViewById(R.id.ivOuterLayout);
            ivMedia = itemView.findViewById(R.id.ivMedia);
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
}
