package com.pugh.sockso.android.music;

import java.util.ArrayList;
import java.util.List;

public class Album {

	private int id = 0;
	private String name;
	private String image; // link to the cover of the album?

	private String artist;
	//private Artist artist;
	private List<Track> tracks = new ArrayList<Track>();

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

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
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
