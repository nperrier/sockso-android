package com.pugh.sockso.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import com.pugh.sockso.android.SocksoServer;
import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.Track;

public class SocksoAPIImpl implements ISocksoAPI {

	private static final String TAG = SocksoAPIImpl.class.getSimpleName();

	private static final String API    = "api";
	private static final String LIMIT  = "limit";
	private static final String OFFSET = "offset";

	private static final int DEFAULT_LIMIT = 100;
	private static final int NO_LIMIT      = -1;

	private String mBaseApiUrl;     
	private SocksoServer mServer; 	// dependency

	// API classes:
	ServerInfoAPI serverInfoAPI;
	ArtistAPI     artistAPI;
	AlbumAPI      albumAPI;
	TrackAPI      trackAPI;
	
	public SocksoAPIImpl(final SocksoServer server) {
		
		mBaseApiUrl = server.getRootUrl();
		mBaseApiUrl += "/" + API;

		Log.d(TAG, "mBaseApiUri: " + mBaseApiUrl);
		
		mServer = server;
		
		// Create API Objects
		serverInfoAPI = new ServerInfoAPI(mBaseApiUrl);
		artistAPI     = new ArtistAPI(mBaseApiUrl);
		albumAPI      = new AlbumAPI(mBaseApiUrl);
		trackAPI      = new TrackAPI(mBaseApiUrl);
	}

	// Will have to only support a minimum version of Sockso (1.6.0?) server
	private class ServerInfoAPI {
		
		private final String mBaseUri;

		public ServerInfoAPI(final String baseApiUri) {
			this.mBaseUri = baseApiUri;
		}

		// /api
		public String getServerInfo() {
			return mBaseUri;
		}
	}

	private class ArtistAPI {

		public static final String ARTISTS = "artists";
		public static final String TRACKS = "tracks";

		private final String mBaseUri;

		public ArtistAPI(final String baseApiUri) {
			mBaseUri = baseApiUri + "/" + ARTISTS;
		}

		// /api/artists/$ID - ArtistAPI $ID
		public String getArtist(final String id) {
			return mBaseUri + "/" + id;
		}

		// /api/artists?limit=<limit>&offset=<offset>
		public String getArtists(final int limit, final int offset) {

			Uri.Builder b = Uri.parse(mBaseUri).buildUpon();

			if (limit != DEFAULT_LIMIT) {
				b.appendQueryParameter(LIMIT, Integer.toString(limit));
			}

			if (offset != 0) {
				b.appendQueryParameter(OFFSET, Integer.toString(offset));
			}

			return b.build().toString();
		}

		// /api/artists?limit=-1
		public String getArtists() {
			return getArtists(NO_LIMIT, 0);
		}

		// /api/artists/<id>/tracks
		public String getTracks(final String id) {
			return mBaseUri + "/" + id + "/" + TRACKS;
		}

	}

	private class AlbumAPI {

		public static final String ALBUMS = "albums";
		public static final String TRACKS = "tracks";

		private final String mBaseUri;

		public AlbumAPI(final String baseApiUri) {
			this.mBaseUri = baseApiUri + "/" + ALBUMS;
		}

		// /api/albums/<id>
		public String getAlbum(final String id) {
			return mBaseUri + "/" + id;
		}

		// /api/albums?limit=<limit>&offset=<offset>
		public String getAlbums(final int limit, final int offset) {

			Uri.Builder b = Uri.parse(mBaseUri).buildUpon();

			if (limit != DEFAULT_LIMIT) {
				b.appendQueryParameter(LIMIT, Integer.toString(limit));
			}

			if (offset != 0) {
				b.appendQueryParameter(OFFSET, Integer.toString(offset));
			}

			return b.build().toString();
		}

		// /api/albums?limit=-1
		public String getAlbums() {
			return getAlbums(NO_LIMIT, 0);
		}

		// /api/albums/<id>/tracks
		public String getTracks(final String id) {
			return mBaseUri + "/" + id + "/" + TRACKS;
		}

	}

