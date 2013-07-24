package com.pugh.sockso.android.music;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Track implements MusicItem {

	private static final String ID     = "id";
	private static final String NAME   = "name";
	private static final String NUMBER = "number";
	private static final String ARTIST = "artist";
	private static final String ALBUM  = "album";
	private static final String GENRE  = "genre";
    public static final String COVER_PREFIX = "tr";

	private long id = 0; // local id
	private long serverId = 0; // remote server id
	private String name;
	private int trackNumber;
	private int duration;
	private String image; // link to the cover of the track?
	private long albumId;
	private long artistId;
	private long genreId;
	//private Album album;
	//private Artist artist;
	//private Genre genre;
	private String album;
	private String artist;
	private String genre;

	public Track() {
	}

	public Track(String name, String artist, String album) {
		this.name = name;
		this.artist = artist;
		this.setAlbum(album);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getServerId() {
		return serverId;
	}

	public void setServerId(long serverId) {
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

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public long getArtistId() {
		return artistId;
	}

	public void setArtistId(long artistId) {
		this.artistId = artistId;
	}

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }
	
	@Override
	public String toString(){
		return this.name;
	}

	/*
	 * "id": 497,
	 * "name": "1nce Again",
	 * "track_number": 4,
	 * "album": {
	 * 		"id":483,
	 * 		"name":"Beats, Rhymes and Life"
	 *          },
	 * "artist": {
	 * 		"id":483,
	 * 		"name":"A Tribe Called Quest"
	 * 			 },
	 * "genre": {
	 *      "id":13,
	 *      "name":"Hip-Hop"
	 *          }
	 */
	
	// Creates a single Track object from a JSONObject
	public static Track fromJSON(JSONObject jsonObj) throws JSONException {

		Track track = new Track();
		track.setServerId(jsonObj.getInt(ID));
		track.setName(jsonObj.getString(NAME));
		track.setTrackNumber(jsonObj.getInt(NUMBER));
        
		//ArtistBuilder artistBuilder = new ArtistBuilder();
		//Artist artist = artistBuilder.build(jsonObject.getJSONObject(ARTIST_KEY));
		//track.setArtist(artist);
		JSONObject artistJSON = jsonObj.getJSONObject(ARTIST);
		track.setArtist(artistJSON.getString(NAME));
		track.setArtistId(artistJSON.getInt(ID));
		
		//AlbumBuilder albumBuilder = new AlbumBuilder();
		//Album album = albumBuilder.build(jsonObject.getJSONObject(ALBUM_KEY));
		//track.setAlbum(album);
		JSONObject albumJSON = jsonObj.getJSONObject(ALBUM);
		track.setAlbum(albumJSON.getString(NAME));	
		track.setAlbumId(albumJSON.getInt(ID));
		
		JSONObject genreJSON = jsonObj.getJSONObject(GENRE);
		track.setGenre(genreJSON.getString(NAME));
		track.setGenreId(genreJSON.getLong(ID));
		
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
