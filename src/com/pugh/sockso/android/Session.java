package com.pugh.sockso.android;

// Represents a Sockso session
public class Session {
	
	private String username;
	private String sessionKey;
	
	public Session(String username, String sessionKey){
		this.username = username;
		this.sessionKey = sessionKey;
	}

	public String getSessionKey(){
		return sessionKey;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setSessionKey(String sessionKey){
		this.sessionKey = sessionKey;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
}
