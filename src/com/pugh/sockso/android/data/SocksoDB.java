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

		StringBuffer db_schema = new StringBuffer();
		
		// Albums Table
		db_schema.append("CREATE TABLE ").append(Album.TABLE_NAME)
				 .append(" (")
		 		 .append(Album.Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
		 		 .append(Album.Columns.NAME).append(" TEXT NOT NULL, ")
		 		 .append(Album.Columns.YEAR).append(" INTEGER")
		 		 .append(")");
		
		// Artists table
		db_schema.append("CREATE TABLE ").append(Artist.TABLE_NAME)
		         .append(" (")
				 .append(Artist.Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				 .append(Artist.Columns.NAME).append(" TEXT NOT NULL")
				 .append(")");

		// TrackAPI Table
		db_schema.append("CREATE TABLE ").append(Track.TABLE_NAME)
			     .append(" (")
				 .append(Track.Columns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				 .append(Track.Columns.ARTIST_ID).append(" TEXT NOT NULL, ") 
				 .append(Track.Columns.ALBUM_ID).append(" INTEGER").append("), ")
				 .append(Track.Columns.NAME).append(" TEXT NOT NULL, ")
				 .append("FOREIGN KEY(").append(Track.Columns.ARTIST_ID).append(") REFERENCES ")
				 .append(Artist.TABLE_NAME).append("(").append(Artist.Columns._ID).append("), ")
				 .append("FOREIGN KEY(").append(Track.Columns.ALBUM_ID).append(") REFERENCES ")
				 .append(Album.TABLE_NAME).append("(").append(Album.Columns._ID).append(")")
				 .append(")");

	    Log.i(DEBUG_TAG, "Creating database schema:\n" + db_schema.toString());
		db.execSQL(db_schema.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	    Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
	            + oldVersion + "]->[" + newVersion + "]");
	    
		StringBuffer drop_schema = new StringBuffer();
		
		drop_schema.append("DROP TABLE IF EXISTS ").append(Album.TABLE_NAME).append(";")
				   .append("DROP TABLE IF EXISTS ").append(Artist.TABLE_NAME).append(";")
				   .append("DROP TABLE IF EXISTS ").append(Track.TABLE_NAME).append(";");
	    db.execSQL(drop_schema.toString());
	    
	    onCreate(db);
	}

}
