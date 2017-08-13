package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.TweetDetailsActivity;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.listeners.TweetReplyClickListener;
import com.codepath.apps.twitter.listeners.UserProfileClickListener;
import com.codepath.apps.twitter.models.Entities;
import com.codepath.apps.twitter.models.Media;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TweetsAdapter extends ArrayAdapter<Tweet> {
    private static final PrettyTime PRETTY_TIME = new PrettyTime();
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private UserProfileClickListener userProfileClickListener;
    private TweetReplyClickListener tweetReplyClickListener;

    public TweetsAdapter(Context context, List<Tweet> tweets, UserProfileClickListener userProfileClickListener, TweetReplyClickListener tweetReplyClickListener) {
        super(context, android.R.layout.simple_list_item_1, tweets);
        this.userProfileClickListener = userProfileClickListener;
        this.tweetReplyClickListener = tweetReplyClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        final TwitterUser user = tweet.getUser();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        setupMedia(convertView, tweet);
        TextView tvTweetId = (TextView) convertView.findViewById(R.id.tvTweetId);
        tvTweetId.setText(String.valueOf(tweet.getId()));
        convertView.setTag(String.valueOf(position));
        TextView tvTweetText = (TextView) convertView.findViewById(R.id.tvTweetText);
        tvTweetText.setText(Html.fromHtml(tweet.getText()), TextView.BufferType.SPANNABLE);
        ImageView ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);
        ivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProfileClickListener != null) {
                    userProfileClickListener.onUserProfileClick(user);
                }
            }
        });
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivUserPhoto);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
        try {
            tvCreatedAt.setText(PRETTY_TIME.format(FORMATTER.parse(tweet.getCreatedAt())));
        } catch (ParseException e) {
            Log.d("TWEET", "uh oh", e);
        }
        ImageView ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweetReplyClickListener != null) {
                    tweetReplyClickListener.onTweetReplyClick(tweet);
                }
            }
        });
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    TextView tvTweetId = (TextView) v.findViewById(R.id.tvTweetId);
                    Long id = Long.parseLong(tvTweetId.getText().toString());
                    Intent intent = new Intent(getContext(), TweetDetailsActivity.class);
                    intent.putExtra(Extras.TWEET_ID, id);
                    getContext().startActivity(intent);
                }
                return true;
            }
        });
        return convertView;
    }

    private void setupMedia(View view, Tweet tweet) {
        ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(0);
        ivPhoto.setVisibility(View.GONE);
        Entities entities = tweet.getEntities();
        if (entities != null) {
            List<Media> mediaList = entities.getMedia();
            for (Media media : mediaList) {
                if (media.isPhoto()) {
                    Picasso.with(getContext()).load(media.getMediaUrl()).into(ivPhoto);
                    ivPhoto.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
