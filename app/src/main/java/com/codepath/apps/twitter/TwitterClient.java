package com.codepath.apps.twitter;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.twitter.models.FriendLookupResult;
import com.codepath.apps.twitter.models.FriendshipLookupResult;
import com.codepath.apps.twitter.models.SearchResults;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TwitterUser;
import com.codepath.apps.twitter.models.UserList;
import com.codepath.oauth.OAuthBaseClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.IOException;
import java.util.List;

public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CALLBACK_URL = "oauth://simpletweets"; // Change this (here and in manifest)

	public static final String REST_CONSUMER_KEY = "RJp27yvgeV9KiscBvTiGirzgH";
	public static final String REST_CONSUMER_SECRET = "kbM1WQQQkio6SvHRrm5QB7MFAQLpAX8CEGLSm049A5LdVPJ8yG";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(TimelineResponseHandler handler) {
		getNewerHomeTimeline(handler, 1L);
	}

	public void getNewerHomeTimeline(final TimelineResponseHandler handler, Long sinceId) {
		getTimeline(handler, sinceId, "since_id", "statuses/home_timeline.json", null);
	}

	public void getOlderHomeTimeline(final TimelineResponseHandler handler, Long maxId) {
		getTimeline(handler, maxId, "max_id", "statuses/home_timeline.json", null);
	}

	public void getMentionsTimeline(TimelineResponseHandler handler) {
		getNewerMentionsTimeline(handler, 1L);
	}

	public void getNewerMentionsTimeline(final TimelineResponseHandler handler, Long sinceId) {
		getTimeline(handler, sinceId, "since_id", "statuses/mentions_timeline.json", null);
	}

	public void getOlderMentionsTimeline(final TimelineResponseHandler handler, Long maxId) {
		getTimeline(handler, maxId, "max_id", "statuses/mentions_timeline.json", null);
	}

	public void getFavoritesTimeline(Long userId, TimelineResponseHandler handler) {
		getNewerFavoritesTimeline(userId, handler, 1L);
	}

	public void getNewerFavoritesTimeline(Long userId, final TimelineResponseHandler handler, Long sinceId) {
		getTimeline(handler, sinceId, "since_id", "favorites/list.json", userId);
	}

	public void getOlderFavoritesTimeline(Long userId, final TimelineResponseHandler handler, Long maxId) {
		getTimeline(handler, maxId, "max_id", "favorites/list.json", userId);
	}

	public void getTimeline(final TimelineResponseHandler handler, Long id, String paramName, String timelinePath, Long userId) {
		String apiUrl = getApiUrl(timelinePath);
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("user_id", userId);
		params.put(paramName, id);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					List<Tweet> tweets = mapper.readValue(responseBody, new TypeReference<List<Tweet>>() {
					});
					handler.onSuccess(tweets);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void getUserTimeline(Long userId, final TimelineResponseHandler handler) {
		getNewerUserTimeline(handler, userId, 1L);
	}

	public void getNewerUserTimeline(final TimelineResponseHandler handler, Long userId, Long sinceId) {
		getUserTimeline(handler, userId, sinceId, "since_id");
	}

	public void getOlderUserTimeline(final TimelineResponseHandler handler, Long userId, Long maxId) {
		getUserTimeline(handler, userId, maxId, "max_id");
	}

	private void getUserTimeline(final TimelineResponseHandler handler, Long userId, Long id, String paramName) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		params.put("count", 25);
		params.put(paramName, id);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					List<Tweet> tweets = mapper.readValue(responseBody, new TypeReference<List<Tweet>>() {
					});
					handler.onSuccess(tweets);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void searchTweets(String query, final SearchResultsResponseHandler handler) {
		searchNewerTeets(handler, query, 1L);
	}

	public void searchNewerTeets(final SearchResultsResponseHandler handler, String query, Long sinceId) {
		searchTweets(handler, query, sinceId, "since_id");
	}

	public void searchOlderTweets(final SearchResultsResponseHandler handler, String query, Long maxId) {
		searchTweets(handler, query, maxId, "max_id");
	}

	private void searchTweets(final SearchResultsResponseHandler handler, String query, Long id, String paramName) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q", query);
		params.put("count", 25);
		params.put(paramName, id);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					SearchResults searchResults = mapper.readValue(responseBody, SearchResults.class);
					handler.onSuccess(searchResults);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void getAuthenticatedUser(final TwitterUserResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getUser(handler, apiUrl, null);
	}

	public void getUser(Long userId, final TwitterUserResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		getUser(handler, apiUrl, params);
	}

	private void getUser(final TwitterUserResponseHandler handler, String apiUrl, RequestParams params) {
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					TwitterUser user = mapper.readValue(responseBody, TwitterUser.class);
					handler.onSuccess(user);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void updateStatus(String status, final StatusUpdateResponseHandler handler) {
		replyToStatus(status, null, handler);
	}

	public void replyToStatus(String status, Long statusId, final StatusUpdateResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		if (statusId != null) {
			params.put("in_reply_to_status_id", statusId);
		}
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				handler.onSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void getStatus(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/show.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void retweet(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", id));
		getClient().post(apiUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void favorite(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void unfavorite(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void getFollowersList(Long userId, final UserListResponseHandler handler) {
		getUsersList(handler, userId, -1L, "followers/list.json");
	}

	public void getFollowersList(Long userId, Long cursor, final UserListResponseHandler handler) {
		getUsersList(handler, userId, cursor, "followers/list.json");
	}

	public void getFollowingList(Long userId, final UserListResponseHandler handler) {
		getUsersList(handler, userId, -1L, "friends/list.json");
	}

	public void getFollowingList(Long userId, Long cursor, final UserListResponseHandler handler) {
		getUsersList(handler, userId, cursor, "friends/list.json");
	}

	private void getUsersList(final UserListResponseHandler handler, Long userId, Long cursor, String path) {
		String apiUrl = getApiUrl(path);
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		params.put("count", 25);
		params.put("cursor", cursor);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					UserList userList = mapper.readValue(responseBody, UserList.class);
					handler.onSuccess(userList);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void lookupFriends(List<Long> userIds, final FriendLookupResponseHandler handler) {
		String apiUrl = getApiUrl("friendships/lookup.json");
		RequestParams params = new RequestParams();
		params.put("user_id", StringUtils.join(userIds, ","));
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					List<FriendLookupResult> results = mapper.readValue(responseBody, new TypeReference<List<FriendLookupResult>>(){});
					handler.onSuccess(results);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void lookupFriendship(Long sourceUserId, Long targetUserId, final FriendshipLookupResponseHandler handler) {
		String apiUrl = getApiUrl("friendships/show.json");
		RequestParams params = new RequestParams();
		params.put("source_id", sourceUserId);
		params.put("target_id", targetUserId);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					FriendshipLookupResult result = mapper.readValue(responseBody, FriendshipLookupResult.class);
					handler.onSuccess(result);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void follow(Long userId, final TwitterUserResponseHandler handler) {
		Log.d("CLIENT", "follow userId=" + userId);
		String apiUrl = getApiUrl("friendships/create.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		params.put("follow", true);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					TwitterUser user = mapper.readValue(responseBody, TwitterUser.class);
					handler.onSuccess(user);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void unfollow(Long userId, final TwitterUserResponseHandler handler) {
		Log.d("CLIENT", "unfollow userId=" + userId);
		String apiUrl = getApiUrl("friendships/destroy.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					TwitterUser user = mapper.readValue(responseBody, TwitterUser.class);
					Log.d("CLIENT", "UNFOLLOW SUCCESS, userId=" + user.getName());
					handler.onSuccess(user);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public interface TimelineResponseHandler {

		void onSuccess(List<Tweet> tweets);

		void onFailure(Throwable error);

	}

	public interface TwitterUserResponseHandler {

		void onSuccess(TwitterUser user);

		void onFailure(Throwable error);

	}

	public interface StatusUpdateResponseHandler {

		void onSuccess();

		void onFailure(Throwable error);

	}

	public interface TweetResponseHandler {

		void onSuccess(Tweet tweet);

		void onFailure(Throwable error);

	}

	public interface SearchResultsResponseHandler {

		void onSuccess(SearchResults searchResults);

		void onFailure(Throwable error);

	}

	public interface UserListResponseHandler {

		void onSuccess(UserList userList);

		void onFailure(Throwable error);

	}

	public interface FriendLookupResponseHandler {

		void onSuccess(List<FriendLookupResult> results);

		void onFailure(Throwable error);

	}

	public interface FriendshipLookupResponseHandler {

		void onSuccess(FriendshipLookupResult result);

		void onFailure(Throwable error);

	}

}