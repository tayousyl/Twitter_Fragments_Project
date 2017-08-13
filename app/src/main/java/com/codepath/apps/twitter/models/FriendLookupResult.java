package com.codepath.apps.twitter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendLookupResult {
    private String name;
    @JsonProperty("screen_name")
    private String screenName;
    private Long id;
    private List<String> connections;

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public Long getId() {
        return id;
    }

    public boolean isFollowing() {
        return connections != null && connections.contains("following");
    }

    public boolean isFollowedBy() {
        return connections != null && connections.contains("followed_by");
    }
}
