package com.pugh.sockso.android.api;

public class ServerInfo {

	/*
	 * {
	 * "title":"Sockso",
	 * "tagline":"Personal Music Server",
	 * "version":"1.5.3",
	 * "requiresLogin":"0"
	 * }
	 */
	
	private String mTitle = "Sockso"; //default
	private String mTagline = "Personal Music Server";
	private String mVersion;  // server version (not client)
	
	public ServerInfo(String version){
		this.setmVersion(version);
	}
	
	public ServerInfo(String version, String title, String tagline){
		this.setmVersion(version);
		this.setmTitle(title);
		this.setmTagline(tagline);
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmTagline() {
		return mTagline;
	}

	public void setmTagline(String mTagline) {
		this.mTagline = mTagline;
	}

	public String getmVersion() {
		return mVersion;
	}

	public void setmVersion(String mVersion) {
		this.mVersion = mVersion;
	}
	
}
