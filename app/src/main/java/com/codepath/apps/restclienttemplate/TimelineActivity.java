package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class TimelineActivity extends AppCompatActivity implements
TweetsListFragment.TweetSelectedListener {

    private final int REQUEST_CODE = 42;
    private ViewPager viewPager;
    private TweetsPagerAdapter tweetsPagerAdapter;
    private HomeTimelineFragment homeTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tweetsPagerAdapter);
        // set up the tabLayout to use the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu - this adds items to the action bar if it's present
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCompose:
                launchComposeView();
                return true;
            case R.id.miProfile:
                launchProfileView(null);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTweetSelected(Tweet tweet) {
        // launchDetailView(tweet), or the logic behind it
        //Intent i = new Intent(TimelineActivity.this, DetailActivity.class);
    }

    // launch sub-activity ProfileActivity
    public void launchProfileView(String screenName) {
        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
        i.putExtra("screen_name", screenName);
        startActivity(i);
    }

    // launch sub-activity ComposeActivity
    public void launchComposeView() {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        i.putExtra("screenName", "");
        startActivityForResult(i, REQUEST_CODE);
    }

    // handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            Tweet newTweet = data.getExtras().getParcelable("tweet");
            homeTimelineFragment = (HomeTimelineFragment) tweetsPagerAdapter.getItem(0);
            viewPager.setCurrentItem(0);
            homeTimelineFragment.addTweet(newTweet);
        }
    }

}
