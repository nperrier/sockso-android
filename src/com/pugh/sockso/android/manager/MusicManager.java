package com.pugh.sockso.android.manager;

import android.util.Log;

public class MusicManager {


    private static final String TAG = "MusicManager";

	
	
	// This should be only run to populate the database
	// the very first time
	// This is going to be inserting a LOT of data, especially for
	// users with large music libraries (read: me)
	// It should run in its own loader task,
	// and perform insertions in chunked batches
	// retrieving the data should be done in chunks too (using offset)
	public static void initLibrary(){
		
		Log.d(TAG, "initLibrary() ran");
		
		
	}
	
    /**
     * Take a list of updated contacts and apply those changes to the
     * contacts database. Typically this list of contacts would have been
     * returned from the server, and we want to apply those changes locally.
     *
     * @param context The context of Authenticator Activity
     * @param account The username for the account
     * @param rawContacts The list of contacts to update
     * @param lastSyncMarker The previous server sync-state
     * @return the server syncState that should be used in our next
     * sync request.
     */
	/*
	// small updates 
	// called when onPerformSync() runs
	public static synchronized long updateLibrary(Context context, String account,
	            List<RawContact> rawContacts, long lastSyncMarker) {

	        long currentSyncMarker = lastSyncMarker;
	        final ContentResolver resolver = context.getContentResolver();
	        final BatchOperation batchOperation = new BatchOperation(context, resolver);
	        final List<RawContact> newUsers = new ArrayList<RawContact>();

	        Log.d(TAG, "updateLibrary() ran");
	        
	        for (final RawContact rawContact : rawContacts) {
	        	
	            // The server returns a syncState (x) value with each contact record.
	            // The syncState is sequential, so higher values represent more recent
	            // changes than lower values. We keep track of the highest value we
	            // see, and consider that a "high water mark" for the changes we've
	            // received from the server.  That way, on our next sync, we can just
	            // ask for changes that have occurred since that most-recent change.
	            if (rawContact.getSyncState() > currentSyncMarker) {
	                currentSyncMarker = rawContact.getSyncState();
	            }

	            // If the server returned a clientId for this user, then it's likely
	            // that the user was added here, and was just pushed to the server
	            // for the first time. In that case, we need to update the main
	            // row for this contact so that the RawContacts.SOURCE_ID value
	            // contains the correct serverId.
	            final long rawContactId;
	            final boolean updateServerId;
	            if (rawContact.getRawContactId() > 0) {
	                rawContactId = rawContact.getRawContactId();
	                updateServerId = true;
	            } else {
	                long serverContactId = rawContact.getServerContactId();
	                rawContactId = lookupRawContact(resolver, serverContactId);
	                updateServerId = false;
	            }
	            if (rawContactId != 0) {
	                if (!rawContact.isDeleted()) {
	                    updateContact(context, resolver, rawContact, updateServerId,
	                            true, true, true, rawContactId, batchOperation);
	                } else {
	                    deleteContact(context, rawContactId, batchOperation);
	                }
	            } else {
	                Log.d(TAG, "In addContact");
	                if (!rawContact.isDeleted()) {
	                    newUsers.add(rawContact);
	                    addContact(context, account, rawContact, groupId, true, batchOperation);
	                }
	            }
	            // A sync adapter should batch operations on multiple contacts,
	            // because it will make a dramatic performance difference.
	            // (UI updates, etc)
	            if (batchOperation.size() >= 50) {
	                batchOperation.execute();
	            }
	        }
	        
	        batchOperation.execute();

	        return currentSyncMarker;
	    }
*/
		

	
}
