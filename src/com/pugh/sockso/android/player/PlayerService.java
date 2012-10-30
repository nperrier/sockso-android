package com.pugh.sockso.android.player;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.activity.PlayerActivity;
import com.pugh.sockso.android.music.Track;

public class PlayerService extends Service implements OnPreparedListener, OnCompletionListener,
        OnBufferingUpdateListener, OnErrorListener {

    private static final String TAG = PlayerService.class.getSimpleName();

    // State for when the player is done preparing, currently playing a track, or paused:
    private boolean mIsInitialized = false;
    
    // Media Player
    private MediaPlayer mPlayer = null;

    // Notification status bar
    private Notification mNotification = null;

    private static final int NOTIFICATION_ID = 1; // just a number to identify notification type

    // Playlist of tracks (can be one)
    private List<Track> mPlaylist = null;

    // Binder object for clients that want to call methods on this service
    private IBinder mBinder = new PlayerServiceBinder();

    // TODO Remove once playlist setup
    private int mPlayIndex = 0;

    // This is to notify the activity that a track changed (or ended) and should update the UI
    public static final String TRACK_STARTED = "com.pugh.sockso.android.player.TRACK_STARTED";
    public static final String TRACK_RESUMED = "com.pugh.sockso.android.player.TRACK_RESUMED";
    public static final String TRACK_CHANGED = "com.pugh.sockso.android.player.TRACK_CHANGED";
    public static final String TRACK_ENDED   = "com.pugh.sockso.android.player.TRACK_ENDED";
    public static final String TRACK_ERROR   = "com.pugh.sockso.android.player.TRACK_ERROR";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        
        mPlaylist = new ArrayList<Track>();
        
        super.onCreate();
    }
    

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");

        mBinder = null;

        super.onDestroy();

        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }

        mPlayer = null;

        // clearNotification();
        // releaseLocks();
    }
    

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() ran");
        return mBinder;
    }
    

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() ran");
        return super.onUnbind(intent);
    }

    /**
     * Class for clients to access. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * Supposably there is a memory leak in Android with this approach:
     * http://code.google.com/p/android/issues/detail?id=6426
     * Consider a work-around
     */
    public class PlayerServiceBinder extends Binder {

        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public boolean isPlaying() {
        Log.d(TAG, "isPlaying() called");

        if ( mPlayer == null ) {
            return false;
        }

        return mPlayer.isPlaying();
    }

    public void pause() {
        Log.d(TAG, "pause() called");

        if (mPlayer != null && mPlayer.isPlaying()) {

            mPlayer.pause();

            // stop being a foreground service
            stopForeground(true);
        }
    }

    // Sets the playlist to a single track
    public void open(Track track) {
        Log.d(TAG, "open(): " + track.getName());
        
        if ( isPlaying() ) {
            throw new IllegalStateException("Can't call open() while track is playing!");
        }
        
        mPlaylist.clear();
        mPlaylist.add(track);
        mPlayIndex = 0;
    }

    // Sets the playlist to a list of tracks
    public void open( List<Track> tracks ) {
        Log.d(TAG, "open(): " + tracks.size());
        
        if ( isPlaying() ) {
            throw new IllegalStateException("Can't call open() while track is playing!");
        }
        
        mPlaylist.clear();
        mPlaylist.addAll(tracks);
        mPlayIndex = 0;
    }
    
    // TODO: This should return the currently playing track id OR Track object
    public Track getTrack() {
        return mPlaylist.get(mPlayIndex);
    }
    
    /**
     * Reconfigures MediaPlayer according to audio focus settings and starts/restarts it. This
     * method starts/restarts the MediaPlayer respecting the current audio focus state. So if
     * we have focus, it will play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is allowed by the
     * current focus settings. This method assumes mPlayer != null, so if you are calling it,
     * you have to do so from a context where you are sure this is the case.
     */
    private void configAndStartMediaPlayer() {
        Log.d(TAG, "configAndStartMediaPlayer() called");

        // TODO Volume should be initially max, but ducked for phone calls, etc..
        mPlayer.setVolume(1.0f, 1.0f);

        mPlayer.start();
        
        setUpAsForeground(getString(R.string.notification_playing));
    }
    
    /**
     * TODO
     * Starts playback of a previously opened file.
     */
    public void play() {
        Log.d(TAG, "play() called");

        if ( mPlaylist.size() == 0 ) {
            return; 
        }
        
        if (mPlayer != null && mIsInitialized) {
            
            configAndStartMediaPlayer();
            notifyChange(TRACK_RESUMED);
        }
        else {
            
            createMediaPlayerIfNeeded();

            Track track = mPlaylist.get(mPlayIndex);
            
            // hardcoded TODO remove
            String url = "http://sockso.perrierliquors.com:4444/stream/" + track.getServerId();

            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            
            try {
                mPlayer.setDataSource(url);
                mPlayer.prepareAsync();
            }
            catch (Exception e) {
                Log.e(TAG, "Exception with url " + url + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {        
        Log.d(TAG, "onPrepared() called");
        
        mIsInitialized = true;
        configAndStartMediaPlayer();
        notifyChange(TRACK_STARTED);
    }
    
    public void stop() {
        Log.d(TAG, "stop() called");
        if (mPlayer != null) {
            mPlayer.stop();
            
            mIsInitialized = false;
            // stop being a foreground service
            stopForeground(true);
        }
    }

    /**
     * Makes sure the media player exists and has been reset.
     * This will create the media player if needed, or reset the existing media player if one
     * already exists.
     */
    private void createMediaPlayerIfNeeded() {

        if (mPlayer == null) {

            Log.d(TAG, "Creating a new MediaPlayer!");

            mPlayer = new MediaPlayer();

            // Listeners to the media player's events
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);

            /**
             * Make sure the media player will acquire a wake-lock while playing.
             * If we don't do that, the CPU might go to sleep while the song is playing, causing
             * playback to stop.
             * Remember that to use this, we have to declare the android.permission.WAKE_LOCK
             * permission in AndroidManifest.xml.
             * mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
             */
        }
        else {
            mIsInitialized = false;
            mPlayer.reset();
        }
    }

    public synchronized int getPosition() {
        
        if (mIsInitialized) {
            return mPlayer.getCurrentPosition();
        }
        
        return 0;
    }
    
    public synchronized int getDuration() {
        
        if (mIsInitialized) {
            return mPlayer.getDuration();
        }
        
        return 0;
    }
    
    /**
     * Called when the mediaplayer is done playing the current track
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        Log.d(TAG, "onCompletion() called");
        
        stop();
        
        if ( mPlayIndex + 1 >= mPlaylist.size() ) {
            
            notifyChange(TRACK_ENDED);
        }
        else {
            mPlayIndex++;
            play();
            notifyChange(TRACK_CHANGED);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percentage) {
        Log.d(TAG, "onBufferingUpdate(): " + percentage);

        // Intent intent = new Intent(TRACK_BUFFERING);
        // intent.putExtra("percentage", percentage);
        // LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        // TODO: This should provide some feedback to the user if we're streaming
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        Log.e(TAG, "onError() called");
        Log.e(TAG, "what: " + what);
        
        // Reset the player back to a valid state:
        mPlayer.reset();
        mIsInitialized = false;
        
        // Notify the activity so the user can be notified
        notifyChange(TRACK_ERROR);
        
        return true;
    }

    /**
     * Configures service as a foreground service.
     * A foreground service is a service that's doing something the user is actively aware of
     * (such as playing music), and must appear to the user as a notification.
     * That's why we create the notification here.
     */
    private void setUpAsForeground(final String text) {

        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.setAction(PlayerActivity.ACTION_VIEW_PLAYER);
        
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification = new Notification();
        mNotification.tickerText = text;
        mNotification.icon = R.drawable.ic_stat_playing;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), text, pendingIntent);

        startForeground(NOTIFICATION_ID, mNotification);
    }

    /**
     * Notify the change-receivers that something has changed.
     * The intent that is sent contains the following data for the currently playing track:
     * "id" - Integer: the database row ID
     * "artist" - String: the name of the artist
     * "album" - String: the name of the album
     * "track" - String: the name of the track
     * The intent has an action that is one of
     * "com.android.music.metachanged"
     * "com.android.music.queuechanged",
     * "com.android.music.playbackcomplete"
     * "com.android.music.playstatechanged"
     * respectively indicating that a new track has started playing,
     * that the playback queue has changed,
     * that playback has stopped because the last file in the list has been played,
     * or that the play-state changed (paused/resumed).
     */
    private void notifyChange(String what) {
        Log.d(TAG, "Broadcasting message: " + what);
        
        Intent intent = new Intent(what);
        /*
         * TODO Set this stuff
         * intent.putExtra("id", Long.valueOf(getAudioId()));
         * intent.putExtra("artist", getArtistName());
         * intent.putExtra("album",getAlbumName());
         * intent.putExtra("track", getTrackName());
         * intent.putExtra("playing", isPlaying());
         */
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        /*
         * if (what.equals(QUEUE_CHANGED)) {
         * saveQueue(true);
         * }
         * else {
         * saveQueue(false);
         * }
         */
    }
    
}
