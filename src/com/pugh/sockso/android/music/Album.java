package com.pugh.sockso.android.music;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Album {
	
	/*
	 * "id": 483, 
	 * "name": "Beats, Rhymes and Life", 
	 * "artist": { 
	 *             "id":483,
	 *             "name":"A Tribe Called Quest" 
	 *           }
	 */
	private static final String ID     = "id";
	private static final String NAME   = "name";
	private static final String ARTIST = "artist";

	private int id = 0;
	private String name;
	private String image; // link to the cover of the album?
	private String artist;
	//private Artist artist;
	//private List<Track> tracks = new ArrayList<Track>();

	public Album() {
	}

	public Album(String name, String artist) {
		this.name = name;
		this.artist = artist;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}
	
/*
	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}
*/
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString(){
		return this.name;
	}
	
	// Creates a single Album object from a JSONObject
	public static Album fromJSON(JSONObject jsonObj) throws JSONException {

		Album album = new Album();
		
		album.setId(jsonObj.getInt(ID));
		album.setName(jsonObj.getString(NAME));

		//ArtistBuilder artistBuilder = new ArtistBuilder();
		//Artist artist = artistBuilder.build(jsonObject.getJSONObject(ARTIST_KEY));
		//album.setArtist(artist);
		JSONObject artistJSON = jsonObj.getJSONObject(ARTIST);
		album.setArtist(artistJSON.getString(NAME));
		
		//TrackBuilder trackBuilder = new TrackBuilder();
		//List<Track> tracks = trackBuilder.buildList(jsonObject.getJSONArray(TRACKS_KEY));
		//album.setTracks(tracks);
		
		return album;
	}

	/*
	public Album buildWithTracks(JSONObject albumJSON, JSONArray tracksJSON) throws JSONException{
		
		Album album = build(albumJSON);
		
		TrackBuilder trackBuilder = new TrackBuilder();
		List<Track> tracks = trackBuilder.buildList(tracksJSON);
		album.setTracks(tracks);
		
		return album;
	}
	*/

	// Builds a list of Albums from a JSONArray
	public static List<Album> fromJSONArray(JSONArray jsonArray) throws JSONException {

		List<Album> albums = new ArrayList<Album>();

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				albums.add(fromJSON(jsonArray.getJSONObject(i)));
			}
		}

		return albums;
	}

}
