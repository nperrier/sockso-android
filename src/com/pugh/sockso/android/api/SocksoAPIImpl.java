package com.pugh.sockso.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.net.Uri;

import com.pugh.sockso.android.SocksoServer;
import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.Track;

public class SocksoAPIImpl implements ISocksoAPI {

	private static final String TAG = SocksoAPIImpl.class.getName();

	private static final String API = "api";
	private static final String LIMIT = "limit";
	private static final String OFFSET = "offset";

	private static final int DEFAULT_LIMIT = 100;
	private static final int NO_LIMIT = -1;

	private Uri.Builder mBaseApiUri;
	private SocksoServer server; 	// dependency

	public SocksoAPIImpl(SocksoServer server) {
		mBaseApiUri = server.getRootUri().path(API);
	}

	// Will have to only support a minimum version of Sockso (1.6.0?) server
	private class ServerInfoAPI {

		private final Uri.Builder mBaseUri;

		public ServerInfoAPI(Uri.Builder baseApiUri) {
			this.mBaseUri = baseApiUri;
		}

		public String getServerInfo() {
			return mBaseUri.build().toString();
		}
	}

	private class AlbumAPI {

		private static final String ALBUMS = "albums";
		private static final String TRACKS = "tracks";

		private final Uri.Builder mBaseUri;

		public AlbumAPI(Uri.Builder baseApiUri) {
			this.mBaseUri = baseApiUri.path(ALBUMS);
		}

		// /api/albums/<id>
		public String getAlbum(final String id) {
			return mBaseUri.path(id).build().toString();
		}

		// /api/albums?limit=<limit>&offset=<offset>
		public String getAlbums(final int limit, final int offset) {

			Uri.Builder b = mBaseUri;

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
			return mBaseUri.path(id).path(TRACKS).build().toString();
		}

	}

	private class TrackAPI {

		private static final String TRACKS = "tracks";

		private final Uri.Builder mBaseUri;

		public TrackAPI(Uri.Builder baseApiUri) {
			this.mBaseUri = baseApiUri.path(TRACKS);
		}

		// /api/tracks/<id>
		public String getTrack(final String id) {
			return mBaseUri.path(id).build().toString();
		}

		// /api/tracks?limit=<limit>&offset=<offset>
		public String getTracks(final int limit, final int offset) {

			Uri.Builder b = mBaseUri;

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

	private class ArtistAPI {

		private static final String ARTISTS = "artists";
		private static final String TRACKS = "tracks";

		private final Uri.Builder mBaseUri;

		public ArtistAPI(Uri.Builder baseApiUri) {
			this.mBaseUri = baseApiUri.path(ARTISTS);
		}

		// /api/artists/$ID - ArtistAPI $ID
		public String getArtist(final String id) {
			return mBaseUri.path(id).build().toString();
		}

		// /api/artists?limit=<limit>&offset=<offset>
		public String getArtists(final int limit, final int offset) {

			Uri.Builder b = mBaseUri;

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
			return mBaseUri.path(id).path(TRACKS).build().toString();
		}

	}

	// TODO - API method not currently supported
	private class PlaylistAPI {

		private static final String PLAYLISTS = "playlists";
		private static final String USER = "user";
		private static final String SITE = "site";

		private final Uri.Builder mBaseUri;

		public PlaylistAPI(Uri.Builder baseApiUri) {
			this.mBaseUri = baseApiUri.path(PLAYLISTS);
		}

		// /api/playlists?limit=-1
		public String getPlaylists() {
			return mBaseUri.appendQueryParameter(LIMIT, Integer.toString(NO_LIMIT)).build().toString();
		}

		// /api/playlists/<id>
		public String getPlaylist(final String id) {
			return mBaseUri.path(id).build().toString();
		}

		// /api/playlists/site
		public String getSitePlaylist() {
			return mBaseUri.path(SITE).build().toString();
		}

		// /api/playlists/user
		public String getUserPlaylist() {
			return mBaseUri.path(USER).build().toString();
		}

		// /api/playlists/user/<id>
		public String getUserPlaylist(final int id) {
			return mBaseUri.path(USER).path(Integer.toString(id)).build().toString();
		}
	}

	public ServerInfo getServerInfo() throws IOException {

		ServerInfo info = null;
		ServerInfoAPI api = new ServerInfoAPI(mBaseApiUri);

		String data = server.doGet(api.getServerInfo());

		return info;
	}

	public Album getAlbum(final String id) throws IOException {

		Album album = null;
		AlbumAPI api = new AlbumAPI(mBaseApiUri);

		String data = server.doGet(api.getAlbum(id));

		return album;
	}

	public List<Album> getAlbums() throws IOException {

		List<Album> albums = new ArrayList<Album>();
		AlbumAPI api = new AlbumAPI(mBaseApiUri);

		String data = server.doGet(api.getAlbums());

		return albums;
	}

	public Artist getArtist(final String id) throws IOException {

		Artist artist = null;
		ArtistAPI api = new ArtistAPI(mBaseApiUri);

		String data = server.doGet(api.getArtist(id));

		return artist;
	}

	public List<Artist> getArtists() throws IOException {

		List<Artist> artists = new ArrayList<Artist>();
		ArtistAPI api = new ArtistAPI(mBaseApiUri);

		String data = server.doGet(api.getArtists());

		return artists;
	}

	public Track getTrack(final String id) throws IOException {

		Track track = null;
		TrackAPI api = new TrackAPI(mBaseApiUri);

		String data = server.doGet(api.getTrack(id));

		return track;
	}

	public List<Track> getTracks() throws IOException {

		List<Track> tracks = new ArrayList<Track>();
		TrackAPI api = new TrackAPI(mBaseApiUri);

		String data = server.doGet(api.getTracks());

		return tracks;
	}

}
