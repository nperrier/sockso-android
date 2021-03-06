package com.pugh.sockso.android.music;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Artist implements MusicItem {

	private static final String ID   = "id";
	private static final String NAME = "name";
    public static final String COVER_PREFIX = "ar";

	private long id = 0; // local id
	private long serverId = 0; // remote server id
	private String name;
	
	public Artist() {
	}

	public Artist(String name) {
		this.name = name;
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
	
	@Override
	public String toString(){
		return this.name;
	}

	// Creates a single Artist object from a JSONObject
	public static Artist fromJSON(JSONObject jsonObj) throws JSONException {

		Artist artist = new Artist();
		
		artist.setServerId(jsonObj.getInt(ID));
		artist.setName(jsonObj.getString(NAME));

		return artist;
	}

	// Builds a list of Artist from a JSONArray
	public static List<Artist> fromJSONArray(JSONArray jsonArray) throws JSONException {

		List<Artist> artists = new ArrayList<Artist>();
		
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				artists.add(fromJSON(jsonArray.getJSONObject(i)));
			}
		}

		return artists;
	}
}
