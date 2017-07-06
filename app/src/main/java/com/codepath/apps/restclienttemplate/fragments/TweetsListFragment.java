package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by dalyadickstein on 7/3/17.
 */

public class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

    public interface TweetSelectedListener {
        // handle tweet selection for detailed view
        public void onTweetSelected(Tweet tweet);
    }

    protected SwipeRefreshLayout swipeContainer;
    protected TweetAdapter tweetAdapter;
    protected RecyclerView rvTweets;
    ArrayList<Tweet> tweets;

    // inflation happens within OnCreateView
    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
    ) {
        // inflate the layout
        View view = inflater.inflate(R.layout.fragments_tweets_list, container, false);

        tweets = new ArrayList<>(); // initialize the ArrayList (data source)
        tweetAdapter = new TweetAdapter(tweets, this); // construct the adapter from this data source

        // SwipeRefreshLayout setup
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);

        // RecyclerView setup (layout manager, use adapter)
        rvTweets = (RecyclerView) view.findViewById(R.id.rvTweet); // find the RecyclerView
        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTweets.setAdapter(tweetAdapter);

        return view;
    }

    // when the user swipes to refresh - method to be overridden in subclasses!
    public void refreshView() {
        Log.i("TweetsListFragment", "Swipe-refreshed");
    }

    public void addItems(JSONArray response) {
        tweetAdapter.clear();
        // for each entry in response, deserialize the JSON object
        for (int i = 0; i < response.length(); i++) {
            // convert each object to a Tweet model
            // add that Tweet model to our data source (the ArrayList 'tweets')
            // notify the adapter that we've added an item
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
                swipeContainer.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // manually insert a tweet into the list of tweets
    public void addTweet(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

    // when the user clicks on a tweet in the tweets list
    @Override
    public void onItemSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
    }

}
