package com.pugh.sockso.android.player;

public interface PlayerService {

    void nextTrack();

    void previousTrack();

    long getAlbumId();

    String getAlbumName();

    long getAudioId();

    CharSequence getTrackName();

    long trackDuration();

    String getPath();

    long position();

    boolean isPlaying();

}
