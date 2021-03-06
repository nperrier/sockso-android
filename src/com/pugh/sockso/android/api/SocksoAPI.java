package com.pugh.sockso.android.api;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.Genre;
import com.pugh.sockso.android.music.Track;

public interface SocksoAPI {

    public ServerInfo getServerInfo() throws IOException, JSONException;

    public Album getAlbum(final String id) throws IOException, JSONException;

    public List<Album> getAlbums() throws IOException, JSONException;

    public List<Album> getAlbums(Date from) throws IOException, JSONException;

    public Artist getArtist(final String id) throws IOException, JSONException;

    public List<Artist> getArtists() throws IOException, JSONException;

    public List<Artist> getArtists(Date from) throws IOException, JSONException;

    public Track getTrack(final String id) throws IOException, JSONException;

    public List<Track> getTracks() throws IOException, JSONException;

    public List<Track> getTracks(Date from) throws IOException, JSONException;

    public Genre getGenre(final String id) throws IOException, JSONException;

    public List<Genre> getGenres() throws IOException, JSONException;

    public List<Genre> getGenres(Date from) throws IOException, JSONException;
}
