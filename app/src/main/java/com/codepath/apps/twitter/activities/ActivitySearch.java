package com.codepath.apps.twitter.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.fragments.SearchFragment;

public class ActivitySearch extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        String query = getIntent().getStringExtra(Extras.QUERY);
        if (savedInstanceState == null) {
            populateSearchResults(query);
        }
    }

    private void populateSearchResults(String query) {
        SearchFragment fragmentSearch = SearchFragment.newInstance(query);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, fragmentSearch);
        ft.commit();
    }

    @Override
    protected void showAuthenticatedUserProfile() {
        Intent intent = new Intent(ActivitySearch.this, ActivityProfile.class);
        intent.putExtra(Extras.USER_ID, authenticatedUser.getId());
        startActivity(intent);
    }

    @Override
    protected void showSearchResults(String query) {
        populateSearchResults(query);
    }

    @Override
    protected String getTag() {
        return "SEARCH";
    }

    @Override
    public void showLatestHomeTimelineTweets() {
        startActivity(new Intent(this, TimelineActivity.class));
    }

}
