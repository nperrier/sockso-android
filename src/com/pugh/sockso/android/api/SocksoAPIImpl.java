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

public class SocksoAPIImpl implements SocksoAPI {

	private static final String TAG = SocksoAPIImpl.class.getSimpleName();

	private static final String API = "api";
	private static final String LIMIT = "limit";
	private static final String OFFSET = "offset";
	private static final String FROM_DATE = "fromDate";

	public static final int DEFAULT_LIMIT = 100;
	public static final int NO_LIMIT = -1;

	private String mBaseApiUrl;
	private SocksoServer mServer; 	// dependency

	public SocksoAPIImpl(SocksoServer server) {
		
		mBaseApiUrl = server.getRootUrl();
		mBaseApiUrl += "/" + API;

		Log.d(TAG, "mBaseApiUri: " + mBaseApiUrl);
		
		mServer = server;
		
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

	private class BaseAPI {
	    
	    protected final String mBaseUri;
	    
	    public BaseAPI(String baseApiUri) {
	        mBaseUri = baseApiUri;
	    }
	    
	    protected String buildUriString(int limit, int offset, long from) {

            Uri.Builder b = Uri.parse(mBaseUri).buildUpon();

            if (limit != DEFAULT_LIMIT) {
                b.appendQueryParameter(LIMIT, Integer.toString(limit));
            }

            if (offset != 0) {
                b.appendQueryParameter(OFFSET, Integer.toString(offset));
            }

            if (from != 0) {
                b.appendQueryParameter(FROM_DATE, Long.toString(from));
            }
            
            return b.build().toString();
	    }
	}
	
	private class ArtistAPI extends BaseAPI {

		public static final String ARTISTS = "artists";
		public static final String TRACKS  = "tracks";

		public ArtistAPI(String baseApiUri) {
		    super(baseApiUri + "/" + ARTISTS);
		}

		// /api/artists/$ID - ArtistAPI $ID
		public String getArtist(final String id) {
			return mBaseUri + "/" + id;
		}

		// /api/artists?limit=<limit>&offset=<offset>
		public String getArtists(int limit, int offset, long from) {
		    return buildUriString(limit, offset, from);
		}

		// /api/artists?limit=-1
		public String getArtists() {
			return getArtists(NO_LIMIT, 0, 0);
		}

		// /api/artists/<id>/tracks
		public String getTracks(final String id) {
			return mBaseUri + "/" + id + "/" + TRACKS;
		}

        public String getArtistsFrom(long from) {
            return buildUriString(NO_LIMIT, 0, from);
        }

	}

	private class AlbumAPI extends BaseAPI {

		public static final String ALBUMS = "albums";
		public static final String TRACKS = "tracks";

		public AlbumAPI(String baseApiUri) {
			super(baseApiUri + "/" + ALBUMS);
		}

		// /api/albums/<id>
		public String getAlbum(final String id) {
			return mBaseUri + "/" + id;
		}

		public String getAlbumsFrom(long from) {
            return buildUriString(NO_LIMIT, 0, from);
		}
		
		// /api/albums?limit=<limit>&offset=<offset>&fromDate=<from>
		public String getAlbums(int limit, int offset, long from) {
            return buildUriString(limit, offset, from);
		}

		// /api/albums?limit=-1
		public String getAlbums() {
			return getAlbums(NO_LIMIT, 0, 0);
		}

		// /api/albums/<id>/tracks
		public String getTracks(final String id) {
			return mBaseUri + "/" + id + "/" + TRACKS;
		}

	}

	private class TrackAPI extends BaseAPI {

		public static final String TRACKS = "tracks";

		public TrackAPI(String baseApiUri) {
		    super(baseApiUri + "/" + TRACKS);
		}

		// /api/tracks/<id>
		public String getTrack(final String id) {
			return mBaseUri + "/" + id;
		}

		// /api/tracks?limit=<limit>&offset=<offset>
		public String getTracks(int limit, int offset, long from) {
            return buildUriString(limit, offset, from);
		}

		// /api/tracks?limit=-1
		public String getTracks() {
			return getTracks(NO_LIMIT, 0, 0);
		}

        public String getTracksFrom(long from) {
            return buildUriString(NO_LIMIT, 0, from);
        }

	}
	// TODO - API method not currently supported
	private class PlaylistAPI extends BaseAPI {

		private static final String PLAYLISTS = "playlists";
		private static final String USER      = "user";
		private static final String SITE      = "site";

		public PlaylistAPI(String baseApiUri) {
			super(baseApiUri + "/" +PLAYLISTS);
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
		ServerInfoAPI api = new ServerInfoAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getServerInfo());
		
		info = ServerInfo.fromJSON(new JSONObject(data));
		return info;
	}

	public Album getAlbum(final String id) throws IOException, JSONException {
		Log.d(TAG, "getAlbum(id) ran");
		
		Album album = null;
		AlbumAPI api = new AlbumAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getAlbum(id));
		
		album = Album.fromJSON(new JSONObject(data));
		return album;
	}

    public List<Album> getAlbums() throws IOException, JSONException {
        return getAlbums(0);
    }
	
	public List<Album> getAlbums(long from) throws IOException, JSONException {
		Log.d(TAG, "getAlbums() ran");
		
		List<Album> albums = new ArrayList<Album>();
		AlbumAPI api = new AlbumAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getAlbumsFrom(from));
		
		albums = Album.fromJSONArray(new JSONArray(data));
		return albums;
	}
	
	public Artist getArtist(final String id) throws IOException, JSONException {
		Log.d(TAG, "getArtist(id) ran");
		
		Artist artist = null;
		ArtistAPI api = new ArtistAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getArtist(id));

		artist = Artist.fromJSON(new JSONObject(data));
		return artist;
	}

    public List<Artist> getArtists() throws IOException, JSONException {
        return getArtists(0);
    }
    
	public List<Artist> getArtists(long from) throws IOException, JSONException {
		Log.d(TAG, "getArtists() ran");
		  
		List<Artist> artists = new ArrayList<Artist>();
		ArtistAPI api = new ArtistAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getArtistsFrom(from));

		artists = Artist.fromJSONArray(new JSONArray(data));
		return artists;
	}

	public Track getTrack(final String id) throws IOException, JSONException {
		Log.d(TAG, "getTrack(id) ran");
		
		Track track = null;
		TrackAPI api = new TrackAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getTrack(id));

		track = Track.fromJSON(new JSONObject(data));
		return track;
	}
	
	public List<Track> getTracks() throws IOException, JSONException {
	    return getTracks(0);
	}
	
	public List<Track> getTracks(long from) throws IOException, JSONException {
		Log.d(TAG, "getTracks() ran");
		
		List<Track> tracks = new ArrayList<Track>();
		TrackAPI api = new TrackAPI(mBaseApiUrl);

		String data = mServer.doGet(api.getTracksFrom(from));

		tracks = Track.fromJSONArray(new JSONArray(data));
		return tracks;
	}

}
