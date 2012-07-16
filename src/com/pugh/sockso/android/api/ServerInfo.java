package com.pugh.sockso.android.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerInfo {
	
	public static final String VERSION = "version";
	public static final String TITLE   = "title";
	public static final String TAGLINE = "tagline";
	
	private String title = "Sockso"; //default
	private String tagline = "Personal Music Server";
	private String version;  // server version (not client)
	
	public ServerInfo(String version){
		this.setVersion(version);
	}
	
	public ServerInfo(String version, String title, String tagline){
		this.setVersion(version);
		this.setTitle(title);
		this.setTagline(tagline);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	/* {
	 * "title":   "Sockso",
	 * "tagline": "Personal Music Server",
	 * "version": "1.5.3",
	 * "requiresLogin": "0"
	 * }
	 */
	public static ServerInfo fromJSON(JSONObject jsonObj) throws JSONException {

		String version = jsonObj.getString(VERSION);
		String title   = jsonObj.getString(TITLE);
		String tagline = jsonObj.getString(TAGLINE);
		
		return new ServerInfo(version, title, tagline);
	}
	
}
