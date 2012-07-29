package com.pugh.sockso.android.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.player.MusicUtils;
import com.pugh.sockso.android.player.PlayerService;
import com.pugh.sockso.android.player.PlayerServiceImpl;

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

    private TextView mArtistNameLabel;
    private TextView mAlbumNameLabel;
    private TextView mTrackNameLabel;
    private TextView mTrackCurrentDurationLabel;
    private TextView mTrackTotalDurationLabel;


    private ImageView mAlbumCover;

    // TODO Handler to update UI timer, progress bar etc.
    private Handler mHandler = new Handler();

    private PlayerService mPlayerService;

    private boolean mIsBound = false;

    private static final int SEEK_FORWARD_INCREMENT = 5000; // 5000 milliseconds
    private static final int SEEK_BACKWARD_INCREMENT = 5000; // 5000 milliseconds
    private static final long UPDATE_PROGRESS_BAR_DELAY = 100; // 100 milliseconds

    private boolean mIsShuffle = false;
    private boolean mIsRepeat = false;
    private boolean mPaused = false;
    private long mDuration = 0;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

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
                togglePlayPause();
            }
        });

        /**
         * Forward button click event Forwards song specified seconds
         */
        mForwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                seekForward();
            }
        });

        /**
         * Backward button click event Backward song to specified seconds
         */
        mBackwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                seekBackward();
            }
        });

        /**
         * Next Track button click event
         */
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "nextTrack Button clicked");
                if (mPlayerService == null) {
                    return;
                }

                mPlayerService.nextTrack();
            }
        });

        /**
         * Previous Track button click event
         */
        mPreviousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mPlayerService == null) {
                    return;
                }

                mPlayerService.previousTrack();
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
                /*
                 * if(isShuffle){ isShuffle = false;
                 * Toast.makeText(getApplicationContext(), "Shuffle is OFF",
                 * Toast.LENGTH_SHORT).show();
                 * mShuffleButton.setImageResource(R.drawable.btn_shuffle); }else{
                 * // make repeat to true isShuffle= true;
                 * Toast.makeText(getApplicationContext(), "Shuffle is ON",
                 * Toast.LENGTH_SHORT).show(); // make shuffle to false isRepeat
                 * = false;
                 * mShuffleButton.setImageResource(R.drawable.btn_shuffle_focused);
                 * mRepeatButton.setImageResource(R.drawable.btn_repeat); }
                 */
            }
        });

        /**
         * Button Click event for Playlist click event Launches list activity
         * which displays list of songs
         */
        mPlaylistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                boolean logProgress = true;
                // TODO if not from touch (user), then the progress changes if the
                // track is playing, which is probably every second, so show less log
                // output
                // for testing:
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

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, UPDATE_PROGRESS_BAR_DELAY);
    }

    /**
     * Background Runnable thread to update track timer progress
     */
    private Runnable mUpdateTimeTask = new Runnable() {

        @Override
        public void run() {

            long totalDuration = mPlayerService.trackDuration();
            long currentDuration = mPlayerService.position();

            // Displaying Total Duration time
            mTrackTotalDurationLabel.setText("" + MusicUtils.msToTrackTime(totalDuration));

            // Displaying time completed playing
            mTrackCurrentDurationLabel.setText("" + MusicUtils.msToTrackTime(currentDuration));

            // Updating progress bar
            int progress = (int) (MusicUtils.getProgressPercentage(currentDuration, totalDuration));

            mTrackProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, UPDATE_PROGRESS_BAR_DELAY);
        }
    };

    @Override
    public void onStart() {
        Log.d(TAG, "onStart() ran");
        super.onStart();

        mPaused = false;

        bindToService();

        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop() ran");

        mPaused = true;

        // mHandler.removeMessages(REFRESH);
        // unregisterReceiver(mStatusListener);

        unbindService(mConnection);
        mPlayerService = null;

        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume() ran");
        super.onResume();

        updateTrackInfo();
        setPauseButtonImage();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() ran");

        mAlbumArtWorker.quit();

        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName classname, IBinder binder) {

            mPlayerService = ((PlayerServiceImpl.PlayerServiceBinder) binder).getService();

            startPlayback();

            try {
                // Assume something is playing when the service says it is,
                // but also if the audio ID is valid but the service is paused.
                if (mPlayerService.getAudioId() >= 0 || mPlayerService.isPlaying() || mPlayerService.getPath() != null) {

                    // something is playing now, we're done
                    mRepeatButton.setVisibility(View.VISIBLE);
                    mShuffleButton.setVisibility(View.VISIBLE);
                    mQueueButton.setVisibility(View.VISIBLE);

                    setRepeatButtonImage();
                    setShuffleButtonImage();
                    setPauseButtonImage();

                    return;
                }
            }
            catch (RemoteException e) {
                Log.e(TAG, "onServiceConnected() caught RemoteException: " + e.getMessage());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mPlayerService = null;
        }

    };

    /**
     * Establish a connection with the service.
     * We use an explicit class name because we want a specific service implementation
     * that we know will be running in our own process (and thus won't be supporting
     * component replacement by other applications).
     */
    private void bindToService() {
        bindService(new Intent(PlayerActivity.this, PlayerServiceImpl.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    protected void toggleShuffle() {
        // TODO Auto-generated method stub

    }

    protected void seekBackward() {
        // TODO Auto-generated method stub

    }

    protected void seekForward() {
        // TODO Auto-generated method stub

    }

    /**
     * Receiving song index from playlist view and play the song
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        /*
         * super.onActivityResult(requestCode, resultCode, data);
         * if(resultCode == 100){ currentSongIndex =
         * intent.getExtras().getInt("songIndex"); // play selected song
         * playSong(currentSongIndex); }
         */
    }

    private void togglePlayPause() {
        try {
            if (mPlayerService != null) {
                if (mPlayerService.isPlaying()) {
                    mPlayerService.pause();
                }
                else {
                    mPlayerService.play();
                }
                refreshNow();
                setPauseButtonImage();
            }
        }
        catch (RemoteException ex) {}
    }

    private void setPauseButtonImage() {
        if (mPlayerService != null && mPlayerService.isPlaying()) {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        else {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void toggleRepeat() {
        // TODO
    }

    private void queueNextRefresh(long delay) {
        if (!mPaused) {
            Message msg = mHandler.obtainMessage(REFRESH);
            mHandler.removeMessages(REFRESH);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private long refreshNow() {

        if (mPlayerService == null) {
            return 500;
        }

        try {

            long pos = mPlayerService.position();
            if ((pos >= 0) && (mDuration > 0)) {

                mTrackCurrentDurationLabel.setText(MusicUtils.msToTrackTime(this, pos / 1000));
                int progress = (int) (1000 * pos / mDuration);
                mTrackProgressBar.setProgress(progress);

                if (mPlayerService.isPlaying()) {
                    mTrackCurrentDurationLabel.setVisibility(View.VISIBLE);
                }
                else {
                    // blink the counter
                    int vis = mTrackCurrentDurationLabel.getVisibility();
                    mTrackCurrentDurationLabel.setVisibility(vis == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    return 500;
                }
            }
            else {
                mTrackCurrentDurationLabel.setText("--:--");
                mTrackProgressBar.setProgress(1000);
            }

            // calculate the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            long remaining = 1000 - (pos % 1000);

            // approximate how often we would need to refresh the slider to move it smoothly
            int width = mTrackProgressBar.getWidth();
            if (width == 0) {
                width = 320;
            }

            long smoothrefreshtime = mDuration / width;
            if (smoothrefreshtime > remaining) {
                return remaining;
            }

            if (smoothrefreshtime < 20) {
                return 20;
            }

            return smoothrefreshtime;
        }
        catch (RemoteException ex) {}

        return 500;
    }

    private void updateTrackInfo() {

        if (mPlayerService == null) {
            return;
        }

        try {
            String path = mPlayerService.getPath();
            if (path == null) {
                finish();
                return;
            }

            long songId = mPlayerService.getAudioId();
            if (songId < 0 && path.toLowerCase().startsWith("http://")) {

                // Once we can get album art and meta data from MediaPlayer,
                // we can show that info again when streaming.
                ((View) mArtistName.getParent()).setVisibility(View.INVISIBLE);
                ((View) mAlbumName.getParent()).setVisibility(View.INVISIBLE);

                mAlbumCover.setVisibility(View.GONE);
                mTrackName.setText(path);

                mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                mAlbumArtHandler.obtainMessage(GET_ALBUM_ART, new AlbumSongIdWrapper(-1, -1)).sendToTarget();
            }
            else {
                ((View) mArtistName.getParent()).setVisibility(View.VISIBLE);
                String artistName = mPlayerService.getAlbumName();
                if (MediaStore.UNKNOWN_STRING.equals(artistName)) {
                    artistName = getString(R.string.unknown_artist_name);
                }
                mArtistName.setText(artistName);

                ((View) mAlbumName.getParent()).setVisibility(View.VISIBLE);
                String albumName = mPlayerService.getAlbumName();
                long albumId = mPlayerService.getAlbumId();
                if (MediaStore.UNKNOWN_STRING.equals(albumName)) {
                    albumName = getString(R.string.unknown_album_name);
                    albumId = -1;
                }
                mAlbumName.setText(albumName);

                mTrackName.setText(mPlayerService.getTrackName());
                mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                mAlbumArtHandler.obtainMessage(GET_ALBUM_ART, new AlbumSongIdWrapper(albumId, songid)).sendToTarget();
                mAlbumCover.setVisibility(View.VISIBLE);
            }

            mDuration = mPlayerService.trackDuration();
            mTrackTotalDurationLabel.setText(MusicUtils.msToTrackTime(this, mDuration / 1000));

        }
        catch (RemoteException ex) {
            finish();
        }
    }

    public class AlbumArtHandler extends Handler {

        private long mAlbumId = -1;

        public AlbumArtHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            
            long albumid = ((AlbumSongIdWrapper) message.obj).albumid;
            long songid  = ((AlbumSongIdWrapper) message.obj).songid;
            
            if (message.what == GET_ALBUM_ART && (mAlbumId != albumid || albumid < 0)) {
                
                // while decoding the new image, show the default album art
                Message numessage = mHandler.obtainMessage(ALBUM_ART_DECODED, null);
                mHandler.removeMessages(ALBUM_ART_DECODED);
                mHandler.sendMessageDelayed(numessage, 300);
                
                // Don't allow default artwork here, because we want to fall back to song-specific
                // album art if we can't find anything for the album.
                Bitmap bm = MusicUtils.getArtwork(PlayerActivity.this, songid, albumid, false);
                
                if (bm == null) {
                    bm = MusicUtils.getArtwork(PlayerActivity.this, songid, -1);
                    albumid = -1;
                }
                
                if (bm != null) {
                    numessage = mHandler.obtainMessage(ALBUM_ART_DECODED, bm);
                    mHandler.removeMessages(ALBUM_ART_DECODED);
                    mHandler.sendMessage(numessage);
                }
                
                mAlbumId = albumid;
            }
        }
    }
}
