package com.pugh.sockso.android.data;

import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.ArtistColumns;
import com.pugh.sockso.android.data.SocksoProvider.SearchColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SocksoDB extends SQLiteOpenHelper {

	private static final String TAG = SocksoDB.class.getSimpleName();
	
	private static final int    DB_VERSION = 1;
	private static final String DB_NAME    = "sockso.db";
	
	public SocksoDB(Context context) {
	    super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		StringBuilder albumsTable     = new StringBuilder();
		StringBuilder artistsTable    = new StringBuilder();
		StringBuilder tracksTable     = new StringBuilder();
		StringBuilder searchView      = new StringBuilder();
		
		// Artists table
		artistsTable.append("CREATE TABLE ").append(ArtistColumns.TABLE_NAME)
		            .append(" (")
				    .append(ArtistColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					.append(ArtistColumns.SERVER_ID).append(" INTEGER NOT NULL, ")					 
					.append(ArtistColumns.NAME).append(" TEXT NOT NULL")
					.append(");\n")
					.append("CREATE INDEX ").append(ArtistColumns.SERVER_ID).append("_i")
					.append(" ON ").append(ArtistColumns.TABLE_NAME).append(" (").append(ArtistColumns.SERVER_ID).append(");");
		
	    // Albums Table
		albumsTable.append("CREATE TABLE ").append(AlbumColumns.TABLE_NAME)
		           .append(" (")
		           .append(AlbumColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
		           .append(ArtistColumns.SERVER_ID).append(" INTEGER NOT NULL, ")
		           .append(AlbumColumns.NAME).append(" TEXT NOT NULL, ")
		           .append(AlbumColumns.YEAR).append(" INTEGER, ")
		           .append(AlbumColumns.ARTIST_ID).append(" INTEGER, ")
		           .append("FOREIGN KEY(").append(AlbumColumns.ARTIST_ID).append(") REFERENCES ")
		           .append(ArtistColumns.TABLE_NAME).append("(").append(ArtistColumns._ID).append(") ")
		           .append(");\n")
		           .append("CREATE INDEX ").append(AlbumColumns.SERVER_ID).append("_i")
		           .append(" ON ").append(AlbumColumns.TABLE_NAME).append(" (").append(AlbumColumns.SERVER_ID)
		           .append(");");

		// Tracks Table
		tracksTable.append("CREATE TABLE ").append(TrackColumns.TABLE_NAME)
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
					.append(");\n")
					.append("CREATE INDEX ").append(TrackColumns.SERVER_ID).append("_i")
                    .append(" ON ").append(TrackColumns.TABLE_NAME).append(" (").append(TrackColumns.SERVER_ID)
                    .append(");");

		// View for searching across all the tables:
		searchView.append("CREATE VIEW ").append(SearchColumns.TABLE_NAME).append(" AS ")
                   .append("SELECT ")
                   .append(ArtistColumns.FULL_ID).append(" AS ").append(SearchColumns._ID).append(", ")
                   .append("'").append(ArtistColumns.MIME_TYPE).append("' AS ").append(SearchColumns.MIME_TYPE).append(", ")
                   .append(ArtistColumns.FULL_NAME).append(" AS ").append(SearchColumns.ARTIST_NAME).append(", ")
                   .append("NULL AS ").append(SearchColumns.ALBUM_NAME).append(", ")
                   .append("NULL AS ").append(SearchColumns.TRACK_NAME).append(", ")
                   .append(ArtistColumns.FULL_NAME).append(" AS ").append(SearchColumns.MATCH).append(", ")
                   .append("1 AS ").append(SearchColumns.GROUP_ORDER)
                   .append(" FROM ").append(ArtistColumns.TABLE_NAME)
                   .append(" UNION ALL ")
                   .append("SELECT ")
                   .append(AlbumColumns.FULL_ID).append(" AS ").append(SearchColumns._ID).append(", ")
                   .append("'").append(AlbumColumns.MIME_TYPE).append("' AS ").append(SearchColumns.MIME_TYPE).append(", ")
                   .append(ArtistColumns.FULL_NAME).append(" AS artist, ")
                   .append(AlbumColumns.FULL_NAME).append(" AS album, ")
                   .append("NULL AS ").append(SearchColumns.TRACK_NAME).append(", ")
                   .append(AlbumColumns.FULL_NAME).append(" AS ").append(SearchColumns.MATCH).append(", ")
                   .append("2 AS ").append(SearchColumns.GROUP_ORDER)
                   .append(" FROM ").append(AlbumColumns.TABLE_NAME)
                   .append(" JOIN ").append(ArtistColumns.TABLE_NAME)
                   .append(" ON ").append(AlbumColumns.FULL_ARTIST_ID).append(" = ").append(ArtistColumns.FULL_SERVER_ID)
                   .append(" UNION ALL ")
                   .append("SELECT ")
                   .append(TrackColumns.FULL_ID).append(" AS ").append(SearchColumns._ID).append(", ")
                   .append("'").append(TrackColumns.MIME_TYPE).append("' AS ").append(SearchColumns.MIME_TYPE).append(", ")
                   .append(ArtistColumns.FULL_NAME).append(" AS ").append(SearchColumns.ARTIST_NAME).append(", ")
                   .append(AlbumColumns.FULL_NAME).append(" AS ").append(SearchColumns.ALBUM_NAME).append(", ")
                   .append(TrackColumns.FULL_NAME).append(" AS ").append(SearchColumns.TRACK_NAME).append(", ")
                   .append(TrackColumns.FULL_NAME).append(" AS ").append(SearchColumns.MATCH).append(", ")
                   .append("3 AS ").append(SearchColumns.GROUP_ORDER)
                   .append(" FROM ").append(TrackColumns.TABLE_NAME)
                   .append(" JOIN ").append(AlbumColumns.TABLE_NAME)
                   .append(" ON ").append(TrackColumns.FULL_ALBUM_ID).append(" = ").append(AlbumColumns.FULL_SERVER_ID)
                   .append(" JOIN ").append(ArtistColumns.TABLE_NAME)
                   .append(" ON ").append(TrackColumns.FULL_ARTIST_ID).append(" = ").append(ArtistColumns.FULL_SERVER_ID)
                   .append(";");
		
	    Log.i(TAG, "Creating database schema:\n" + 
	            artistsTable + "\n" +
	    		albumsTable  + "\n" +
	    		tracksTable  + "\n" +
	    		searchView   + "\n");
	    
	    db.execSQL(artistsTable.toString());
	    db.execSQL(albumsTable.toString());
	    db.execSQL(tracksTable.toString());
        db.execSQL(searchView.toString());
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
