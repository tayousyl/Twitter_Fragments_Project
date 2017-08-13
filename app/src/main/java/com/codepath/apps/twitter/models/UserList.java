package com.codepath.apps.twitter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserList {
    @JsonProperty("previous_cursor")
    private Long previousCursor;
    @JsonProperty("next_cursor")
    private Long nextCursor;
    private List<TwitterUser> users;

    public Long getPreviousCursor() {
        return previousCursor;
    }

    public Long getNextCursor() {
        return nextCursor;
    }

    public List<TwitterUser> getUsers() {
        return users;
    }
}
