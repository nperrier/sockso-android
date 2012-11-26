package com.pugh.sockso.android.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * This class handles execution of batch mOperations on Contacts provider.
 */
final public class BatchOperation {

	private final String TAG = BatchOperation.class.getSimpleName();

	private final ContentResolver mResolver;
	private final Uri mUri;

	// List for storing the batch mOperations
	private final List<ContentValues> mOperations;

	public BatchOperation(Uri uri, Context context, ContentResolver resolver) {
	    mUri = uri;
		mResolver = resolver;
		mOperations = new ArrayList<ContentValues>();
	}

	public int size() {
		return mOperations.size();
	}

	public void add(ContentValues cv) {
		mOperations.add(cv);
	}

	public int execute() {
		Log.d(TAG, "execute() batch");

        if (mOperations.size() == 0) {
            return 0;
        }

		// Apply the mOperations to the content provider
		//ContentProviderResult[] results = mResolver.applyBatch(SocksoProvider.AUTHORITY, mOperations);
		int rows = mResolver.bulkInsert(mUri, mOperations.toArray(new ContentValues[mOperations.size()]) );
		
		mOperations.clear();
		
		return rows;
	}
}
