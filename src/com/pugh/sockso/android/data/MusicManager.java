package com.pugh.sockso.android.data;

import java.util.List;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.IMusicItem;
import com.pugh.sockso.android.music.Track;

public class MusicManager {

	private static final String TAG = MusicManager.class.getSimpleName();
	
	private static final int BATCH_MAX = 50;  // max size of inserts to database in one operation

	/** TODO This should be only run to populate the database the very first time.
	 * 
	 * This is going to be inserting a LOT of data, especially for users 
	 * with large music libraries (read: me).
	 *
	 * It should run in its own loader task, and perform insertions in chunked batches.
	 * 
	 * Retrieving the data should be done in chunks too (using offset).
	 */
	public static void syncLibrary(final Context context, final Map<String, List<? extends IMusicItem>> musicItems) {
		Log.d(TAG, "initLibrary() ran");

		final ContentResolver resolver = context.getContentResolver();

		List<Artist> artists = (List<Artist>) musicItems.get("artists");
		syncArtists(artists, context, resolver);

		List<Album> albums = (List<Album>) musicItems.get("albums");
		syncAlbums(albums, context, resolver);

		List<Track> tracks = (List<Track>) musicItems.get("tracks");
		syncTracks(tracks, context, resolver);

	}

	// TODO
	private static void syncArtists(List<Artist> artists, Context context, ContentResolver resolver) {
		Log.d(TAG, "syncArtists() ran");

		final BatchOperation batchOperation = new BatchOperation(context, resolver);

		for (final Artist artist : artists) {
			// add an insert operation for each artist
			addArtist(artist, context, resolver, batchOperation);

			// A sync adapter should batch operations on multiple items,
			// because it will make a dramatic performance difference.
			// (UI updates, etc)
			if (batchOperation.size() >= BATCH_MAX) {
				Log.d(TAG, "syncArtists(): " + BATCH_MAX + " batched. Executing current batch...");
				batchOperation.execute();
			}

		}

		batchOperation.execute();
	}

	private static void syncAlbums(List<Album> albums, Context context, ContentResolver resolver) {
		Log.d(TAG, "syncAlbums() ran");

		final BatchOperation batchOperation = new BatchOperation(context, resolver);

		for (final Album album : albums) {
			// add an insert operation for each artist
			addAlbum(album, context, resolver, batchOperation);

			// A sync adapter should batch operations on multiple items,
			// because it will make a dramatic performance difference.
			// (UI updates, etc)
			if (batchOperation.size() >= BATCH_MAX) {
				Log.d(TAG, "syncAlbums(): " + BATCH_MAX + " batched. Executing current batch...");
				batchOperation.execute();
			}

		}

		batchOperation.execute();
	}

	private static void syncTracks(List<Track> tracks, Context context, ContentResolver resolver) {
		Log.d(TAG, "syncTracks() ran");

		final BatchOperation batchOperation = new BatchOperation(context, resolver);

		for (final Track track : tracks) {
			// add an insert operation for each artist
			addTrack(track, context, resolver, batchOperation);

			// A sync adapter should batch operations on multiple items,
			// because it will make a dramatic performance difference.
			// (UI updates, etc)
			if (batchOperation.size() >= BATCH_MAX) {
				Log.d(TAG, "syncTracks(): " + BATCH_MAX + " batched. Executing current batch...");
				batchOperation.execute();
			}
		}

		batchOperation.execute();
	}

	// TODO
	public static void addArtist(Artist artist, Context context, ContentResolver resolver, BatchOperation batchOperation) {
		Log.d(TAG, "addArtist() ran");

		ContentValues contentValues = new ContentValues();

		contentValues.put(SocksoProvider.Artist.Columns.SERVER_ID, artist.getServerId());
		contentValues.put(SocksoProvider.Artist.Columns.NAME, artist.getName());

		Uri insertUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + SocksoProvider.Artist.TABLE_NAME);

		ContentProviderOperation cpo = ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build();
		batchOperation.add(cpo);
	}

	private static void addAlbum(Album album, Context context, ContentResolver resolver, BatchOperation batchOperation) {
		Log.d(TAG, "addAlbum() ran");

		ContentValues contentValues = new ContentValues();

		contentValues.put(SocksoProvider.Album.Columns.SERVER_ID, album.getServerId());
		contentValues.put(SocksoProvider.Album.Columns.NAME, album.getName());
		contentValues.put(SocksoProvider.Album.Columns.ARTIST_ID, album.getArtistId());
		// TODO SocksoProvider.Album.Columns.YEAR;

		Uri insertUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + SocksoProvider.Album.TABLE_NAME);

		ContentProviderOperation cpo = ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build();
		batchOperation.add(cpo);
	}

	private static void addTrack(Track track, Context context, ContentResolver resolver, BatchOperation batchOperation) {
		Log.d(TAG, "addTrack() ran");

		ContentValues contentValues = new ContentValues();

		contentValues.put(SocksoProvider.Track.Columns.SERVER_ID, track.getServerId());
		contentValues.put(SocksoProvider.Track.Columns.NAME, track.getName());
		contentValues.put(SocksoProvider.Track.Columns.ARTIST_ID, track.getArtistId());
		contentValues.put(SocksoProvider.Track.Columns.ALBUM_ID, track.getAlbumId());
		// TODO contentValues.put(SocksoProvider.Track.Columns.TRACK_NO,
		// track.getTrackNumber());

		Uri insertUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + SocksoProvider.Track.TABLE_NAME);

		ContentProviderOperation cpo = ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build();
		batchOperation.add(cpo);
	}

}
