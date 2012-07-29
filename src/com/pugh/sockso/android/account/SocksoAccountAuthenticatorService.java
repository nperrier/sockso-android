package com.pugh.sockso.android.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SocksoAccountAuthenticatorService extends Service {

	private static final String TAG = "SocksoAccAuthService"; // must be less than 25 chars for logcat

	private SocksoAccountAuthenticator mAuthenticator;

	public SocksoAccountAuthenticatorService(){
		super();
	}
	
	@Override
	public void onCreate() {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "Authentication Service started.");
		}

		mAuthenticator = new SocksoAccountAuthenticator(this);
	}

	@Override
	public void onDestroy() {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "Authentication Service stopped.");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "getBinder() ... returning AccountAuthenticator binder");
		}

		return mAuthenticator.getIBinder();
	}
}
