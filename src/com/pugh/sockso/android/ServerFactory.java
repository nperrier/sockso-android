package com.pugh.sockso.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServerFactory {

    private static final String TAG = ServerFactory.class.getSimpleName();

    private ServerFactory() {}

    public static SocksoServer getServer(final Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String host = prefs.getString("server", null);
        int    port = Integer.parseInt(prefs.getString("port", "4444"));

        return new SocksoServerImpl(host, port);
    }
}
