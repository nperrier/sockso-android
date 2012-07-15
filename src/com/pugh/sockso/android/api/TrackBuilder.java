package com.pugh.sockso.android.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pugh.sockso.android.music.Track;

public class TrackBuilder extends JSONBuilder<Track> {

	/*
	 * "id": 497,
	 * "name": "1nce Again",
	 * "album": {
	 * 		"id":483,
	 * 		"name":"Beats, Rhymes and Life"
	 *          },
	 * "artist": {
	 * 		"id":483,
	 * 		"name":"A Tribe Called Quest"
	 * 			 }
	 */
	
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";
	
	private static final String ARTIST_KEY = "artist";
	private static final String ALBUM_KEY  = "album";
	
	// builds a single artist
	@Override
	public Track build(JSONObject jsonObject) throws JSONException {

		Track track = new Track();
		track.setId(jsonObject.getInt(ID_KEY));
		track.setName(jsonObject.getString(NAME_KEY));

		//ArtistBuilder artistBuilder = new ArtistBuilder();
		//Artist artist = artistBuilder.build(jsonObject.getJSONObject(ARTIST_KEY));
		//track.setArtist(artist);
		JSONObject artistJSON = jsonObject.getJSONObject(ARTIST_KEY);
		track.setArtist(artistJSON.getString(NAME_KEY));
		
		//AlbumBuilder albumBuilder = new AlbumBuilder();
		//Album album = albumBuilder.build(jsonObject.getJSONObject(ALBUM_KEY));
		//track.setAlbum(album);
		JSONObject albumJSON = jsonObject.getJSONObject(ALBUM_KEY);
		track.setAlbum(albumJSON.getString(NAME_KEY));	

		return track;
	}

	// builds an array of Artists
	public List<Track> buildList(JSONArray jsonArray) throws JSONException {

		List<Track> tracks = new ArrayList<Track>();

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				tracks.add(build(jsonArray.getJSONObject(i)));
			}
		}

		return tracks;
	}

}