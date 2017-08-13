package com.codepath.apps.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;

public class ComposeTweetFragment extends DialogFragment {
    private static final int MAX_CHARS = 140;
    private static final String TAG = "COMPOSE_TWEET";
    private TwitterClient client;
    private TextView tvCharsLeft;
    private EditText etTweetText;
    private StatusUpdateListener listener;
    private Long inReplyToStatusId = null;
    private String inReplyToScreenName = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_compose, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setupUserDetails(view);
        setupCharacterLimit(view);
        setupTweetButton(view);
        return view;
    }

    private void setupUserDetails(final View view) {
        client = TwitterApplication.getRestClient();
        client.getAuthenticatedUser(new TwitterClient.TwitterUserResponseHandler() {
            @Override
            public void onSuccess(TwitterUser user) {
                TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
                TextView tvUserScreenName = (TextView) view.findViewById(R.id.tvUserScreenName);
                ImageView ivUserPhoto = (ImageView) view.findViewById(R.id.ivUserPhoto);
                tvUserName.setText(user.getName());
                tvUserScreenName.setText("@" + user.getScreenName());
                Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivUserPhoto);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.d(TAG, "Failed to retrieve user's details", error);
            }
        });
    }

    private void setupCharacterLimit(View view) {
        etTweetText = (EditText) view.findViewById(R.id.etTweetText);
        String text = "";
        if (inReplyToScreenName != null) {
            text = "@" + inReplyToScreenName + " ";
            etTweetText.setText(text);
            etTweetText.setSelection(text.length());
        }
        etTweetText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_CHARS)});
        etTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsLeft = MAX_CHARS - s.length();
                tvCharsLeft.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        });
        int startingCount = MAX_CHARS - text.length();
        tvCharsLeft = (TextView) view.findViewById(R.id.tvCharsLeft);
        tvCharsLeft.setText(String.valueOf(startingCount));
    }

    public void setupTweetButton(View view) {
        Button btnTweet = (Button) view.findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = etTweetText.getText().toString();
                Log.d(TAG, "status=" + status);
                client.replyToStatus(status, inReplyToStatusId, new TwitterClient.StatusUpdateResponseHandler() {
                    @Override
                    public void onSuccess() {
                        if (listener != null) {
                            listener.onStatusUpdated();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.d(TAG, "Failed to update status", error);
                    }
                });
            }
        });
    }



    public void setListener(StatusUpdateListener listener) {
        this.listener = listener;
    }

    public void setInReplyToStatusId(Long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public interface StatusUpdateListener {

        void onStatusUpdated();

    }
}
