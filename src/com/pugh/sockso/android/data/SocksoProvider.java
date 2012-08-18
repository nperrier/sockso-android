package com.pugh.sockso.android.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class SocksoProvider extends ContentProvider {

    private SocksoDB mDB;

    private static final String TAG = SocksoProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.pugh.sockso.android.data.SocksoProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final int ARTISTS_CODE = 100;
    public static final int ARTISTS_ID_CODE = 101;
    public static final int ARTISTS_ID_TRACKS_CODE = 102;

    public static final int ALBUMS_CODE = 200;
    public static final int ALBUMS_ID_CODE = 201;
    public static final int ALBUMS_ID_TRACKS_CODE = 202;

    public static final int TRACKS_CODE = 300;
    public static final int TRACKS_ID_CODE = 301;

    public static final int PLAYLISTS_CODE = 400;
    public static final int PLAYLISTS_ID_CODE = 401;
    public static final int PLAYLISTS_SITE_CODE = 402;
    public static final int PLAYLISTS_USER_CODE = 403;
    public static final int PLAYLISTS_USER_ID_CODE = 404;

    // MIME-types:
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // URI Matchers
    static {
        sURIMatcher.addURI(AUTHORITY, ArtistColumns.TABLE_NAME, ARTISTS_CODE);
        sURIMatcher.addURI(AUTHORITY, ArtistColumns.TABLE_NAME + "/#", ARTISTS_ID_CODE);
        sURIMatcher.addURI(AUTHORITY, ArtistColumns.TABLE_NAME + "/#/" + TrackColumns.TABLE_NAME,
                ARTISTS_ID_TRACKS_CODE);

        sURIMatcher.addURI(AUTHORITY, AlbumColumns.TABLE_NAME, ALBUMS_CODE);
        sURIMatcher.addURI(AUTHORITY, AlbumColumns.TABLE_NAME + "/#", ALBUMS_ID_CODE);
        sURIMatcher.addURI(AUTHORITY, AlbumColumns.TABLE_NAME + "/#/" + TrackColumns.TABLE_NAME, ALBUMS_ID_TRACKS_CODE);

        sURIMatcher.addURI(AUTHORITY, TrackColumns.TABLE_NAME, TRACKS_CODE);
        sURIMatcher.addURI(AUTHORITY, TrackColumns.TABLE_NAME + "/#", TRACKS_ID_CODE);

        sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME, PLAYLISTS_CODE);
        sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/#", PLAYLISTS_ID_CODE);
        sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/" + Playlist.SITE_PATH, PLAYLISTS_SITE_CODE);
        sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/" + Playlist.USER_PATH, PLAYLISTS_USER_CODE);
        sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/" + Playlist.USER_PATH + "/#", PLAYLISTS_USER_ID_CODE);
    }

    private static final Map<String, String> sAlbumProjectionMap = new HashMap<String, String>();
    private static final Map<String, String> sTrackProjectionMap = new HashMap<String, String>();
    // Projection Maps
    static {
        sAlbumProjectionMap.put(AlbumColumns.ARTIST_NAME, ArtistColumns.FULL_NAME + " AS " + AlbumColumns.ARTIST_NAME);
        sAlbumProjectionMap.put(AlbumColumns.SERVER_ID, AlbumColumns.FULL_SERVER_ID);
        sAlbumProjectionMap.put(AlbumColumns.NAME, AlbumColumns.FULL_NAME);
        sAlbumProjectionMap.put(AlbumColumns._ID, AlbumColumns.FULL_ID);

        sTrackProjectionMap.put(TrackColumns.ARTIST_NAME, ArtistColumns.FULL_NAME + " AS " + TrackColumns.ARTIST_NAME);
        sTrackProjectionMap.put(TrackColumns.SERVER_ID, TrackColumns.FULL_SERVER_ID);
        sTrackProjectionMap.put(TrackColumns.NAME, TrackColumns.FULL_NAME);
        sTrackProjectionMap.put(TrackColumns._ID, TrackColumns.FULL_ID);
    }

    public final static class ArtistColumns implements BaseColumns {

        private ArtistColumns() {}

        // Table:
        public static final String TABLE_NAME = "artists";

        // Columns:
        public static final String SERVER_ID = "server_id";
        public static final String NAME = "name";

        // Fully qualified columns (non-public)
        static final String FULL_ID = TABLE_NAME + "." + _ID;
        static final String FULL_SERVER_ID = TABLE_NAME + "." + SERVER_ID;
        static final String FULL_NAME = TABLE_NAME + "." + NAME;
    }

    public final static class AlbumColumns implements BaseColumns {

        private AlbumColumns() {}

        // Table:
        public static final String TABLE_NAME = "albums";

        // Columns:
        public static final String SERVER_ID = "server_id";
        public static final String NAME = "name";
        public static final String ARTIST_ID = "artist_id";
        public static final String YEAR = "year";

        // Mapped Columns:
        public static final String ARTIST_NAME = "artist_name";

        // Fully qualified columns (non-public)
        static final String FULL_SERVER_ID = TABLE_NAME + "." + SERVER_ID;
        static final String FULL_YEAR = TABLE_NAME + "." + YEAR;
        static final String FULL_ID = TABLE_NAME + "." + _ID;
        static final String FULL_NAME = TABLE_NAME + "." + NAME;
        static final String FULL_ARTIST_ID = TABLE_NAME + "." + ARTIST_ID;
    }

    public final static class TrackColumns implements BaseColumns {

        private TrackColumns() {}

        // Table:
        public static final String TABLE_NAME = "tracks";

        // Columns:
        public static final String SERVER_ID = "server_id";
        public static final String NAME = "name";
        public static final String ARTIST_ID = "artist_id";
        public static final String ALBUM_ID = "album_id";
        public static final String TRACK_NO = "track_no";

        // Mapped Columns:
        public static final String ARTIST_NAME = "artist_name";

        // Fully qualified columns (non-public)
        static final String FULL_SERVER_ID = TABLE_NAME + "." + SERVER_ID;
        static final String FULL_NAME = TABLE_NAME + "." + NAME;
        static final String FULL_ARTIST_ID = TABLE_NAME + "." + ARTIST_ID;
        static final String FULL_ALBUM_ID = TABLE_NAME + "." + ALBUM_ID;
        static final String FULL_TRACK_NO = TABLE_NAME + "." + TRACK_NO;
        static final String FULL_ID = TABLE_NAME + "." + _ID;
    }

    public final static class Playlist implements BaseColumns {

        private Playlist() {}

        // Table:
        public static final String TABLE_NAME = "playlists";

        private static final String SITE_PATH = "site";
        private static final String USER_PATH = "user";

        // Columns:
        public static final String SERVER_ID = "server_id";
        public static final String NAME = "name";
        public static final String USER_ID = "user_id";
    }

    @Override
    public boolean onCreate() {
        mDB = new SocksoDB(getContext());

        Log.i(TAG, "onCreate() ran");

        return true;
    }

    @Override
    public String getType(Uri uri) {

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {

        case ARTISTS_CODE:
        case ALBUMS_CODE:
        case TRACKS_CODE:
        case PLAYLISTS_CODE:
        case PLAYLISTS_SITE_CODE:
        case PLAYLISTS_USER_CODE:
            return CONTENT_TYPE;

        case ARTISTS_ID_CODE:
        case ARTISTS_ID_TRACKS_CODE:
        case ALBUMS_ID_TRACKS_CODE:
        case TRACKS_ID_CODE:
        case PLAYLISTS_ID_CODE:
        case PLAYLISTS_USER_ID_CODE:
            return CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert() ran");

        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        long inserted_id = 0;

        switch (uriType) {

        case ARTISTS_CODE:
            inserted_id = sqlDB.insert(ArtistColumns.TABLE_NAME, null, values);
            break;

        case ALBUMS_CODE:
            inserted_id = sqlDB.insert(AlbumColumns.TABLE_NAME, null, values);
            break;

        case TRACKS_CODE:
            inserted_id = sqlDB.insert(TrackColumns.TABLE_NAME, null, values);
            break;

        case PLAYLISTS_CODE:
            inserted_id = sqlDB.insert(Playlist.TABLE_NAME, null, values);
            break;

        default:
            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.withAppendedPath(uri, "/" + inserted_id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query() ran");

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
        case ARTISTS_CODE:
            Log.d(TAG, "In ARTISTS_CODE");
            queryBuilder.setTables(ArtistColumns.TABLE_NAME);
            break;
        case ARTISTS_ID_CODE:
            Log.d(TAG, "In ARTISTS_ID_CODE");
            queryBuilder.setTables(ArtistColumns.TABLE_NAME);
            queryBuilder.appendWhere(ArtistColumns._ID + "=" + uri.getLastPathSegment());
            break;
        case ALBUMS_CODE:
            Log.d(TAG, "In ALBUMS_CODE");

            queryBuilder.setProjectionMap(sAlbumProjectionMap);
            queryBuilder.setTables(AlbumColumns.TABLE_NAME + " JOIN " + ArtistColumns.TABLE_NAME + " ON "
                    + AlbumColumns.FULL_ARTIST_ID + "=" + ArtistColumns.FULL_SERVER_ID);
            break;
        case ALBUMS_ID_CODE:
            Log.d(TAG, "In ALBUMS_ID_CODE");
            /*
             * SELECT <projection>
             * FROM artists JOIN albums
             * ON artists.server_id=albums.artist_id
             * WHERE albums._id=1
             */

            queryBuilder.setProjectionMap(sAlbumProjectionMap);
            queryBuilder.setTables(AlbumColumns.TABLE_NAME + " JOIN " + ArtistColumns.TABLE_NAME + " ON "
                    + AlbumColumns.FULL_ARTIST_ID + "=" + ArtistColumns.FULL_SERVER_ID);
            queryBuilder.appendWhere(AlbumColumns.FULL_ID + "=" + uri.getLastPathSegment());

            break;
        case TRACKS_CODE:
            Log.d(TAG, "In TRACKS_CODE");

            queryBuilder.setProjectionMap(sTrackProjectionMap);
            queryBuilder.setTables(TrackColumns.TABLE_NAME + " JOIN " + ArtistColumns.TABLE_NAME + " ON "
                    + TrackColumns.FULL_ARTIST_ID + "=" + ArtistColumns.FULL_SERVER_ID);

            break;
        case TRACKS_ID_CODE:
            Log.d(TAG, "In TRACKS_ID_CODE");
            queryBuilder.setTables(TrackColumns.TABLE_NAME);
            queryBuilder.appendWhere(TrackColumns._ID + "=" + uri.getLastPathSegment());
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(), projection, selection, selectionArgs, null, null,
                sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        int rowsAffected = 0;

        switch (uriType) {

        case ARTISTS_CODE:
            rowsAffected = sqlDB.delete(ArtistColumns.TABLE_NAME, selection, selectionArgs);
            break;

        case ARTISTS_ID_CODE:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsAffected = sqlDB.delete(ArtistColumns.TABLE_NAME, ArtistColumns._ID + "=" + id, null);
            }
            else {
                rowsAffected = sqlDB.delete(ArtistColumns.TABLE_NAME, selection + " and " + ArtistColumns._ID + "="
                        + id, selectionArgs);
            }
            break;

        default:
            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

}
