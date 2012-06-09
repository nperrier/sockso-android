package com.pugh.sockso.android.music;

public class Track {
	
	private String title;
	private String artist;
	private String album;
	
	public Track(){}
	
	public Track(String title, String artist, String album){
		this.title = title;
		this.artist = artist;
		this.album = album;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	
	
	

}
