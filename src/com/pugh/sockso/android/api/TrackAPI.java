package com.pugh.sockso.android.api;

import android.net.Uri;

public class TrackAPI extends AbstractAPIMethod {

		protected static final String TRACKS  = "tracks";
		
		public TrackAPI(String server, int port) {
			super(server, port);
		}
		
		// /api/tracks/$ID - TrackAPI $ID
		public String getTrack(String id){
			return getBaseURI().path(id).build().toString();
		}
		
		// /api/tracks - Lists tracks (paged)
		public String getTracks(int limit, int offset){
			Uri.Builder b = getBaseURI();
			
			if(limit != DEFAULT_LIMIT){
				b.appendQueryParameter(LIMIT.toString(), Integer.toString(limit));
			}
			if(offset != 0){
				b.appendQueryParameter(OFFSET.toString(), Integer.toString(offset));
			}
			return b.build().toString();
		}
		
		public String getTracks(){
			return getTracks(NO_LIMIT, 0);
		}		
		
		public Uri.Builder getBaseURI(){
			Uri.Builder b = this.getApiURI().buildUpon();
			b.path(TRACKS);
			return b;
		}

		
	}
