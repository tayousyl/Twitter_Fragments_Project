package com.codepath.apps.twitter.fragments;


import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.models.SearchResults;
import com.codepath.apps.twitter.models.Tweet;

import java.util.List;

public class SearchFragment extends TweetListFragment {
    private TwitterClient client;
    private String query;
    private Long maxId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        query = getArguments().getString(Extras.QUERY);
        populateWithLatestTweets();
    }

    public static SearchFragment newInstance(String query) {
        SearchFragment fragmentSearch = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(Extras.QUERY, query);
        fragmentSearch.setArguments(args);
        return fragmentSearch;
    }

    @Override
    public void populateWithLatestTweets() {
        client.searchTweets(query, new TwitterClient.SearchResultsResponseHandler() {
            @Override
            public void onSuccess(SearchResults searchResults) {
                List<Tweet> tweets = searchResults.getTweets();
                maxId = searchResults.getMetaData().getMaxId();
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
        client.searchOlderTweets(new TwitterClient.SearchResultsResponseHandler() {
            @Override
            public void onSuccess(SearchResults searchResults) {
                List<Tweet> tweets = searchResults.getTweets();
                maxId = searchResults.getMetaData().getMaxId();
                addAll(tweets.isEmpty() ? tweets : tweets.subList(1, tweets.size()));
            }

            @Override
            public void onFailure(Throwable error) {
                logError(error);
            }
        }, query, maxId);
    }

    private void logError(Throwable error) {
        Log.d("SEARCH", "Failed to retrieve tweets", error);
    }

}
