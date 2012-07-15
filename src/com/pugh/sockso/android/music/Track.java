package com.pugh.sockso.android.music;

public class Track {

	private int id;
	private String name;
	private int trackNumber;
	private int duration;
	private String image; // link to the cover of the album?

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

	@Override
	public String toString(){
		return this.name;
	}
}
