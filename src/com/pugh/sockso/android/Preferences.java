package com.pugh.sockso.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

	private static String PREFS_FILE = "socksoprefs";

	private final SharedPreferences prefs;

	public Preferences(Context context) {
		this.prefs = context.getSharedPreferences(PREFS_FILE,
				Context.MODE_PRIVATE);
	}

	public void saveConfig(SocksoConfig config) {
		Editor editor = prefs.edit();
		editor.putString("server", config.getServer());
		editor.putInt("port", config.getPort());
		editor.putString("user", config.getUser());
		editor.putString("password", config.getPassword());
		editor.commit();
	}

	public SocksoConfig getConfig() {
		SocksoConfig config = new SocksoConfig();
		
		// TODO maybe use a demo server for first time users
		String server = prefs.getString("server", null);
		String user = prefs.getString("user", null);
		int port = prefs.getInt("port", SocksoConfig.DEFAULT_PORT);
		String password = prefs.getString("password", null);
		config = new SocksoConfig(user, password, server, port);

		return config;
	}

	public boolean isServerConfigured() {
		return prefs.contains("server");
	}
	
	public boolean isLoginConfigured() {
		return prefs.contains("user"); // password needed too?
	}
}
