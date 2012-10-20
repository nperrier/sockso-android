package com.pugh.sockso.android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.ServerFactory;
import com.pugh.sockso.android.SocksoServer;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;
import com.pugh.sockso.android.music.Track;
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
    private boolean mIsBound     = false;

    private boolean mIsShuffling = false;
    private boolean mIsRepeating = false;
    private boolean mIsPlaying   = false;
    
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
        mTrackCurrentDurationLabel = (TextView) findViewById(R.id.trackCurrentDurationLabel);
        mTrackTotalDurationLabel   = (TextView) findViewById(R.id.trackTotalDurationLabel);

        mTrackNameLabel = (TextView) findViewById(R.id.trackNameLabel);

        mAlbumCover = (ImageView) findViewById(R.id.coverImage);
        
        // TODO Add these to the view:
        //mAlbumNameLabel = (TextView) findViewById(R.id.);
        //mArtistNameLabel = (TextView) findViewById(R.id.);
        
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
            public void onClick(View view) {
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


    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() called");
            
            String action = intent.getAction();
            
            if (action.equals(PlayerService.TRACK_CHANGED)) {
                // Update the UI:
                
                // redraw the artist/title info 
                // set new max for progress bar
                
                updateTrackInfo();
                setPlayButtonImage();
                //queueNextRefresh(1);
            }
            else if (action.equals(PlayerService.TRACK_ENDED)) {
                
                mIsPlaying = false;
                setPlayButtonImage();
            }
        }
    };
    
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName classname, IBinder service) {
            Log.d(TAG, "onServiceConnected() ran");
            mService = ((PlayerService.PlayerServiceBinder) service).getService();
            
            // Now that the service is connected, handle the intent sent to this activity:
            handleIntent(getIntent());
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private void handleIntent(Intent intent) {
        
        String action = intent.getAction();
        Log.d(TAG, "intent.getAction(): " + action);
        
        if (action != null) {
            // Play a track
            if (action.equals(ACTION_PLAY)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    
                    long trackId = bundle.getLong("track_id", -1);
                    
                    if (trackId != -1) {
                        
                        startPlayback(trackId);
                        
                    }
                }
            }
        }

        if (mService.isPlaying() || mService.isPaused()) {
            Log.d(TAG, "service is in the middle of playing something");
            // something is playing now, so just update the view

            if (mService.isPlaying()) {
                mIsPlaying = true;
            }

            setRepeatButtonImage();
            setShuffleButtonImage();
            setPlayButtonImage();
            
            updateTrackInfo();
            
            return;
        }
        else {
            // nothing is playing, but no request to play?
        }
    
    }
    
    
    // TODO Should the Track object be created by the activity or service?
    private Track getTrack( long trackId ) {
        Log.d(TAG, "getTrack() called");
        
        Track track = null;
        
        // Start the PlayerActivity and send it the id
        // of the track which the player activity will retrieve
        // from the content provider and send to the player service
        
        String[] projection = { TrackColumns.SERVER_ID, 
                                TrackColumns.ARTIST_NAME, 
                                TrackColumns.NAME,
                                TrackColumns.TRACK_NO
                              };
        Uri uri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + TrackColumns.TABLE_NAME + "/" + trackId);
        
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, projection, null, null, null);
        
        Log.d(TAG, "col count: " + cursor.getColumnCount());
        Log.d(TAG, "column_name[0]: " + cursor.getColumnName(0));
        Log.d(TAG, "row count: " + cursor.getCount());
        
        cursor.moveToNext();
        
        long serverTrackId = cursor.getLong(0);
        String artistName = cursor.getString(1);
        String trackName  = cursor.getString(2);
        int trackNumber   = cursor.getInt(3);
        
        cursor.close();
        Log.d(TAG, "serverTrackId: " + serverTrackId);
        
        track = new Track();
        track.setId(trackId); // TODO Should be long type
        track.setServerId(serverTrackId);
        track.setName(trackName);
        track.setArtist(artistName);
        track.setTrackNumber(trackNumber);
        
        return track;
    }
    
    private void startPlayback( long trackId ) {
        Log.d(TAG, "startPlayback() called with trackId: " + trackId);

        if (mService == null) {
            Log.d(TAG, "mService is null");
            return;
        }

        Track serviceTrack = mService.getTrack();
        
        if (serviceTrack == null) {
            Log.d(TAG, "track is null!");
        }
        
        // Only start track if the track is different
        if (serviceTrack == null || ( serviceTrack != null && trackId != serviceTrack.getId() ) ) {

            Track track = getTrack(trackId);
            
            try {
                mService.stop(); // stop whatever is currently playing
                // mService.openFile(filename);
                mService.setTrack(track);
                
                updateTrackInfo();
                
                mService.play();
                mIsPlaying = true;
                setPlayButtonImage();

                // This resets the intent, I assume?
                //setIntent(new Intent());
            }
            catch (Exception e) {
                Log.d(TAG, "Unable to start playback: " + e.getMessage());
            }
        }

        //updateTrackInfo();
        // long next = refreshNow();
        // queueNextRefresh(next);
    }

    // This should update the UI to reflect the current state of the player
    // for the Track's data (name, artist, album, image, etc..)
    private void updateTrackInfo() {
        Log.d(TAG, "updateTrackInfo() called");
        
        Track track = mService.getTrack();
        
        if ( track != null ) {
            //mArtistNameLabel.setText(track.getArtist());
            //mAlbumNameLabel.setText(track.getAlbum());
            mTrackNameLabel.setText(track.getName());
            
            SocksoServer server = ServerFactory.getServer(this);
            CoverArtFetcher coverFetcher = new CoverArtFetcher(server);
            coverFetcher.download("tr" + track.getServerId(), mAlbumCover);
        }
        
    }

    // This should update the parts of the UI that need to change quickly and often:
    // * progress bar
    // * timers
    private void refreshNow() {
        // TODO
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart() ran");
        super.onStart();

        // Start the PlayerService
        Intent playerIntent = new Intent(this, PlayerService.class);
        startService(playerIntent);
        
        // Bind to PlayerService
        if ( !mIsBound ) {
            Intent bindIntent = new Intent(PlayerActivity.this, PlayerService.class);
            bindService(bindIntent, mServiceConn, BIND_AUTO_CREATE);
            mIsBound = true;
        }
        
        // Setup listener to PlayerService
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.TRACK_CHANGED);
        intentFilter.addAction(PlayerService.TRACK_ENDED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mStatusListener, new IntentFilter(intentFilter));
        
        //updateTrackInfo();
        // long next = refreshNow();
        // queueNextRefresh(next);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop() ran");

        // mPaused = true;

        // mHandler.removeMessages(REFRESH);
        if (mIsBound) {
            unbindService(mServiceConn);
            mIsBound = false;
        }
        
        // unregisterReceiver(mStatusListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStatusListener);
        
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause() ran");
        super.onPause();
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "onResume() ran");
        super.onResume();
        //updateTrackInfo();
        // setPlayButtonImage();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() ran");

        // mAlbumArtWorker.quit();

        super.onDestroy();
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
