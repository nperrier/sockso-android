package com.pugh.sockso.android.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.player.PlayerService;

public class PlayerActivity extends Activity {

    private static final String TAG = PlayerActivity.class.getSimpleName();

    // View buttons
    private ImageButton mPlayButton;
    private ImageButton mForwardButton;
    private ImageButton mBackwardButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private ImageButton mPlaylistButton;
    private ImageButton mRepeatButton;
    private ImageButton mShuffleButton;

    // View seekbar
    private SeekBar mTrackProgressBar;

    // View duration
    private TextView mTrackCurrentDurationLabel;
    private TextView mTrackTotalDurationLabel;

    // View track info
    private TextView mArtistNameLabel;
    private TextView mAlbumNameLabel;
    private TextView mTrackNameLabel;

    // View cover art
    private ImageView mAlbumCover;

    // Service that is playing music in the background
    private PlayerService mService;

    // Is this activity bound to the PlayerService?
    private boolean mIsBound = false;

    private boolean mIsShuffling = false;
    private boolean mIsRepeating = false;
    private boolean mIsPlaying = false;

    // Intent actions
    public static final String ACTION_PLAY = "com.pugh.sockso.android.player.ACTION_PLAY";

    /**
     * Establish a connection with the service.
     * We use an explicit class name because we want a specific service implementation
     * that we know will be running in our own process (and thus won't be supporting
     * component replacement by other applications).
     * private void bindToService() {
     * bindService(new Intent(PlayerActivity.this, PlayerService.class), mConnection,
     * Context.BIND_AUTO_CREATE);
     * mIsBound = true;
     * }
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() ran");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.player);

        // All player buttons
        mPlayButton     = (ImageButton) findViewById(R.id.playPauseButton);
        mForwardButton  = (ImageButton) findViewById(R.id.forwardButton);
        mBackwardButton = (ImageButton) findViewById(R.id.backwardButton);
        mNextButton     = (ImageButton) findViewById(R.id.nextButton);
        mPreviousButton = (ImageButton) findViewById(R.id.previousButton);
        mPlaylistButton = (ImageButton) findViewById(R.id.playlistButton);
        mRepeatButton   = (ImageButton) findViewById(R.id.repeatButton);
        mShuffleButton  = (ImageButton) findViewById(R.id.shuffleButton);

        mTrackProgressBar = (SeekBar) findViewById(R.id.trackProgressBar);

        mTrackNameLabel = (TextView) findViewById(R.id.trackNameLabel);
        mTrackCurrentDurationLabel = (TextView) findViewById(R.id.trackCurrentDurationLabel);
        mTrackTotalDurationLabel   = (TextView) findViewById(R.id.trackTotalDurationLabel);

        /**
         * Play button click event
         * Plays a song and changes button to pause image Pauses a song and
         * changes button to play image
         */
        mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "PlayPause Button clicked");
                togglePlayPause();
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

    private ServiceConnection mServiceConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName classname, IBinder service) {

            Log.d(TAG, "onServiceConnected() ran");

            mService = ((PlayerService.PlayerServiceBinder) service).getService();

            // Assume something is playing when the service says it is,
            // but also if the audio ID is valid but the service is paused.
            // if (mService.getAudioId() >= 0 || mService.isPlaying() || mService.getPath() != null)
            // {
            if (mService.isPlaying()) {
                Log.d(TAG, "service is playing something currently");
                // something is playing now, so just update the view
                
                mIsPlaying = true;
                setRepeatButtonImage();
                setShuffleButtonImage();
                setPlayButtonImage();

                return;
            } 
            else {
                // nothing is playing?
                startPlayback();
            }

            /**
             * TODO Service is dead or not playing anything.
             * If we got here as part of a "play this file" Intent, exit.
             * Otherwise go to the Music app start screen.
             */

            // finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private void startPlayback() {
        Log.d(TAG, "startPlayback() called");

        if (mService == null) {
            Log.d(TAG, "mService is null!");
            return;
        }

        Intent intent = getIntent();
        Uri uri = intent.getData();

        // if (uri != null && uri.toString().length() > 0) {

        try {
            mService.stop(); // stop whatever is currently playing
            // mService.openFile(filename);
            mService.play();
            mIsPlaying = true;
            setPlayButtonImage();
            // setIntent(new Intent());
        }
        catch (Exception ex) {
            Log.d(TAG, "couldn't start playback: " + ex);
        }
        // }

        // updateTrackInfo();
        // long next = refreshNow();
        // queueNextRefresh(next);

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart() ran");
        super.onStart();

        handleIntent(getIntent());

        if ( !mIsBound ) {
            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
            bindService(intent, mServiceConn, BIND_AUTO_CREATE);
            mIsBound = true;
        }

        // updateTrackInfo();
        // long next = refreshNow();
        // queueNextRefresh(next);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop() ran");

        // mPaused = true;

        // mHandler.removeMessages(REFRESH);
        // unregisterReceiver(mStatusListener);
        if (mIsBound) {
            unbindService(mServiceConn);
            mIsBound = false;
        }
        // mPlayerService = null;

        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume() ran");
        super.onResume();

        // updateTrackInfo();
        // setPlayButtonImage();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() ran");

        // mAlbumArtWorker.quit();

        super.onDestroy();
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent() called");

        String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        Log.d(TAG, "intent.getAction(): " + action);
        if (bundle != null) {
            Log.d(TAG, "bundle.isEmpty()?:  " + bundle.isEmpty());

            for (String key : bundle.keySet()) {
                Log.d(TAG, "key: " + key + ", long: " + bundle.getLong("track_id"));
            }
        }
        
        // if (action == ACTION_PLAY) {
        startService(new Intent(this, PlayerService.class));
        // }
    }

    protected void togglePlayPause() {

        if (mService != null) {

            if (mService.isPlaying()) {
                mService.pause();
                mIsPlaying = false;
            }
            else {
                mService.play();
                mIsPlaying = true;
            }

            // TODO refreshNow();
            setPlayButtonImage();
        }
    }

    protected void setPlayButtonImage() {

        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.btn_pause);
        }
        else {
            mPlayButton.setImageResource(R.drawable.btn_play);
        }
    }

    protected void toggleShuffle() {
        // TODO Auto-generated method stub

    }

    protected void setShuffleButtonImage() {

        if (mIsShuffling) {
            mShuffleButton.setImageResource(R.drawable.btn_shuffle);
        }
        else {
            mShuffleButton.setImageResource(R.drawable.btn_shuffle_focused);
        }
    }

    protected void toggleRepeat() {
        // TODO Auto-generated method stub

    }

    protected void setRepeatButtonImage() {

        if (mIsRepeating) {
            mRepeatButton.setImageResource(R.drawable.btn_repeat);
        }
        else {
            mRepeatButton.setImageResource(R.drawable.btn_repeat_focused);
        }
    }

}
