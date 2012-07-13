package com.pugh.sockso.android.sync;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;
import android.util.Log;

public class SocksoSyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG = SocksoSyncAdapter.class.getName();
	private static final String PARAM_AUTHTOKEN_TYPE = "com.pugh.sockso.android.AUTH_TOKEN";
	private static final String SYNC_MARKER_KEY = "sync-marker";

	private AccountManager mAccountManager;
	private ContentResolver mContentResolver;

	public SocksoSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		mAccountManager = AccountManager.get(context);
		mContentResolver = context.getContentResolver();
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
			SyncResult syncResult) {

		Log.i(TAG, "onPerformSync() ran");

		try {
			// see if we already have a sync-state attached to this account. 
			// By handing this value to the server, we can just get the contacts that have
			// been updated on the server-side since our last sync-up
			long lastSyncMarker = getServerSyncMarker(account);

			// Use the account manager to request the AuthToken we'll need to talk to our sample server. 
			// If we don't have an AuthToken yet, this could involve a round-trip to the server to request and AuthToken.
			final String authtoken = mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, NOTIFY_AUTH_FAILURE);

			// TODO get server data that has changed (pass sync marker)
			
			// Update the local contacts database with the changes.
			// updateContacts() returns a syncState value that indicates the high-water-mark for the changes we received.
			Log.d(TAG, "Calling contactManager's sync contacts");
			long newSyncState = ContactManager.updateContacts(mContext, account.name, updatedContacts, groupId,
					lastSyncMarker);

			// This is a demo of how you can update IM-style status messages
			// for contacts on the client. This probably won't apply to
			// 2-way contact sync providers - it's more likely that one-way
			// sync providers (IM clients, social networking apps, etc) would
			// use this feature.
			ContactManager.updateStatusMessages(mContext, updatedContacts);

			// Save off the new sync marker. On our next sync, we only want to receive 
			// things that have changed since this sync...
			setServerSyncMarker(account, newSyncState);

			
			
			
		} catch (final AuthenticatorException e) {
			Log.e(TAG, "AuthenticatorException", e);
			syncResult.stats.numParseExceptions++;
		} catch (final OperationCanceledException e) {
			Log.e(TAG, "OperationCanceledExcetpion", e);
		} catch (final IOException e) {
			Log.e(TAG, "IOException", e);
			syncResult.stats.numIoExceptions++;
		} catch (final AuthenticationException e) {
			Log.e(TAG, "AuthenticationException", e);
			syncResult.stats.numAuthExceptions++;
		} catch (final ParseException e) {
			Log.e(TAG, "ParseException", e);
			syncResult.stats.numParseExceptions++;
		} catch (final JSONException e) {
			Log.e(TAG, "JSONException", e);
			syncResult.stats.numParseExceptions++;
		}
	}
	
	/**
     * This helper function fetches the last known high-water-mark
     * we received from the server - or 0 if we've never synced.
     * @param account the account we're syncing
     * @return the change high-water-mark
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
     * @param account The account we're syncing
     * @param marker The high-water-mark we want to save.
     */
    private void setServerSyncMarker(Account account, long marker) {
        mAccountManager.setUserData(account, SYNC_MARKER_KEY, Long.toString(marker));
    }
    
}