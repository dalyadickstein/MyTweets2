package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String screenName = getIntent().getStringExtra("screen_name");

        // create the user timeline fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        // display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimelineFragment);
        ft.commit();

        // create the user headline
        client = TwitterApp.getRestClient();
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // deserialize the JSON object
                try {
                    User user = User.fromJSON(response);
                    // set the title of the action bar based on the user info
                    getSupportActionBar().setTitle("@" + user.screenName);
                    populateUserHeadline(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        if (screenName != null) { // user clicked on a tweet, not the profile menu option
            client.getClickedUserInfo(screenName, handler);
        } else {
            client.getUserInfo(handler);
        }
    }

    public void populateUserHeadline(User user) {
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName.setText(user.name);
        tvTagLine.setText(user.tagLine);
        tvFollowers.setText(user.followersCount + " Followers");
        tvFollowing.setText(user.followingCount + " Following");
        // load profile image with Glide
        Glide.with(this).load(user.profileImageUrl).into(ivProfileImage);

    }

}
