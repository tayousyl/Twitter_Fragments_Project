package com.codepath.apps.twitter.fragments;


import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.models.TwitterUser;
import com.codepath.apps.twitter.models.UserList;

import java.util.List;

public class FollowersListFragment extends UserListFragment {
    private TwitterClient client;
    private Long userId;
    private Long nextCursor;

    public static FollowersListFragment newInstance(Long userId) {
        FollowersListFragment fragment = new FollowersListFragment();
        Bundle args = new Bundle();
        args.putLong(Extras.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        userId = getArguments().getLong(Extras.USER_ID);
        populateWithUsers();
    }

    @Override
    public void populateWithUsers() {
        populateWithUsers(userId);
    }

    public void populateWithUsers(Long userId) {
        this.userId = userId;
        client.getFollowersList(userId, new TwitterClient.UserListResponseHandler() {
            @Override
            public void onSuccess(UserList userList) {
                nextCursor = userList.getNextCursor();
                clear();
                addAll(userList.getUsers());
                showLatest();
            }

            @Override
            public void onFailure(Throwable error) {
                logError(error);
            }
        });
    }

    @Override
    public void populateWithMoreUsers() {
        client.getFollowersList(userId, nextCursor, new TwitterClient.UserListResponseHandler() {
            @Override
            public void onSuccess(UserList userList) {
                nextCursor = userList.getNextCursor();
                List<TwitterUser> users = userList.getUsers();
                addAll(users.isEmpty() ? users : users.subList(1, users.size()));
            }

            @Override
            public void onFailure(Throwable error) {
                logError(error);
            }
        });
    }

    private void logError(Throwable error) {
        Log.d("FOLLOWERS_LIST", "Failed to retrieve following list", error);
    }
}
