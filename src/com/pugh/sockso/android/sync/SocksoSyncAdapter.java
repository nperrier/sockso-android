package com.pugh.sockso.android.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.pugh.sockso.android.SocksoConfig;
import com.pugh.sockso.android.SocksoServer;
import com.pugh.sockso.android.api.ISocksoAPI;
import com.pugh.sockso.android.api.SocksoAPIImpl;
import com.pugh.sockso.android.data.MusicManager;
import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.IMusicItem;
import com.pugh.sockso.android.music.Track;

public class SocksoSyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG = SocksoSyncAdapter.class.getName();

	private static final String PARAM_AUTHTOKEN_TYPE = "com.pugh.sockso.android.AUTH_TOKEN";
	private static final String SYNC_MARKER_KEY = "com.pugh.sockso.android.sync.MARKER";

	private AccountManager mAccountManager;
	private ContentResolver mContentResolver;
    private final Context mContext;

	public SocksoSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		
		mContext = context;
		mAccountManager = AccountManager.get(context);
		mContentResolver = context.getContentResolver();
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, 
			ContentProviderClient provider,	SyncResult syncResult) {
		
		Log.i(TAG, "onPerformSync() ran");
		
		Log.d(TAG, "account: " + account.name);
		Log.d(TAG, "extras: " + extras.size());
		for(String key : extras.keySet())
			Log.d(TAG, "key: " + key + ", value: " + extras.get(key));
		Log.d(TAG, "authority: " + authority);
		Log.d(TAG, "provider: " + provider.getClass());
		
		String server = mAccountManager.getUserData(account, "server");
		String port   = mAccountManager.getUserData(account, "port");

		Log.d(TAG, "server: " + server);
		Log.d(TAG, "port:   " + port);
		
		// Consider putting all this stuff in the SocksoApp global class
		SocksoConfig config = new SocksoConfig(server, Integer.parseInt(port));
		SocksoServer socksoServer = new SocksoServer(config);
		
		ISocksoAPI socksoAPI = new SocksoAPIImpl(socksoServer);
		
		if(syncResult != null)
			Log.d(TAG, "syncResult: " + syncResult.toString());	
		
		try {
			// see if we already have a sync-state attached to this account.
			// By handing this value to the server, we can just get the contacts
			// that have been updated on the server-side since our last sync-up
			long lastSyncMarker = getServerSyncMarker(account);

			// Use the account manager to request the AuthToken we'll need to
			// talk to our sample server.
			// If we don't have an AuthToken yet, this could involve a
			// round-trip to the server to request and AuthToken.
			
			// final String authtoken = mAccountManager.blockingGetAuthToken(account, PARAM_AUTHTOKEN_TYPE, NOTIFY_AUTH_FAILURE);

			// TODO 0 means we've never synced
			if (lastSyncMarker != 0) {
				Log.i(TAG, "onPerformSync(): We've synced before, so let's just update what's changed");
				// TODO get server data that has changed (pass sync marker)

				// Update the local contacts database with the changes.
				// updateContacts() returns a syncState value that indicates the
				// high-water-mark for the changes we received.
				
				// long newSyncState = ContactManager.updateContacts(mContext, account.name, updatedContacts, groupId, lastSyncMarker);

				// Save off the new sync marker. On our next sync, we only want
				// to receive things that have changed since this sync...
				long newSyncState = 1; // TODO Just to tell it we've synced before
				setServerSyncMarker(account, newSyncState);
			}
			else {
				Log.i(TAG, "onPerformSync(): First sync: Let's init that database");
				
				// Grab data from server
				// TODO This is a rather naive approach.  At some point later,
				// this should be moved into a class that handles batched data retrieval 
				// and creates batches for the contentProvider in a more incremental manner
				List<Artist> artists = socksoAPI.getArtists();
				List<Album>  albums  = socksoAPI.getAlbums();
				List<Track>  tracks  = socksoAPI.getTracks();
				
				Log.d(TAG, "artists.size(): " + artists.size());
				Log.d(TAG, "albums.size():  " + albums.size());
				Log.d(TAG, "tracks.size():  " + tracks.size());
				
				Map<String, List<? extends IMusicItem>> musicItems = new HashMap<String, List<? extends IMusicItem>>();
				musicItems.put("artists", artists);
				musicItems.put("albums", albums);
				musicItems.put("tracks", tracks);
				/*
				 * Account account, 
				 * Bundle extras, 
				 * String authority, 
				 * ContentProviderClient provider,	
				 * SyncResult syncResult
				 */
				
				MusicManager.syncLibrary(mContext, musicItems);
				
			}
			
		} catch (Exception e) {
			Log.e(TAG, "onPerformSync() caught Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * This helper function fetches the last known high-water-mark we received
	 * from the server - or 0 if we've never synced.
	 * 
	 * @param account The account we're syncing
	 * @return The change high-water-mark
	 */
	private long getServerSyncMarker(Account account) {
		String markerString = mAccountManager.getUserData(account, SYNC_MARKER_KEY);
		if (!TextUtils.isEmpty(markerString)) {
			return Long.parseLong(markerString);
		}
		return 0;
	}

	/**
	 * Save off the high-water-mark we receive back from the server.
	 * 
	 * @param account The account we're syncing
	 * @param marker The high-water-mark we want to save.
	 */
	private void setServerSyncMarker(Account account, long marker) {
		mAccountManager.setUserData(account, SYNC_MARKER_KEY, Long.toString(marker));
	}

}