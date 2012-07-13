package com.pugh.sockso.android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class SocksoSyncService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static SocksoSyncAdapter sSyncAdapter = null;

	public SocksoSyncService(){
		super();
	}
	
	@Override
	public void onCreate() {
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SocksoSyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}
}
