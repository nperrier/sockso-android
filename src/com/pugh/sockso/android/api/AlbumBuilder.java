package com.pugh.sockso.android.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Track;

public class AlbumBuilder extends JSONBuilder<Album> {
	
	/*
	 * "id": 483, 
	 * "name": "Beats, Rhymes and Life", 
	 * "artist": { 
	 *             "id":483,
	 *             "name":"A Tribe Called Quest" 
	 *           }
	 */
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";

	private static final String ARTIST_KEY = "artist";

	// builds a single artist
	@Override
	public Album build(JSONObject albumJSON) throws JSONException {

		Album album = new Album();
		
		album.setId(albumJSON.getInt(ID_KEY));
		album.setName(albumJSON.getString(NAME_KEY));

		//ArtistBuilder artistBuilder = new ArtistBuilder();
		//Artist artist = artistBuilder.build(jsonObject.getJSONObject(ARTIST_KEY));
		//album.setArtist(artist);
		JSONObject artistJSON = albumJSON.getJSONObject(ARTIST_KEY);
		album.setArtist(artistJSON.getString(ARTIST_KEY));
		
		//TrackBuilder trackBuilder = new TrackBuilder();
		//List<Track> tracks = trackBuilder.buildList(jsonObject.getJSONArray(TRACKS_KEY));
		//album.setTracks(tracks);
		
		return album;
	}

	public Album buildWithTracks(JSONObject albumJSON, JSONArray tracksJSON) throws JSONException{
		
		Album album = build(albumJSON);
		
		TrackBuilder trackBuilder = new TrackBuilder();
		List<Track> tracks = trackBuilder.buildList(tracksJSON);
		album.setTracks(tracks);
		
		return album;
	}
	
	// builds an array of Artists
	public List<Album> buildList(JSONArray jsonArray) throws JSONException {

		List<Album> albums = new ArrayList<Album>();

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				albums.add(build(jsonArray.getJSONObject(i)));
			}
		}

		return albums;
	}

}