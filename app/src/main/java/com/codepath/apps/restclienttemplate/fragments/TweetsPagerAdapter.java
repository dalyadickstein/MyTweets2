package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dalyadickstein on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Home", "Mentions"};
    private Context context;
    private HomeTimelineFragment homeTimelineFragment;
    private MentionsTimelineFragment mentionsTimelineFragment;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    // return the total # of fragments
    @Override
    public int getCount() {
        return 2;
    }

    // return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return getHomeTimelineFragment();
            case 1:
                return getMentionsTimelineFragment();
            default:
                return null;
        }
    }

    private HomeTimelineFragment getHomeTimelineFragment() {
        if (homeTimelineFragment == null) {
            homeTimelineFragment = new HomeTimelineFragment();
        }
        return homeTimelineFragment;
    }

    private MentionsTimelineFragment getMentionsTimelineFragment() {
        if (mentionsTimelineFragment == null) {
            mentionsTimelineFragment = new MentionsTimelineFragment();
        }
        return mentionsTimelineFragment;
    }

    // return fragment title to be used for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // generate title based on item position
        return tabTitles[position];
    }

}
