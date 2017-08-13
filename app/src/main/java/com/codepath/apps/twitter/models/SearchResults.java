package com.codepath.apps.twitter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResults {
    @JsonProperty("statuses")
    private List<Tweet> tweets;
    @JsonProperty("search_metadata")
    private SearchMetaData metaData;

    public List<Tweet> getTweets() {
        return tweets;
    }

    public SearchMetaData getMetaData() {
        return metaData;
    }
}
