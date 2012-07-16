package com.pugh.sockso.android.data;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class handles execution of batch mOperations on Contacts provider.
 */
final public class BatchOperation {

	private final String TAG = BatchOperation.class.getSimpleName();

	private final ContentResolver mResolver;

	// List for storing the batch mOperations
	private final ArrayList<ContentProviderOperation> mOperations;

	public BatchOperation(Context context, ContentResolver resolver) {
		mResolver = resolver;
		mOperations = new ArrayList<ContentProviderOperation>();
	}

	public int size() {
		return mOperations.size();
	}

	public void add(ContentProviderOperation cpo) {
		mOperations.add(cpo);
	}

	public Uri execute() {
		Log.d(TAG, "execute() batch");
		
		Uri result = null;

		if (mOperations.size() == 0) {
			return result;
		}
		
		// Apply the mOperations to the content provider
		try {
			
			ContentProviderResult[] results = mResolver.applyBatch(SocksoProvider.AUTHORITY, mOperations);
			
			if((results != null) && (results.length > 0)){
				result = results[0].uri;
			}
			
		} catch (final OperationApplicationException e1) {
			Log.e(TAG, "storing contact data failed", e1);
		} catch (final RemoteException e2) {
			Log.e(TAG, "storing contact data failed", e2);
		}
		
		mOperations.clear();
		
		return result;
	}
}
