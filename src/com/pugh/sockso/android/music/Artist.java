package com.pugh.sockso.android.music;

import java.util.List;

public class Artist {

	private String artist;
	private List<Album> albums;

	public Artist() {
	}

	public Artist(String artist) {
		this.artist = artist;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

}
