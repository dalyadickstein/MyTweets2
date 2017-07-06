package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dalyadickstein on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;
    private TweetAdapterListener mListener;
    private TwitterClient client;

    // define an interface required by the adapter so that user can click on a tweet (a row)
    // and be brought to the poster's user page
    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
    }

    // pass in the Tweets array into the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
        client = TwitterApp.getRestClient();
    }

    // for each row, inflate the layout and cache references into the ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_tweet layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        // return a new ViewHolder
        return new ViewHolder(tweetView);
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the tweet data at the specified position
        Tweet tweet = mTweets.get(position);
        // populate the view with the tweet data
        holder.tvUserName.setText(tweet.user.name);
        holder.tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeDate.setText(tweet.relativeDate);
        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);
        holder.addListenerOnReplyClick(tweet, holder.btReply);
        holder.addListenerOnFavoriteClick(tweet, holder.btFavorite);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvRelativeDate;
        ImageButton btReply;
        ImageButton btFavorite;
        ImageButton btRetweet;

        // constructor takes in an inflated layout
        public ViewHolder(View itemView) {
            super(itemView);
            // perform findViewById lookups
            this.ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            this.tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            this.tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            this.tvRelativeDate = (TextView) itemView.findViewById(R.id.tvRelativeDate);
            this.btReply = (ImageButton) itemView.findViewById(R.id.btReply);
            this.btFavorite = (ImageButton) itemView.findViewById(R.id.btFavorite);
            this.btRetweet = (ImageButton) itemView.findViewById(R.id.btRetweet);

            // handle row click events
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        // get position of row element
                        int position = getAdapterPosition();
                        // fire listener callback
                        mListener.onItemSelected(v, position);
                    }
                }
            });
        }

        public void addListenerOnReplyClick(final Tweet tweet, ImageButton replyButton) {
            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("screenName", tvScreenName.getText().toString());
                    i.putExtra("inReplyToStatusId", Long.toString(tweet.uid));
                    ((TimelineActivity)context).startActivityForResult(i, 42);
                }
            });
        }

        public void addListenerOnFavoriteClick(final Tweet tweet, ImageButton favoriteButton) {
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedFavoriteBtn(tweet);
                }
            });
        }

        public void clickedFavoriteBtn(final Tweet tweet) {
            String tweetId = Long.toString(tweet.uid);
            Log.d("TweetAdapter", tweetId);
            if (tweet.favorited) {
                client.unfavorite(tweetId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(context, "Unfavorited", Toast.LENGTH_LONG).show();
                        tweet.favorited = false;
                    }
                });
            } else {
                client.favorite(tweetId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(
                    int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(context, "Favorited", Toast.LENGTH_LONG).show();
                        tweet.favorited = true;
                    }
                });
            }
        }

    }

}
