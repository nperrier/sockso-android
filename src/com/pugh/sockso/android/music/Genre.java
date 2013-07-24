package com.pugh.sockso.android.music;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Genre implements MusicItem {
    
    /*
     * "id": 123, 
     * "name": "Hip-Hop", 
     */
    private static final String ID     = "id";
    private static final String NAME   = "name";

    private long id = 0; // local id
    private long serverId = 0; // remote server id
    private String name;
    
    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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
    
    // Creates a single Genre object from a JSONObject
    public static Genre fromJSON(JSONObject jsonObj) throws JSONException {

        Genre genre = new Genre();
        
        genre.setServerId(jsonObj.getInt(ID));
        genre.setName(jsonObj.getString(NAME));
        
        return genre;
    }

    // Builds a list of Genres from a JSONArray
    public static List<Genre> fromJSONArray(JSONArray jsonArray) throws JSONException {

        List<Genre> genres = new ArrayList<Genre>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                genres.add(fromJSON(jsonArray.getJSONObject(i)));
            }
        }

        return genres;
    }
    
}
