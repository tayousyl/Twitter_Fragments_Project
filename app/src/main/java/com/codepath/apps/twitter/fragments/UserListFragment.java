package com.codepath.apps.twitter.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.adapters.UsersAdapter;
import com.codepath.apps.twitter.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.listeners.UserProfileClickListener;
import com.codepath.apps.twitter.models.TwitterUser;

import java.util.ArrayList;
import java.util.List;

public abstract class UserListFragment extends Fragment  {
    private UsersAdapter aUsers;
    private List<TwitterUser> users;
    private GridView gvUsers;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_list, parent, false);
        setupSwitchRefreshLayout(v);
        gvUsers = (GridView) v.findViewById(R.id.gvUsers);
        gvUsers.setAdapter(aUsers);
        gvUsers.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateWithMoreUsers();
                return true;
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<>();
        aUsers = new UsersAdapter(getActivity(), users, (UserProfileClickListener) getActivity(), TwitterApplication.getAuthenticatedUserId());
    }

    private void setupSwitchRefreshLayout(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateWithUsers();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public abstract void populateWithUsers();

    public abstract void populateWithMoreUsers();

    public void showLatest() {
        gvUsers.smoothScrollToPosition(0);
    }

    public void clear() {
        aUsers.clear();
    }

    public void addAll(List<TwitterUser> users) {
        aUsers.addAll(users);
        swipeContainer.setRefreshing(false);
    }

}
