package com.pugh.sockso.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pugh.sockso.android.R;

public class PlayerActivity extends Activity {

    private static final String TAG = PlayerActivity.class.getSimpleName();

    private ImageButton mPlayPauseButton;
    private ImageButton mForwardButton;
    private ImageButton mBackwardButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private ImageButton mPlaylistButton;
    private ImageButton mRepeatButton;
    private ImageButton mShuffleButton;

    private SeekBar mTrackProgressBar;
    
    private TextView mTrackCurrentDurationLabel;
    private TextView mTrackTotalDurationLabel;
    
    private TextView mArtistNameLabel;
    private TextView mAlbumNameLabel;
    private TextView mTrackNameLabel;

    private ImageView mAlbumCover;

    private boolean mIsPlaying = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() ran");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.player);

        // All player buttons
        mPlayPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        mForwardButton = (ImageButton) findViewById(R.id.forwardButton);
        mBackwardButton = (ImageButton) findViewById(R.id.backwardButton);
        mNextButton = (ImageButton) findViewById(R.id.nextButton);
        mPreviousButton = (ImageButton) findViewById(R.id.previousButton);
        mPlaylistButton = (ImageButton) findViewById(R.id.playlistButton);
        mRepeatButton = (ImageButton) findViewById(R.id.repeatButton);
        mShuffleButton = (ImageButton) findViewById(R.id.shuffleButton);

        mTrackProgressBar = (SeekBar) findViewById(R.id.trackProgressBar);

        mTrackNameLabel = (TextView) findViewById(R.id.trackNameLabel);
        mTrackCurrentDurationLabel = (TextView) findViewById(R.id.trackCurrentDurationLabel);
        mTrackTotalDurationLabel = (TextView) findViewById(R.id.trackTotalDurationLabel);

        /**
         * Play button click event
         * Plays a song and changes button to pause image Pauses a song and
         * changes button to play image
         */
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "PlayPause Button clicked");
                if (mIsPlaying) {
                    mPlayPauseButton.setImageResource(R.drawable.btn_pause);
                    mIsPlaying = false;
                }
                else {
                    // Changing button image to pause button
                    mPlayPauseButton.setImageResource(R.drawable.btn_play);
                    mIsPlaying = true;
                }
            }
        });

        /**
         * Forward button click event Forwards song specified seconds
         */
        mForwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "ForwardSeek Button clicked");
            }
        });

        /**
         * Backward button click event Backward song to specified seconds
         */
        mBackwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "BackwardSeek Button clicked");
            }
        });

        /**
         * Next Track button click event
         */
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "NextTrack Button clicked");
            }
        });

        /**
         * Previous Track button click event
         */
        mPreviousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "PreviousTrack Button clicked");
            }
        });

        /**
         * Button Click event for Repeat button Enables repeat flag to true
         */
        mRepeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                toggleRepeat();
            }
        });

        /**
         * Button Click event for Shuffle button Enables shuffle flag to true
         */
        mShuffleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                toggleShuffle();

            }
        });

        /**
         * Button Click event for Playlist click event Launches list activity
         * which displays list of songs
         */
        mPlaylistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "PlaylistButton clicked");
                /*
                 * Intent i = new Intent(getApplicationContext(),
                 * PlayListActivity.class); startActivityForResult(i, 100);
                 */
            }
        });

        mTrackProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            // When user starts moving the progress handler
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch() ran");
                // TODO
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                boolean logProgress = true;
                // TODO if not from touch (user), then the progress changes if the
                // track is playing, which is probably every second, so show less log
                // output for testing:
                if (!fromTouch && !(progress % 10 == 0)) {
                    logProgress = false;
                }

                if (logProgress) {
                    Log.d(TAG, "onProgressChanged(): " + progress);
                }
            }

            // When user stops moving the progress handler
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch() ran");
                // TODO notify service send seekBar location
            }
        });

    }

    protected void toggleShuffle() {
        // TODO Auto-generated method stub

    }

    protected void toggleRepeat() {
        // TODO Auto-generated method stub

    }

}
