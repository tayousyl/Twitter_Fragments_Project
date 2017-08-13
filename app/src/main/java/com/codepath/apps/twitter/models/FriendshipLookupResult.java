package com.codepath.apps.twitter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendshipLookupResult {
    private Relationship relationship;

    public Relationship getRelationship() {
        return relationship;
    }
}
