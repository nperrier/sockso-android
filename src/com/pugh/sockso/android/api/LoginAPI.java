package com.pugh.sockso.android.api;

import android.net.Uri;

import com.pugh.sockso.android.Config;

public class LoginAPI extends AbstractAPIMethod {

	private static final String LOGIN = "/user/login";
		
	public LoginAPI(String server, int port) {
		super(server, port);
	}

	public Uri.Builder getBaseURI(){
		Uri.Builder b = this.getApiURI().buildUpon();
		b.path(LOGIN);
		return b;
	}
	
	public Uri getLoginURI(){
		return this.getBaseURI().build();
	}
}