	private class TrackAPI {

		public static final String TRACKS = "tracks";
		public static final String STREAM = "stream";

		private final String mBaseApiUri;
		private final String mBaseUri;

		public TrackAPI(final String baseApiUri) {
			this.mBaseApiUri = baseApiUri;
			this.mBaseUri = baseApiUri + "/" + TRACKS;
		}

		// /api/tracks/<id>
		public String getTrack(final String id) {
			return mBaseUri + "/" + id;
		}

		// /api/tracks?limit=<limit>&offset=<offset>
		public String getTracks(final int limit, final int offset) {

			Uri.Builder b = Uri.parse(mBaseUri).buildUpon();

			if (limit != DEFAULT_LIMIT) {
				b.appendQueryParameter(LIMIT, Integer.toString(limit));
			}

			if (offset != 0) {
				b.appendQueryParameter(OFFSET, Integer.toString(offset));
			}

			return b.build().toString();
		}

		// /api/tracks?limit=-1
		public String getTracks() {
			return getTracks(NO_LIMIT, 0);
		}
	}
	// TODO - API method not currently supported
	private class PlaylistAPI {

		private static final String PLAYLISTS = "playlists";
		private static final String USER = "user";
		private static final String SITE = "site";

		private final String mBaseUri;

		public PlaylistAPI(final String baseApiUri) {
			this.mBaseUri = baseApiUri + "/" +PLAYLISTS;
		}

		// /api/playlists?limit=-1
		public String getPlaylists() {
			return mBaseUri + "?" + LIMIT + "=" + NO_LIMIT;
		}

		// /api/playlists/<id>
		public String getPlaylist(final String id) {
			return mBaseUri + "/" + id;
		}

		// /api/playlists/site
		public String getSitePlaylist() {
			return mBaseUri + "/" + SITE;
		}

		// /api/playlists/user
		public String getUserPlaylist() {
			return mBaseUri + "/" + USER;
		}

		// /api/playlists/user/<id>
		public String getUserPlaylist(final int id) {
			return mBaseUri + "/" + USER + "/" + id;
		}
	}

	public ServerInfo getServerInfo() throws IOException, JSONException {
		Log.d(TAG, "getServerInfo() ran");
		
		ServerInfo info = null;
		String data = mServer.doGet(serverInfoAPI.getServerInfo());
		
		return ServerInfo.fromJSON(new JSONObject(data));
	}

	public Album getAlbum(final String id) throws IOException, JSONException {
		Log.d(TAG, "getAlbum(id) ran");
		
		String data = mServer.doGet(albumAPI.getAlbum(id));
		
		return Album.fromJSON(new JSONObject(data));
	}

	public List<Album> getAlbums() throws IOException, JSONException {
		Log.d(TAG, "getAlbums() ran");
		
		String data = mServer.doGet(albumAPI.getAlbums());
		
		return Album.fromJSONArray(new JSONArray(data));
	}

	public Artist getArtist(final String id) throws IOException, JSONException {
		Log.d(TAG, "getArtist(id) ran");
		
		String data = mServer.doGet(artistAPI.getArtist(id));
		
		return Artist.fromJSON(new JSONObject(data));
	}

	public List<Artist> getArtists() throws IOException, JSONException {
		Log.d(TAG, "getArtists() ran");
		
		String data = mServer.doGet(artistAPI.getArtists());

		return Artist.fromJSONArray(new JSONArray(data));
	}

	public Track getTrack(final String id) throws IOException, JSONException {
		Log.d(TAG, "getTrack(id) ran");
		
		String data = mServer.doGet(trackAPI.getTrack(id));

		return Track.fromJSON(new JSONObject(data));
	}

	public List<Track> getTracks() throws IOException, JSONException {
		Log.d(TAG, "getTracks() ran");
		
		String data = mServer.doGet(trackAPI.getTracks());

		return Track.fromJSONArray(new JSONArray(data));
	}

}
