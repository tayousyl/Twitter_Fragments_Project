package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.models.TwitterUser;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class ActivityLogin extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		getClient().getAuthenticatedUser(new TwitterClient.TwitterUserResponseHandler() {
			@Override
			public void onSuccess(TwitterUser user) {
				TwitterApplication.setAuthenticatedUserId(user.getId());
				showTimeline();
			}

			@Override
			public void onFailure(Throwable error) {
				showTimeline();
			}
		});
	}

	private void showTimeline() {
		Intent intent = new Intent(ActivityLogin.this, TimelineActivity.class);
		startActivity(intent);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
