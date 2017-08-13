package com.codepath.apps.twitter.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.models.Tweet;

import java.util.List;

public class UserTimelineFragment extends TweetListFragment {
    private TwitterClient client;
    private Long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        userId = getArguments().getLong(Extras.USER_ID);
        populateWithLatestTweets();
    }

    public static UserTimelineFragment newInstance(Long userId) {
        UserTimelineFragment fragmentUserTimeline = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putLong(Extras.USER_ID, userId);
        fragmentUserTimeline.setArguments(args);
        return fragmentUserTimeline;
    }

    @Override
    public void populateWithLatestTweets() {
        populateWithLatestTweets(userId);
    }

    public void populateWithLatestTweets(Long userId) {
        this.userId = userId;
        client.getUserTimeline(userId, new TwitterClient.TimelineResponseHandler() {
            @Override
            public void onSuccess(List<Tweet> tweets) {
                clear();
                addAll(tweets);
                showLatest();
            }

            @Override
            public void onFailure(Throwable error) {
                logError(error);
            }
        });
    }

    @Override
    public void populateWithOlderTweets(Long oldestTweetId) {
        client.getOlderUserTimeline(new TwitterClient.TimelineResponseHandler() {
            @Override
            public void onSuccess(List<Tweet> tweets) {
                addAll(tweets.isEmpty() ? tweets : tweets.subList(1, tweets.size()));
            }

            @Override
            public void onFailure(Throwable error) {
                logError(error);
            }
        }, userId, oldestTweetId);
    }

    private void logError(Throwable error) {
        Log.d("USER_TIMELINE", "Failed to retrieve tweets", error);
    }
}
