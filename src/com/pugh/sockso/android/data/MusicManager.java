package com.pugh.sockso.android.data;

import java.util.List;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.ArtistColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;
import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.MusicItem;
import com.pugh.sockso.android.music.Track;

public class MusicManager {

    private static final String TAG = MusicManager.class.getSimpleName();
    private static final int BATCH_MAX = 50;

    // TODO This should be only run to populate the database
    // the very first time
    // This is going to be inserting a LOT of data, especially for
    // users with large music libraries (read: me)
    // It should run in its own loader task,
    // and perform insertions in chunked batches
    // retrieving the data should be done in chunks too (using offset)
    public static void syncLibrary(final Context context, final Map<String, List<? extends MusicItem>> musicItems) {
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

        contentValues.put(ArtistColumns.SERVER_ID, artist.getServerId());
        contentValues.put(ArtistColumns.NAME, artist.getName());

        Uri insertUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + ArtistColumns.TABLE_NAME);

        ContentProviderOperation cpo = ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build();
        batchOperation.add(cpo);
    }

    private static void addAlbum(Album album, Context context, ContentResolver resolver, BatchOperation batchOperation) {
        Log.d(TAG, "addAlbum() ran");

        ContentValues contentValues = new ContentValues();

        contentValues.put(AlbumColumns.SERVER_ID, album.getServerId());
        contentValues.put(AlbumColumns.NAME, album.getName());
        contentValues.put(AlbumColumns.ARTIST_ID, album.getArtistId());
        // TODO .AlbumColumns.YEAR;

        Uri insertUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + AlbumColumns.TABLE_NAME);

        ContentProviderOperation cpo = ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build();
        batchOperation.add(cpo);
    }

    private static void addTrack(Track track, Context context, ContentResolver resolver, BatchOperation batchOperation) {
        Log.d(TAG, "addTrack() ran");

        ContentValues contentValues = new ContentValues();

        contentValues.put(TrackColumns.SERVER_ID, track.getServerId());
        contentValues.put(TrackColumns.NAME, track.getName());
        contentValues.put(TrackColumns.TRACK_NO, track.getTrackNumber());
        contentValues.put(TrackColumns.ARTIST_ID, track.getArtistId());
        contentValues.put(TrackColumns.ALBUM_ID, track.getAlbumId());

        Uri insertUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + TrackColumns.TABLE_NAME);

        ContentProviderOperation cpo = ContentProviderOperation.newInsert(insertUri).withValues(contentValues).build();
        batchOperation.add(cpo);
    }

}
