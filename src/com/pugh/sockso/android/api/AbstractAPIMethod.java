package com.pugh.sockso.android.api;

import android.net.Uri;

import com.pugh.sockso.android.Config;

public abstract class AbstractAPIMethod {
	
	private String mServer;
	private int mPort;
	
	boolean requireLogin = false;
	
	protected static final String API      = "api";
	protected static final String LIMIT    = "limit";
	protected static final String OFFSET   = "offset";
	
	protected static final int    DEFAULT_LIMIT = 100;
	protected static final int    NO_LIMIT      = -1;
	
	protected AbstractAPIMethod(String server, int port) {
		this.mServer = server;
		this.mPort   = port;
	}

	protected Uri getApiURI(){
		StringBuilder baseuri = new StringBuilder("http://").append(mServer)
				.append(":").append(mPort)
				.append("/").append(API);
		Uri uri = Uri.parse(baseuri.toString());
		
		return uri;
	}
	

	public abstract Uri.Builder getBaseURI();

}
