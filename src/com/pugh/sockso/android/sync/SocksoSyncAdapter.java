package com.pugh.sockso.android.sync;

import java.io.IOException;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;

public class SocksoSyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String PARAM_ACCOUNT_TYPE = "com.pugh.sockso.android.account";
	private static final String PARAM_AUTHTOKEN_TYPE = "com.pugh.sockso.android.AUTH_TOKEN";
	private static final String TAG = SocksoSyncAdapter.class.getName();

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

		String authtoken = null;
		try {
			authtoken = mAccountManager.blockingGetAuthToken(account, PARAM_AUTHTOKEN_TYPE, true);

			// Dummy sample. Do whatever you want in this method.
			// List data = fetchData(authtoken);

			// syncRemoteDeleted(data);
			// syncFromServerToLocalStorage(data);
			// syncDirtyToServer(authtoken, getDirtyList(mContentResolver));

		} catch (Exception e) {
			Log.v(TAG, "Failed in onPerformSync(): " + e.getMessage());
		}
	}
}