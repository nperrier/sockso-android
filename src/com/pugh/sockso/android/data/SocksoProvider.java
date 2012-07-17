package com.pugh.sockso.android.data;

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
	public static final Uri    CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final int ARTISTS_CODE    	   = 100;
	public static final int ARTISTS_ID_CODE 	   = 101;
	public static final int ARTISTS_ID_TRACKS_CODE = 102;
	
	public static final int ALBUMS_CODE     	   = 200;
	public static final int ALBUMS_ID_CODE  	   = 201;
	public static final int ALBUMS_ID_TRACKS_CODE  = 202;
	
	public static final int TRACKS_CODE            = 300;
	public static final int TRACKS_ID_CODE         = 301;
	
	public static final int PLAYLISTS_CODE  	   = 400;
	public static final int PLAYLISTS_ID_CODE  	   = 401;
	public static final int PLAYLISTS_SITE_CODE	   = 402;
	public static final int PLAYLISTS_USER_CODE	   = 403;
	public static final int PLAYLISTS_USER_ID_CODE = 404;
	
	// MIME-types:
	public static final String CONTENT_TYPE      = ContentResolver.CURSOR_DIR_BASE_TYPE  + "/" + AUTHORITY;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
	    sURIMatcher.addURI(AUTHORITY, Artist.TABLE_NAME, ARTISTS_CODE);
	    sURIMatcher.addURI(AUTHORITY, Artist.TABLE_NAME + "/#", ARTISTS_ID_CODE);
	    sURIMatcher.addURI(AUTHORITY, Artist.TABLE_NAME + "/#/" + Track.TABLE_NAME, ARTISTS_ID_TRACKS_CODE);
	    
	    sURIMatcher.addURI(AUTHORITY, Album.TABLE_NAME, ALBUMS_CODE);
	    sURIMatcher.addURI(AUTHORITY, Album.TABLE_NAME + "/#", ALBUMS_ID_CODE);
	    sURIMatcher.addURI(AUTHORITY, Album.TABLE_NAME + "/#/" + Track.TABLE_NAME, ALBUMS_ID_TRACKS_CODE);
	    
	    sURIMatcher.addURI(AUTHORITY, Track.TABLE_NAME, TRACKS_CODE);
	    sURIMatcher.addURI(AUTHORITY, Track.TABLE_NAME + "/#", TRACKS_ID_CODE);	  
	   
	    sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME, PLAYLISTS_CODE);
	    sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/#", PLAYLISTS_ID_CODE);
	    sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/" + Playlist.SITE_PATH, PLAYLISTS_SITE_CODE);
	    sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/" + Playlist.USER_PATH, PLAYLISTS_USER_CODE);	 
	    sURIMatcher.addURI(AUTHORITY, Playlist.TABLE_NAME + "/" + Playlist.USER_PATH + "/#", PLAYLISTS_USER_ID_CODE); 
	}
	
    public final class Artist {

    	private Artist() {}
       	
    	public static final String TABLE_NAME = "artists";
    	
    	public final class Columns implements BaseColumns {

    		private Columns() {}

    		// Columns:
    		public static final String SERVER_ID = "server_id";
    		public static final String NAME = "name";
    		
    	}
    }
  
    public final class Album {

    	private Album() {}
       
    	public static final String TABLE_NAME = "albums";
    	
    	// Columns:
    	public final class Columns implements BaseColumns {

    		private Columns() {}
    		
    		public static final String SERVER_ID = "server_id";
    		public static final String NAME      = "name";
    		public static final String ARTIST_ID = "artist_id";
    		public static final String YEAR      = "year";
    	}
    }

    public final class Track {

    	private Track() {}
       
    	public static final String TABLE_NAME = "tracks";
    	
    	// static
    	public final class Columns implements BaseColumns {

    		private Columns() {}

    		// Columns:
    		public static final String SERVER_ID = "server_id";
    		public static final String NAME      = "name";
    		public static final String ARTIST_ID = "artist_id";
    		public static final String ALBUM_ID  = "album_id";
    		public static final String TRACK_NO  = "track_no";
    	}
    }

    public final class Playlist {

    	private Playlist() {}
       
    	public static final String TABLE_NAME = "playlists";
    	
    	private static final String SITE_PATH = "site";
    	private static final String USER_PATH = "user";
    		
    	// static
    	public final class Columns implements BaseColumns {

    		private Columns() {}

    		// Columns:
    		public static final String SERVER_ID = "server_id";
    		public static final String NAME = "name";
    		public static final String USER_ID = "user_id";
    	}
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
	    		inserted_id = sqlDB.insert(Artist.TABLE_NAME, null, values);
	        break;
	        
	    	case ALBUMS_CODE:
	    		inserted_id = sqlDB.insert(Album.TABLE_NAME, null, values);
	        break;	        
	    	
	    	case TRACKS_CODE:
	    		inserted_id = sqlDB.insert(Track.TABLE_NAME, null, values);
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
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		 
		    int uriType = sURIMatcher.match(uri);
		    
		    switch (uriType) {
	    		case ARTISTS_CODE:
	    		    queryBuilder.setTables(Artist.TABLE_NAME);
		        break;
		    	case ARTISTS_ID_CODE:
				    queryBuilder.setTables(Artist.TABLE_NAME);
		    		queryBuilder.appendWhere(Artist.Columns._ID + "=" + uri.getLastPathSegment());
		        break;
		    	case ALBUMS_CODE:
				    queryBuilder.setTables(Album.TABLE_NAME);
		        break;
		    	case ALBUMS_ID_CODE:
				    queryBuilder.setTables(Album.TABLE_NAME);
		    		queryBuilder.appendWhere(Album.Columns._ID + "=" + uri.getLastPathSegment());
		        break;
		    	case TRACKS_CODE:
				    queryBuilder.setTables(Track.TABLE_NAME);
		        break;
		    	case TRACKS_ID_CODE:
				    queryBuilder.setTables(Track.TABLE_NAME);
		    		queryBuilder.appendWhere(Track.Columns._ID + "=" + uri.getLastPathSegment());
		        break;
		    	default:
		    		throw new IllegalArgumentException("Unknown URI");
		    }
		 
		    Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
		            projection, selection, selectionArgs, null, null, sortOrder);
		    
		    cursor.setNotificationUri(getContext().getContentResolver(), uri);
		    
		    return cursor;
		}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
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
	    		rowsAffected = sqlDB.delete(Artist.TABLE_NAME,
	                selection, selectionArgs);
	        break;
	        
	    	case ARTISTS_ID_CODE:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsAffected = sqlDB.delete(Artist.TABLE_NAME,
	                    Artist.Columns._ID + "=" + id, null);
	    		} else {
	    			rowsAffected = sqlDB.delete(Artist.TABLE_NAME,
	                    selection + " and " + Artist.Columns._ID + "=" + id,
	                    selectionArgs);
	    		}
	    	break;
	    	
	    	default:
	    		throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    
	    return rowsAffected;
	}

}
