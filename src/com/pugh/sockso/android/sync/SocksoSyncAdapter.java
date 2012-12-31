package com.pugh.sockso.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.pugh.sockso.android.Preferences;
import com.pugh.sockso.android.account.SocksoAccountAuthenticator;
import com.pugh.sockso.android.data.MusicManager;

public class SocksoSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SocksoSyncAdapter.class.getSimpleName();

    private static final String SYNC_MARKER = "com.pugh.sockso.android.sync.MARKER";

    private AccountManager mAccountManager;
    private final Context mContext;

    public SocksoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() ran");

        // set this to a timestamp of the last sync so we can tell the server
        // we only want music items that have been add/changed since the last sync
        long lastSyncMarker = getServerSyncMarker(account);

        if (lastSyncMarker == 0) {
            Log.i(TAG, "Initial sync");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isNewAccount = prefs.getBoolean(Preferences.NEW_ACCOUNT, false);
        
        // Clear new account now that we've started syncing
        if ( isNewAccount ) {
            Editor editPrefs = prefs.edit();
            editPrefs.putBoolean(Preferences.NEW_ACCOUNT, false);
            editPrefs.commit();
            // TODO Wipe out the database if this is a new account!
        }
        
        try {
            long syncMarker = MusicManager.syncLibrary(mContext, lastSyncMarker);
            setServerSyncMarker(account, syncMarker);
        }
        catch (Exception e) {
            Log.e(TAG, "Exception syncing library", e);
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

        String markerString = mAccountManager.getUserData(account, SYNC_MARKER);
        Log.d(TAG, "getServerSyncMarker(): " + markerString);

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
        Log.d(TAG, "setServerSyncMarker(): " + marker);

        mAccountManager.setUserData(account, SYNC_MARKER, Long.toString(marker));
    }

}