package com.pugh.sockso.android.api;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.Track;

public interface SocksoAPI {

	public ServerInfo getServerInfo() throws IOException, JSONException;

	public Album getAlbum(final String id) throws IOException, JSONException;

	public List<Album> getAlbums() throws IOException, JSONException;

    public List<Album> getAlbums(long from) throws IOException, JSONException;

	public Artist getArtist(final String id) throws IOException, JSONException;

	public List<Artist> getArtists() throws IOException, JSONException;
    
	public List<Artist> getArtists(long from) throws IOException, JSONException;

	public Track getTrack(final String id) throws IOException, JSONException;

	public List<Track> getTracks() throws IOException, JSONException;
	
	public List<Track> getTracks(long from) throws IOException, JSONException;
}
