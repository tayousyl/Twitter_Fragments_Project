package com.codepath.apps.twitter.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.BaseActivity;
import com.codepath.apps.twitter.adapters.TweetsAdapter;
import com.codepath.apps.twitter.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.listeners.TweetReplyClickListener;
import com.codepath.apps.twitter.listeners.UserProfileClickListener;
import com.codepath.apps.twitter.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetListFragment extends Fragment implements TweetReplyClickListener {
    private TweetsAdapter aTweets;
    private List<Tweet> tweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    private ComposeTweetFragment composeTweetFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        setupSwitchRefreshLayout(v);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateWithOlderTweets(getOldestTweetId());
                return true;
            }
        });
        return v;
    }

    private Long getOldestTweetId() {
        if (tweets.size() == 0) {
            return 1L;
        } else {
            Tweet tweet = tweets.get(tweets.size() - 1);
            return tweet.getId();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getActivity(), tweets, (UserProfileClickListener) getActivity(), this);
    }

    @Override
    public void onTweetReplyClick(Tweet tweet) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        composeTweetFragment = new ComposeTweetFragment();
        composeTweetFragment.setInReplyToStatusId(tweet.getId());
        composeTweetFragment.setInReplyToScreenName(tweet.getUser().getScreenName());
        composeTweetFragment.setListener(new ComposeTweetFragment.StatusUpdateListener() {
            @Override
            public void onStatusUpdated() {
                composeTweetFragment.dismiss();
                ((BaseActivity) getActivity()).showLatestHomeTimelineTweets();
            }
        });
        composeTweetFragment.show(fragmentManager, "COMPOSE_TWEET");
    }

    private void setupSwitchRefreshLayout(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateWithLatestTweets();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public abstract void populateWithLatestTweets();

    public abstract void populateWithOlderTweets(Long oldestTweetId);

    public void showLatest() {
        lvTweets.smoothScrollToPosition(0);
    }

    public void clear() {
        aTweets.clear();
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
        swipeContainer.setRefreshing(false);
    }

}
