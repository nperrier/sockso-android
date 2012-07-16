package com.pugh.sockso.android.music;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Track implements IMusicItem {

	private static final String ID     = "id";
	private static final String NAME   = "name";
	private static final String ARTIST = "artist";
	private static final String ALBUM  = "album";

	private int id = 0; // local id
	private int serverId = 0; // remote server id
	private String name;
	private int trackNumber;
	private int duration;
	private String image; // link to the cover of the track?
	private int albumId;
	private int artistId;
	//private Album album;
	//private Artist artist;
	private String album;
	private String artist;

	public Track() {
	}

	public Track(String name, String artist, String album) {
		this.name = name;
		this.artist = artist;
		this.setAlbum(album);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public int getArtistId() {
		return artistId;
	}

	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	@Override
	public String toString(){
		return this.name;
	}
	

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
	
	// Creates a single Track object from a JSONObject
	public static Track fromJSON(JSONObject jsonObj) throws JSONException {

		Track track = new Track();
		track.setServerId(jsonObj.getInt(ID));
		track.setName(jsonObj.getString(NAME));

		//ArtistBuilder artistBuilder = new ArtistBuilder();
		//Artist artist = artistBuilder.build(jsonObject.getJSONObject(ARTIST_KEY));
		//track.setArtist(artist);
		JSONObject artistJSON = jsonObj.getJSONObject(ARTIST);
		track.setArtist(artistJSON.getString(NAME));
		
		//AlbumBuilder albumBuilder = new AlbumBuilder();
		//Album album = albumBuilder.build(jsonObject.getJSONObject(ALBUM_KEY));
		//track.setAlbum(album);
		JSONObject albumJSON = jsonObj.getJSONObject(ALBUM);
		track.setAlbum(albumJSON.getString(NAME));	

		return track;
	}

	// Builds a list of Tracks from a JSONArray
	public static List<Track> fromJSONArray(JSONArray jsonArray) throws JSONException {

		List<Track> tracks = new ArrayList<Track>();

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				tracks.add(fromJSON(jsonArray.getJSONObject(i)));
			}
		}

		return tracks;
	}

}
