package com.pugh.sockso.android.data;

import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.ArtistColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SocksoDB extends SQLiteOpenHelper {

	private static final String TAG  = "SocksoDB";
	private static final int    DB_VERSION = 1;
	private static final String DB_NAME    = "sockso.db";
	
	public SocksoDB(Context context) {
	    super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		StringBuffer table_albums  = new StringBuffer();
		StringBuffer table_artists = new StringBuffer();
		StringBuffer table_tracks  = new StringBuffer();
		
		// Artists table
		table_artists.append("CREATE TABLE ").append(ArtistColumns.TABLE_NAME)
					 .append(" (")
					 .append(ArtistColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					 .append(ArtistColumns.SERVER_ID).append(" INTEGER NOT NULL, ")					 
					 .append(ArtistColumns.NAME).append(" TEXT NOT NULL")
					 .append(")").append(";");

		// Albums Table
		table_albums.append("CREATE TABLE ").append(AlbumColumns.TABLE_NAME)
					.append(" (")
					.append(AlbumColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					.append(ArtistColumns.SERVER_ID).append(" INTEGER NOT NULL, ")
					.append(AlbumColumns.NAME).append(" TEXT NOT NULL, ")	
					.append(AlbumColumns.YEAR).append(" INTEGER, ")
					.append(AlbumColumns.ARTIST_ID).append(" INTEGER, ")
					.append("FOREIGN KEY(").append(AlbumColumns.ARTIST_ID).append(") REFERENCES ")
					.append(ArtistColumns.TABLE_NAME).append("(").append(ArtistColumns._ID).append(") ")
					.append(")").append(";");
		
		// TrackAPIBuilder Table
		table_tracks.append("CREATE TABLE ").append(TrackColumns.TABLE_NAME)
					.append(" (")
					.append(TrackColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					.append(ArtistColumns.SERVER_ID).append(" INTEGER NOT NULL, ")
					.append(TrackColumns.NAME).append(" TEXT NOT NULL, ")
					.append(TrackColumns.TRACK_NO).append(" INTEGER, ")
					.append(TrackColumns.ARTIST_ID).append(" INTEGER, ")
					.append(TrackColumns.ALBUM_ID).append(" INTEGER, ")
					.append("FOREIGN KEY(").append(TrackColumns.ARTIST_ID).append(") REFERENCES ")
					.append(ArtistColumns.TABLE_NAME).append("(").append(ArtistColumns._ID).append("), ")
					.append("FOREIGN KEY(").append(TrackColumns.ALBUM_ID).append(") REFERENCES ")
					.append(AlbumColumns.TABLE_NAME).append("(").append(AlbumColumns._ID).append(")")
					.append(")").append(";");

	    Log.i(TAG, "Creating database schema:\n" + 
	    		table_artists.toString() + "\n" +
	    		table_albums.toString() + "\n" +
	    		table_tracks.toString() + "\n");
	    
	    db.execSQL(table_artists.toString());
	    db.execSQL(table_albums.toString());
	    db.execSQL(table_tracks.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	    //Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
	    //        + oldVersion + "]->[" + newVersion + "]");
	    
		//StringBuffer drop_schema = new StringBuffer();
		
		// TODO Probably should have migrator class to handle upgrades between versions,  but
		// for now wiping out the db and re-populating is safest (data should be read-only anyway)
		//drop_schema.append("DROP TABLE IF EXISTS ").append(ArtistColumns.TABLE_NAME).append(";")
		//		   .append("DROP TABLE IF EXISTS ").append(AlbumColumns.TABLE_NAME).append(";")
		//		   .append("DROP TABLE IF EXISTS ").append(TrackColumns.TABLE_NAME).append(";");
	    //db.execSQL(drop_schema.toString());
	    
	    //onCreate(db);
	}

}
