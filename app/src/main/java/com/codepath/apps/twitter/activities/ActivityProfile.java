package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tayousyl.PagerSlidingTabStrip;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.fragments.FavoritesTimelineFragment;
import com.codepath.apps.twitter.fragments.FollowersListFragment;
import com.codepath.apps.twitter.fragments.FollowingListFragment;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;


public class ActivityProfile extends BaseActivity {
    private static final String TAG = "PROFILE";
    private static final NumberFormat NUMBER_FORMATTTER = NumberFormat.getIntegerInstance();
    private TwitterClient client;
    private TwitterUser twitterUser;
    private ViewPager vpPager;
    private ProfilePagerAdapter aPager;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        supportActionBar.setIcon(R.drawable.twitter_logo_white_48);
        client = TwitterApplication.getRestClient();
        userId = getIntent().getLongExtra(Extras.USER_ID, -1);

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        aPager = new ProfilePagerAdapter(getSupportFragmentManager(), getLayoutInflater());
        vpPager.setAdapter(aPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

        client.getUser(userId, new TwitterClient.TwitterUserResponseHandler() {
            @Override
            public void onSuccess(TwitterUser user) {
                twitterUser = user;
                populateUserDetails(twitterUser);
                aPager.setupStats(user);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to retrieve user profile", error);
            }
        });
    }

    @Override
    public void showLatestHomeTimelineTweets() {
        startActivity(new Intent(this, TimelineActivity.class));
    }

    private void populateUserHeader(TwitterUser user) {
        ActivityProfile.this.twitterUser = user;
        getSupportActionBar().setTitle("@" + twitterUser.getScreenName());
        TextView tvUserDescription = (TextView) findViewById(R.id.tvUserDescription);
        tvUserDescription.setText(user.getDescription());
        ImageView ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
        ivUserPhoto.setImageResource(0);
        Picasso.with(getApplicationContext()).load(twitterUser.getProfileImageUrl()).into(ivUserPhoto);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(twitterUser.getName());
        TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + twitterUser.getScreenName());
        final RelativeLayout rlUserHeader = (RelativeLayout) findViewById(R.id.rlUserHeader);
        final String backgroundImageUrl = twitterUser.getProfileBackgroundImageUrl();
        ImageView ivUserBackgroundImage = (ImageView) findViewById(R.id.ivUserBackgroundImage);
        ivUserBackgroundImage.setImageResource(0);
        if (backgroundImageUrl != null && backgroundImageUrl != "") {
            Picasso.with(getApplicationContext()).load(backgroundImageUrl).into(ivUserBackgroundImage);
        } else {
            setHeaderBackgroundColor(rlUserHeader);
        }
    }

    private void setHeaderBackgroundColor(RelativeLayout rlUserHeader) {
        rlUserHeader.setBackgroundColor(Color.parseColor("#" + twitterUser.getProfileBackgroundColor()));
    }

    private void populateUserTimeline(Long userId) {
        vpPager.setCurrentItem(aPager.USER_TIMELINE_POSITION);
        aPager.userTimelineFragment.populateWithLatestTweets(userId);
    }

    @Override
    protected void showAuthenticatedUserProfile() {
        populateUserDetails(authenticatedUser);
    }

    private void populateUserDetails(TwitterUser user) {
        this.twitterUser = user;
        populateUserHeader(user);
        Long userId = user.getId();
        populateUserTimeline(userId);
        if (aPager.followingListFragment != null) {
            aPager.followingListFragment.populateWithUsers(userId);
        }
        if (aPager.followersListFragment != null) {
            aPager.followersListFragment.populateWithUsers(userId);
        }
        if (aPager.favoritesTimelineFragment != null) {
            aPager.favoritesTimelineFragment.populateWithLatestTweets(userId);
        }
        aPager.setupStats(twitterUser);
    }

    @Override
    protected void showSearchResults(String query) {
        Intent intent = new Intent(this, ActivitySearch.class);
        intent.putExtra(Extras.QUERY, query);
        startActivity(intent);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onUserProfileClick(TwitterUser user) {
        populateUserDetails(user);
    }

    public class ProfilePagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.ViewTabProvider {
        private final String[] tabTitles = {"Tweets", "Following", "Followers", "Favorites"};
        private final int USER_TIMELINE_POSITION = 0;
        private final int FOLLOWING_LIST_POSITION = 1;
        private final int FOLLOWERS_LIST_POSITION = 2;
        private final int FAVORITES_LIST_POSITION = 3;
        private UserTimelineFragment userTimelineFragment;
        private FollowingListFragment followingListFragment;
        private FollowersListFragment followersListFragment;
        private FavoritesTimelineFragment favoritesTimelineFragment;
        private View[] TABS;

        public ProfilePagerAdapter(FragmentManager fragmentManager, LayoutInflater inflater) {
            super(fragmentManager);
            TABS = new View[4];
            TABS[0] = inflater.inflate(R.layout.item_user_stats, null);
            TABS[1] = inflater.inflate(R.layout.item_user_stats, null);
            TABS[2] = inflater.inflate(R.layout.item_user_stats, null);
            TABS[3] = inflater.inflate(R.layout.item_user_stats, null);
        }

        private void setupStats(TwitterUser twitterUser) {
            setupView(TABS[0], tabTitles[0], twitterUser.getTweetCount());
            setupView(TABS[1], tabTitles[1], twitterUser.getFriendsCount());
            setupView(TABS[2], tabTitles[2], twitterUser.getFollowersCount());
            setupView(TABS[3], tabTitles[3], twitterUser.getUserFavoritedCount());
        }

        private void setupView(View view, String title, int count) {
            TextView tvStatLabel = (TextView) view.findViewById(R.id.tvStatLabel);
            tvStatLabel.setText(title.toUpperCase());
            TextView tvStatCount = (TextView) view.findViewById(R.id.tvStatCount);
            tvStatCount.setText(NUMBER_FORMATTTER.format(count));
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case USER_TIMELINE_POSITION:
                    return UserTimelineFragment.newInstance(userId);
                case FOLLOWING_LIST_POSITION:
                    return FollowingListFragment.newInstance(userId);
                case FOLLOWERS_LIST_POSITION:
                    return FollowersListFragment.newInstance(userId);
                case FAVORITES_LIST_POSITION:
                    return FavoritesTimelineFragment.newInstance(userId);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case USER_TIMELINE_POSITION:
                    userTimelineFragment = (UserTimelineFragment) fragment;
                    break;
                case FOLLOWING_LIST_POSITION:
                    followingListFragment = (FollowingListFragment) fragment;
                    break;
                case FOLLOWERS_LIST_POSITION:
                    followersListFragment = (FollowersListFragment) fragment;
                    break;
                case FAVORITES_LIST_POSITION:
                    favoritesTimelineFragment = (FavoritesTimelineFragment) fragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public View getPageView(int position) {
            return TABS[position];
        }
    }

}
