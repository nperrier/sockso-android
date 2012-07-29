package com.pugh.sockso.android.player;

import java.io.IOException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.activity.PlayerActivity;
import com.pugh.sockso.android.music.Track;

// IntentService?
public class PlayerServiceImpl extends Service implements PlayerService, AudioManager.OnAudioFocusChangeListener,
        OnPreparedListener, OnCompletionListener, OnErrorListener {

    private static final String TAG = PlayerServiceImpl.class.getSimpleName();

    private static final String WIFI_LOCK_NAME = "com.pugh.sockso.android.player.WIFI_LOCK";

    // The ID we use for the notification icon
    private static final int NOTIFICATION_ID = 1;

    // Action Intents
    public static final String PLAY_ACTION = "com.pugh.sockso.android.player.PLAY";
    public static final String PAUSE_ACTION = "com.pugh.sockso.android.player.PAUSE";
    public static final String STOP_ACTION = "com.pugh.sockso.android.player.STOP";
    public static final String NEXT_TRACK_ACTION = "com.pugh.sockso.android.player.NEXT_TRACK";
    public static final String PREV_TRACK_ACTION = "com.pugh.sockso.android.player.PREV_TRACK";
    public static final String LOAD_TRACK_ACTION = "com.pugh.sockso.android.player.SET_TRACK";

    // TODO Player States
    private static final int PREPARING_STATE = 1;
    private static final int PLAYING_STATE = 2;
    private static final int SKIPPING_STATE = 3;
    private static final int PAUSED_STATE = 4;
    private static final int STOPPED_STATE = 5;
    private static final int ERROR_STATE = 6;

    private static final float VOLUME_MAX = 1.0f;
    private static final float VOLUME_MIN = 0.1f;

    // To indicate the state of the media player
    private int mState = STOPPED_STATE;

    // Whether the song we are playing is streaming from the network
    // This determines whether or not we should use the wifi lock
    // One day, I'd like to have a local cache of most/recently played tracks
    // and let the user set the size (or enable/disable) the cache
    boolean mIsStreaming = false;

    // A wifi lock to keep the wifi on while phone is idle
    private WifiLock mWifiLock;

    // Needed to add/remove icon from the notification bar when streaming music
    private NotificationManager mNotificationManager;

    // The notification icon for the notification bar
    private Notification mNotification = null;

    // Needed for volume and ringer mode control.
    private AudioManager mAudioManager;

    // The media player to play music
    private MediaPlayer mPlayer = null;

    private Track mCurrentTrack = null;

    // Binder object for clients that want to call methods on this service
    private final IBinder mBinder = new PlayerServiceBinder();

    // Called when media player is done preparing.
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared() ran");

        // The media player is done preparing. That means we can start playing!
        mState = PLAYING_STATE;
        updateNotification(mCurrentTrack.getName() + " " + R.string.notification_track_playing);
        configAndStartMediaPlayer();
    }

    // Called when media player is done playing current song.
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion() ran");

        // The media player finished playing the current song, so we go ahead and start the next.
        nextTrackAction();
    }

    // If an error occurs in the media player, this is called
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError() ran");

        Toast.makeText(getApplicationContext(), "Media player error! Reseting.", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));

        mState = STOPPED_STATE;
        relaxResources(true);
        // giveUpAudioFocus();
        return true; // true indicates we handled the error
    }

    // TODO Audio Focus
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN:
            // resume playback
            if (mPlayer == null) {
                createMediaPlayerIfNeeded();
            }
            else if (!mPlayer.isPlaying()) {
                mPlayer.start();
            }
            mPlayer.setVolume(VOLUME_MAX, VOLUME_MAX);
            break;

        case AudioManager.AUDIOFOCUS_LOSS:
            // Lost focus for an unbounded amount of time: stop playback and release media player
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            relaxResources(true);
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            // Lost focus for a short time, but we have to stop playback.
            // We don't release the media player because playback is likely to resume
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            // Lost focus for a short time, but it's ok to keep playing
            // at an attenuated level
            if (mPlayer.isPlaying()) {
                mPlayer.setVolume(VOLUME_MIN, VOLUME_MIN);
            }
            break;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() ran");

        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL,
                WIFI_LOCK_NAME);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // mDefaultAlbumArt = BitmapFactory.decodeResource(getResources(),
        // R.drawable.dummy_album_art);

        // mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
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

        public PlayerServiceImpl getService() {
            return PlayerServiceImpl.this;
        }
    }

    /**
     * Reconfigures MediaPlayer according to audio focus settings and starts/restarts it.
     * This method starts/restarts the MediaPlayer respecting the current audio focus state.
     * So if we have focus, it will play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is allowed by the
     * current focus settings.
     * This method assumes mPlayer != null, so if you are calling it,
     * you have to do so from a context where you are sure this is the case.
     */
    private void configAndStartMediaPlayer() {
        Log.d(TAG, "configAndStartMediaPlayer() ran");

        /*
         * if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
         * // If we don't have audio focus and can't duck, we have to pause, even if mState
         * // is State.Playing. But we stay in the Playing state so that we know we have to resume
         * // playback once we get the focus back.
         * if (mPlayer.isPlaying()) mPlayer.pause();
         * return;
         * }
         * else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
         * mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME); // we'll be relatively quiet
         * else
         */

        mPlayer.setVolume(VOLUME_MAX, VOLUME_MAX); // we can be loud

        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    /**
     * Makes sure the media player exists and has been reset.
     * This will create the media player if needed, or reset the existing media player if one
     * already exists.
     */
    private void createMediaPlayerIfNeeded() {
        Log.d(TAG, "createMediaPlayerIfNeeded() ran");

        if (mPlayer == null) {
            mPlayer = new MediaPlayer();

            /*
             * Make sure the media player will acquire a wake-lock while playing.
             * If we don't do that, the CPU might go to sleep while the song is playing,
             * causing playback to stop.
             * Remember that to use this, we have to declare the android.permission.WAKE_LOCK
             * permission in AndroidManifest.xml.
             */
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        }
        else {
            mPlayer.reset();
        }
    }

    /**
     * Called when we receive an Intent.
     * When we receive an intent sent to us via startService(), this is the method that gets called.
     * So here we react appropriately depending on the Intent's action, which specifies what is
     * being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (action.equals(LOAD_TRACK_ACTION)) {
            loadTrackAction(intent);
        }
        if (action.equals(PLAY_ACTION)) {
            playAction();
        }
        else if (action.equals(PAUSE_ACTION)) {
            pauseAction();
        }
        else if (action.equals(STOP_ACTION)) {
            stopAction();
        }
        else if (action.equals(NEXT_TRACK_ACTION)) {
            nextTrackAction();
        }
        else if (action.equals(PREV_TRACK_ACTION)) {
            prevTrackAction();
        }

        // Means we started the service, but don't want it to restart in case it's killed.
        return START_NOT_STICKY;
    }

    /* ********************************************************************************************* */

    private void playAction() {
        Log.d(TAG, "playAction() ran");
        // tryToGetAudioFocus();

        // actually play the song
        if (mState == STOPPED_STATE) {
            // TODO If we're stopped, just go ahead to the next song and start playing
            nextTrackAction();
        }
        else if (mState == PAUSED_STATE) {

            // If we're paused, just continue playback and restore the 'foreground service' state.
            mState = PLAYING_STATE;

            setUpAsForeground(mCurrentTrack.getName() + " " + R.string.notification_track_playing);

            configAndStartMediaPlayer();
        }

        // check for already playing
        /*
         * if (mPlayer.isPlaying()) {
         * if (mp!=null) {
         * mp.pause();
         * // Changing button image to play button
         * btnPlay.setImageResource(R.drawable.btn_play);
         * }
         * }
         * else {
         * // Resume song
         * if (mp!=null) {
         * mp.start();
         * // Changing button image to pause button
         * btnPlay.setImageResource(R.drawable.btn_pause);
         * }
         * }
         */

        // TODO Notify any controllers that we're now playing
    }

    /**
     * User wants to play a song directly by URL or path.
     * The URL or path comes in the "data" part of the Intent.
     * This Intent is sent by clicking a track in the TrackListFragmentActivity.
     */
    private void loadTrackAction(Intent intent) {
        Log.d(TAG, "setTrackAction(i) ran");

        if (mState == PLAYING_STATE || mState == PAUSED_STATE || mState == STOPPED_STATE) {

            Log.i(TAG, "Playing from URL/path: " + intent.getData().toString());

            // tryToGetAudioFocus();
            // TODO Intent can send a URI
            // or something else?
            // intent.getData()
            playNextTrack();
        }
    }

    public void playNextTrack() {
        Log.d(TAG, "playNextTrack(i) ran");

        mState = STOPPED_STATE;

        relaxResources(false); // release everything except MediaPlayer

        try {

            if (url != null) {
                // set the source of the media player to a manual URL or path
                createMediaPlayerIfNeeded();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(url);
                
                // if track exist in future cache, this variable will tell whether
                // we need a wifi lock or not
                mIsStreaming = url.startsWith("http:") || url.startsWith("https:");
                
                mState = PREPARING_STATE;
                
                // TODO: Get track from a PlaylistManager
                //mCurrentTrack = PlaylistManager;
                
            }
            else {
                Log.e(TAG, "url from track: " + mCurrentTrack.toString() + " is null!");
                return;
            }

            setUpAsForeground(mCurrentTrack.getName() + " (loading)");

            // starts preparing the media player in the background. When it's done, it will call
            // our OnPreparedListener (that is, the onPrepared() method on this class, since we set
            // the listener to 'this').
            //
            // Until the media player is prepared, we *cannot* call start() on it!
            mPlayer.prepareAsync();

            // If we are streaming from the internet, we want to hold a Wifi lock, which prevents
            // the Wifi radio from going to sleep while the song is playing. If, on the other hand,
            // we are *not* streaming, we want to release the lock if we were holding it before.
            if (mIsStreaming) {
                mWifiLock.acquire();
            }
            else if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }

        }
        catch (IOException ex) {
            Log.e(TAG, "IOException playing next track: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /* ********************************************************************************************* */

    private void prevTrackAction() {
        Log.d(TAG, "prevTrackAction() ran");

        /*
         * if(currentSongIndex > 0){
         * playSong(currentSongIndex - 1);
         * currentSongIndex = currentSongIndex - 1;
         * }
         * else {
         * // play last song
         * playSong(songsList.size() - 1);
         * currentSongIndex = songsList.size() - 1;
         * }
         */
    }

    private void nextTrackAction() {
        Log.d(TAG, "nextTrackAction() ran");

        /*
         * // check if next song is there or not
         * if(currentSongIndex < (songsList.size() - 1)){
         * playSong(currentSongIndex + 1);
         * currentSongIndex = currentSongIndex + 1;
         * }else{
         * // play first song
         * playSong(0);
         * currentSongIndex = 0;
         * }
         */
    }

    private void stopAction() {
        Log.d(TAG, "stopAction() ran");

        if (mState == PLAYING_STATE || mState == PAUSED_STATE) {

            mState = STOPPED_STATE;

            // let go of all resources
            relaxResources(true);
            // giveUpAudioFocus();

            // TODO Tell any remote controls that our playback state is 'paused'.

            // service is no longer necessary. Will be started again if needed.
            stopSelf();
        }
    }

    private void pauseAction() {
        Log.d(TAG, "pauseAction() ran");

        if (mState == PLAYING_STATE) {

            // Pause media player and cancel the 'foreground service' state.
            mState = PAUSED_STATE;
            mPlayer.pause();

            // while paused, we always retain the MediaPlayer
            relaxResources(false);

            // do not give up audio focus
        }

        // TODO: Tell any remote controls that our playback state is 'paused'.
    }

    // TODO
    private void seekForward() {

        int currentPosition = mPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mPlayer.getDuration()) {
            // forward song
            mPlayer.seekTo(currentPosition + seekForwardTime);
        }
        else {
            // forward to end position
            mPlayer.seekTo(mPlayer.getDuration());
        }
    }

    private void seekBackwards() {

        int currentPosition = mPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mPlayer.seekTo(currentPosition - seekBackwardTime);
        }
        else {
            // backward to starting position
            mPlayer.seekTo(0);
        }
    }

    // TODO This should be called from the activity when the user is done moving the
    // seek bar to a new time position
    // onStopTrackingTouch()
    /**
     * Seeks to the position specified.
     * 
     * @param pos The position to seek to, in milliseconds
     */
    private void seekTo(long pos) {

    }

    /**
     * Updates the notification.
     * TODO: Use Notification.Builder here instead?
     */
    private void updateNotification(String text) {
        Log.d(TAG, "updateNotification() ran");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(
                getApplicationContext(), PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification.setLatestEventInfo(getApplicationContext(), TAG, text, pendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * Configures service as a foreground service.
     * A foreground service is a service that's doing something the user is actively aware of (such
     * as playing music),
     * and must appear to the user as a notification.
     * That's why we create the notification here.
     * TODO: Use Notification.Builder here instead?
     */
    private void setUpAsForeground(String text) {
        Log.d(TAG, "setUpAsForeground() ran");

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),
                PlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification = new Notification();
        mNotification.tickerText = text;
        mNotification.icon = R.drawable.ic_stat_playing;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.setLatestEventInfo(getApplicationContext(), TAG, text, pi);

        startForeground(NOTIFICATION_ID, mNotification);
    }

    /**
     * Releases resources used by the service for playback.
     * This includes the "foreground service" status and notification, the wake locks and possibly
     * the MediaPlayer.
     * 
     * @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
     */
    void relaxResources(boolean releaseMediaPlayer) {
        Log.d(TAG, "relaxResources() ran");

        // stop being a foreground service
        stopForeground(true);

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }

        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() ran");
        // Service is being killed, so make sure we release our resources
        mState = STOPPED_STATE;
        relaxResources(true);
        // mBinder = null;
        // giveUpAudioFocus();
    }

}
