package com.codepath.apps.twitter;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     RestClient client = RestApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	public static final String PREFS_NAME = "SIMPLE_TWEETS";
	public static final String AUTHENTICATED_USER_ID_KEY = "authenticatedUserId";
	private static Context context;

	public static Long getAuthenticatedUserId() {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong(AUTHENTICATED_USER_ID_KEY, -1);
	}

	public static void setAuthenticatedUserId(Long userId) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(AUTHENTICATED_USER_ID_KEY, userId);
		editor.commit();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}
}