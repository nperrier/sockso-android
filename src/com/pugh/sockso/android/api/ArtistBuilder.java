package com.pugh.sockso.android.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pugh.sockso.android.music.Artist;

public class ArtistBuilder extends JSONBuilder<Artist> {

	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";

	// builds a single artist
	@Override
	public Artist build(JSONObject jsonObject) throws JSONException {

		Artist artist = new Artist();
		
		artist.setId(jsonObject.getInt(ID_KEY));
		artist.setName(jsonObject.getString(NAME_KEY));

		return artist;
	}

	// builds an array of Artists
	public List<Artist> buildList(JSONArray jsonArray) throws JSONException {

		List<Artist> artists = new ArrayList<Artist>();
		
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				artists.add(build(jsonArray.getJSONObject(i)));
			}
		}

		return artists;
	}
}