package com.pugh.sockso.android.data;

import com.pugh.sockso.android.data.SocksoProvider.Album;
import com.pugh.sockso.android.data.SocksoProvider.Artist;
import com.pugh.sockso.android.data.SocksoProvider.Track;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SocksoDB extends SQLiteOpenHelper {

	private static final String DEBUG_TAG  = SocksoDB.class.getName();
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
		table_artists.append("CREATE TABLE ").append(Artist.TABLE_NAME)
					 .append(" (")
					 .append(Artist.Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					 .append(Artist.Columns.NAME).append(" TEXT NOT NULL")
					 .append(")").append(";");

		// Albums Table
		table_albums.append("CREATE TABLE ").append(Album.TABLE_NAME)
					.append(" (")
					.append(Album.Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					.append(Album.Columns.NAME).append(" TEXT NOT NULL, ")	
					.append(Album.Columns.YEAR).append(" INTEGER, ")
					.append(Album.Columns.ARTIST_ID).append(" INTEGER, ")
					.append("FOREIGN KEY(").append(Album.Columns.ARTIST_ID).append(") REFERENCES ")
					.append(Artist.TABLE_NAME).append("(").append(Artist.Columns._ID).append(") ")
					.append(")").append(";");
		
		// TrackAPI Table
		table_tracks.append("CREATE TABLE ").append(Track.TABLE_NAME)
					.append(" (")
					.append(Track.Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					.append(Track.Columns.NAME).append(" TEXT NOT NULL, ")
					.append(Track.Columns.ARTIST_ID).append(" INTEGER, ")
					.append(Track.Columns.ALBUM_ID).append(" INTEGER, ")
					.append("FOREIGN KEY(").append(Track.Columns.ARTIST_ID).append(") REFERENCES ")
					.append(Artist.TABLE_NAME).append("(").append(Artist.Columns._ID).append("), ")
					.append("FOREIGN KEY(").append(Track.Columns.ALBUM_ID).append(") REFERENCES ")
					.append(Album.TABLE_NAME).append("(").append(Album.Columns._ID).append(")")
					.append(")").append(";");

	    Log.i(DEBUG_TAG, "Creating database schema:\n" + 
	    		table_artists.toString() + "\n" +
	    		table_albums.toString() + "\n" +
	    		table_tracks.toString() + "\n");
	    db.execSQL(table_artists.toString());
	    db.execSQL(table_albums.toString());
	    db.execSQL(table_tracks.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	    Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
	            + oldVersion + "]->[" + newVersion + "]");
	    
		StringBuffer drop_schema = new StringBuffer();
		
		drop_schema.append("DROP TABLE IF EXISTS ").append(Artist.TABLE_NAME).append(";")
				   .append("DROP TABLE IF EXISTS ").append(Album.TABLE_NAME).append(";")
				   .append("DROP TABLE IF EXISTS ").append(Track.TABLE_NAME).append(";");
	    db.execSQL(drop_schema.toString());
	    
	    onCreate(db);
	}

}
