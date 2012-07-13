package com.pugh.sockso.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.pugh.sockso.android.api.AlbumAPI;
import com.pugh.sockso.android.api.ArtistAPI;
import com.pugh.sockso.android.api.LoginAPI;
import com.pugh.sockso.android.api.TrackAPI;
import com.pugh.sockso.android.music.Album;
import com.pugh.sockso.android.music.Artist;
import com.pugh.sockso.android.music.Track;

// Singleton
public class SocksoServer {

	private static final String TAG = SocksoServer.class.getName();

	private static SocksoServer instance;

	private SocksoConfig mConfig;
	private String mAuthToken;
	
	private SocksoServer(SocksoConfig config){
		this.mConfig = config;
	}

	public static SocksoServer getInstance(SocksoConfig config) {
		if (instance == null) {
			instance = new SocksoServer(config);
		}
		return instance;
	}
	
	public SocksoSession authenticate(String username, String password) {

		LoginAPI login = new LoginAPI(mConfig.getServer(), mConfig.getPort());
		Uri uri = login.getLoginURI();
		Log.i(TAG, "login URL: " + uri.toString());

		HttpPost httpPost = new HttpPost();
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("todo", "login")); 
		nameValuePair.add(new BasicNameValuePair("name", username));
		nameValuePair.add(new BasicNameValuePair("pass", password));

		// URL Encode the POST parameters
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Error encoding params");
			e.printStackTrace();
		}

		SocksoSession session = null;
		try {
			HttpResponse response = doPost(httpPost);
			if(response != null){
				// TODO server has not implemented OAuth yet, so using dummy token
				session = new SocksoSession(username, "authToken");
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return session;
	}
	
	public Album getAlbum(String id){
		
		AlbumAPI api = new AlbumAPI(mConfig.getServer(), mConfig.getPort());
		HttpGet httpGet = new HttpGet(api.getAlbum(id));
		Album album = null;
		try {
			HttpResponse response = doGet(httpGet);
			if(response != null){
				// TODO parse response and build album 
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return album;		
	}

	public Album[] getAlbums(){
		AlbumAPI api = new AlbumAPI(mConfig.getServer(), mConfig.getPort());
		HttpGet httpGet = new HttpGet(api.getAlbums());
		Album[] albums = null;
		try {
			HttpResponse response = doGet(httpGet);
			if(response != null){
				// TODO parse response and build albums array 
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return albums;	
	}
		
	public Artist getArtist(String id){
		ArtistAPI api = new ArtistAPI(mConfig.getServer(), mConfig.getPort());
		HttpGet httpGet = new HttpGet(api.getArtist(id));
		Artist artist = null;
		try {
			HttpResponse response = doGet(httpGet);
			if(response != null){
				// TODO parse response and build artist
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return artist;	
	}
	
	public Artist[] getArtists(){
		ArtistAPI api = new ArtistAPI(mConfig.getServer(), mConfig.getPort());
		HttpGet httpGet = new HttpGet(api.getArtists());
		Artist[] artists = null;
		try {
			HttpResponse response = doGet(httpGet);
			if(response != null){
				
				// TODO parse response and build artists array
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return artists;	
	}
	
	public Track getTrack(String id){
		TrackAPI api = new TrackAPI(mConfig.getServer(), mConfig.getPort());
		HttpGet httpGet = new HttpGet(api.getTrack(id));
		Track track = null;
		try {
			HttpResponse response = doGet(httpGet);
			if(response != null){
				// TODO parse response and build track
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return track;	
	}
		
	public Track[] getTracks(){
		TrackAPI api = new TrackAPI(mConfig.getServer(), mConfig.getPort());
		HttpGet httpGet = new HttpGet(api.getTracks());
		Track[] tracks = null;
		try {
			HttpResponse response = doGet(httpGet);
			if(response != null){
				// TODO parse response and build track array
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		return tracks;
	}
		
	// TODO
	public static Bitmap getImage(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
		conn.setRequestProperty("connection", "close");
		conn.setRequestMethod("GET");

		return BitmapFactory.decodeStream(conn.getInputStream());
	}

	public static HttpResponse doPost(HttpPost httpPost) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpResponse response = null;
		
		try {
			Log.i(TAG, "Connecting to Sockso server...");
			// Pass local context as a parameter
			response = httpClient.execute(httpPost);

			// response headers
			Log.i(TAG, "Http Response: " + response.toString());
			Log.i(TAG, "Status: " + response.getStatusLine());
			for (Header h : response.getAllHeaders()) {
				Log.i(TAG, h.getName() + ": " + h.getValue());
			}
			
			// clean the contents of the response
			response.getEntity().consumeContent();

		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error in HTTP protocol: " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

	public static HttpResponse doGet(HttpGet httpGet) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpResponse response = null;
		
		try {
			Log.i(TAG, "Connecting to Sockso server...");
			// Pass local context as a parameter
			response = httpClient.execute(httpGet);

			// response headers
			Log.i(TAG, "Http Response: " + response.toString());
			Log.i(TAG, "Status: " + response.getStatusLine());
			for (Header h : response.getAllHeaders()) {
				Log.i(TAG, h.getName() + ": " + h.getValue());
			}
			// clean the contents of the response
			response.getEntity().consumeContent();

		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error in HTTP protocol: " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

}
