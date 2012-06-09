package com.pugh.sockso.android.api;

import android.net.Uri;

public class AlbumAPI extends AbstractAPIMethod {

	protected static final String ALBUMS = "albums";
	protected static final String TRACKS = "tracks";
	
	public AlbumAPI(String server, int port) {
		super(server, port);
	}
	
	// /api/albums/$ID - AlbumAPI $ID
	public String getAlbum(String id){
		return getBaseURI().path(id).build().toString();
	}
	
	// /api/albums - Lists albums (paged)
	public String getAlbums(int limit, int offset){
		Uri.Builder b = getBaseURI();
		
		if(limit != DEFAULT_LIMIT){
			b.appendQueryParameter(LIMIT.toString(), Integer.toString(limit));
		}
		if(offset != 0){
			b.appendQueryParameter(OFFSET.toString(), Integer.toString(offset));
		}
		
		return b.build().toString();
	}
	
	public String getAlbums(){
		return getAlbums(NO_LIMIT, 0);
	}
	
	// /api/albums/$ID/tracks - TrackAPI for album $ID
	public String getTracks(String id){
		return getBaseURI().path(id).path(TRACKS).build().toString();
	}
	
	public Uri.Builder getBaseURI(){
		Uri.Builder b = this.getApiURI().buildUpon();
		b.path(ALBUMS);
		return b;
	}

}
