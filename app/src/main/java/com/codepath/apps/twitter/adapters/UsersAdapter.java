package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.listeners.UserProfileClickListener;
import com.codepath.apps.twitter.models.FriendshipLookupResult;
import com.codepath.apps.twitter.models.FriendshipSource;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;


import java.util.List;

public class UsersAdapter extends ArrayAdapter<TwitterUser> {
    private static final String TAG = "USER_ADAPTER";
    private UserProfileClickListener listener;
    private TwitterClient client;
    private Long authenticatedUserId;

    public UsersAdapter(Context context, List<TwitterUser> users, UserProfileClickListener listener, Long authenticatedUserId) {
        super(context, android.R.layout.simple_list_item_1, users);
        this.listener = listener;
        this.authenticatedUserId = authenticatedUserId;
        this.client = TwitterApplication.getRestClient();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TwitterUser user = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.rlUserHeader = (RelativeLayout) convertView.findViewById(R.id.rlUserHeader);
            viewHolder.ivBackgroundImage = (ImageView) convertView.findViewById(R.id.ivUserBackgroundImage);
            viewHolder.btnFollow = (Button) convertView.findViewById(R.id.btnFollow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TextView tvUserDescription = (TextView) convertView.findViewById(R.id.tvUserDescription);
        tvUserDescription.setText(user.getDescription());
        if (user.getId().equals(authenticatedUserId)) {
            viewHolder.btnFollow.setVisibility(View.INVISIBLE);
        } else {
            client.lookupFriendship(authenticatedUserId, user.getId(), new TwitterClient.FriendshipLookupResponseHandler() {
                @Override
                public void onSuccess(FriendshipLookupResult result) {
                    FriendshipSource source = result.getRelationship().getSource();
                    setupFollowButton(user.getId(), source.isFollowing(), viewHolder);
                }

                @Override
                public void onFailure(Throwable error) {
                    Log.e("USER_ADAPTER", "Failed to lookup friend", error);
                }
            });
        }
        ImageView ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);
        ivUserPhoto.setImageResource(0);
        ivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUserProfileClick(user);
                }
            }
        });
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivUserPhoto);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        final String backgroundImageUrl = user.getProfileBackgroundImageUrl();
        viewHolder.ivBackgroundImage.setImageResource(0);
        if (backgroundImageUrl != null && backgroundImageUrl != "") {
            Picasso.with(getContext()).load(backgroundImageUrl).into(viewHolder.ivBackgroundImage);
        } else {
            setHeaderBackgroundColor(viewHolder.rlUserHeader, user.getProfileBackgroundColor());
        }
        return convertView;
    }

    private void setupFollowButton(final Long userId, boolean isFollowing, final ViewHolder viewHolder) {
        if (isFollowing) {
            viewHolder.btnFollow.setTextColor(Color.parseColor("#FFFFFF"));
            viewHolder.btnFollow.setBackgroundResource(R.drawable.following_button);
            viewHolder.btnFollow.setText(R.string.unfollow);
            viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitterApplication.getRestClient().unfollow(userId, new TwitterClient.TwitterUserResponseHandler() {
                        @Override
                        public void onSuccess(TwitterUser user) {
                            setupFollowButton(userId, false, viewHolder);
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e(TAG, "Failed to unfollow friend", error);
                        }
                    });
                }
            });
        } else {
            viewHolder.btnFollow.setTextColor(Color.parseColor("#89000000"));
            viewHolder.btnFollow.setBackgroundResource(R.drawable.not_following_button);
            viewHolder.btnFollow.setText(R.string.follow);
            viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitterApplication.getRestClient().follow(userId, new TwitterClient.TwitterUserResponseHandler() {
                        @Override
                        public void onSuccess(TwitterUser user) {
                            setupFollowButton(userId, true, viewHolder);
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e(TAG, "Failed to follow friend", error);
                        }
                    });
                }
            });
        }
    }

    private void setHeaderBackgroundColor(RelativeLayout rlUserHeader, String backgroundColor) {
        if (rlUserHeader != null && backgroundColor != null) {
            rlUserHeader.setBackgroundColor(Color.parseColor("#" + backgroundColor));
        }
    }

    public static class ViewHolder {
        ImageView ivBackgroundImage;
        RelativeLayout rlUserHeader;
        Button btnFollow;
    }

}
