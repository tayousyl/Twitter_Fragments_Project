package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.tayousyl.PagerSlidingTabStrip;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.fragments.HomeTimelineFragment;
import com.codepath.apps.twitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitter.fragments.TweetListFragment;

public class TimelineActivity extends BaseActivity {
    private ViewPager vpPager;
    private TweetsPagerAdapter aPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        aPager = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(aPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
    }

    @Override
    protected String getTag() {
        return "TIMELINE";
    }

    public void showLatestHomeTimelineTweets() {
        vpPager.setCurrentItem(aPager.HOME_TIMELINE_POSITION);
        aPager.homeTimelineFragment.populateWithLatestTweets();
    }

    @Override
    protected void showAuthenticatedUserProfile() {
        Intent intent = new Intent(TimelineActivity.this, ActivityProfile.class);
        intent.putExtra(Extras.USER_ID, authenticatedUser.getId());
        startActivity(intent);
    }

    @Override
    protected void showSearchResults(String query) {
        Intent intent = new Intent(this, ActivitySearch.class);
        intent.putExtra(Extras.QUERY, query);
        startActivity(intent);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles = {"Home", "Mentions"};
        private final int HOME_TIMELINE_POSITION = 0;
        private final int MENTIONS_TIMELINE_POSITION = 1;
        private HomeTimelineFragment homeTimelineFragment;
        private MentionsTimelineFragment mentionsTimelineFragment;

        public TweetsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == HOME_TIMELINE_POSITION) {
                return new HomeTimelineFragment();
            } else if (position == MENTIONS_TIMELINE_POSITION) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TweetListFragment tweetListFragment = (TweetListFragment) super.instantiateItem(container, position);
            switch (position) {
                case HOME_TIMELINE_POSITION:
                    homeTimelineFragment = (HomeTimelineFragment) tweetListFragment;
                    break;
                case MENTIONS_TIMELINE_POSITION:
                    mentionsTimelineFragment = (MentionsTimelineFragment) tweetListFragment;
                    break;
            }
            return tweetListFragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }

}
