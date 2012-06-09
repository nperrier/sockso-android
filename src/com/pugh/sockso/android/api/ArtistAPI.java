package com.pugh.sockso.android.api;

import android.net.Uri;

public class ArtistAPI extends AbstractAPIMethod {

	protected static final String ARTISTS = "artists";
	protected static final String TRACKS  = "tracks";
	
	public ArtistAPI(String server, int port) {
		super(server, port);
	}
	
	// /api/artists/$ID - ArtistAPI $ID
	public String getArtist(String id){
		return getBaseURI().path(id).build().toString();
	}
	
	// /api/artists - Lists artists (paged)
	public String getArtists(int limit, int offset){
		Uri.Builder b = getBaseURI();
		
		if(limit != DEFAULT_LIMIT){
			b.appendQueryParameter(LIMIT.toString(), Integer.toString(limit));
		}
		if(offset != 0){
			b.appendQueryParameter(OFFSET.toString(), Integer.toString(offset));
		}
		
		return b.build().toString();
	}
	
	public String getArtists(){
		return getArtists(NO_LIMIT, 0);
	}
	
	// /api/artists/$ID/tracks - TrackAPI for artist $ID
	public String getTracks(String id){	
		return getBaseURI().path(id).path(TRACKS).build().toString();
	}
	
	public Uri.Builder getBaseURI(){
		Uri.Builder b = this.getApiURI().buildUpon();
		b.path(ARTISTS);
		return b;
	}

	
}
