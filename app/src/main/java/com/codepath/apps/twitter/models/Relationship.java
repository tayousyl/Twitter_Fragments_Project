package com.codepath.apps.twitter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationship {
    private FriendshipSource source;

    public FriendshipSource getSource() {
        return source;
    }
}
