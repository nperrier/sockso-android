package com.pugh.sockso.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

// Singleton
public class SocksoServer implements ISocksoServer {

	private static final String TAG = SocksoServer.class.getName();
	
	private static SocksoServer instance;
	private SocksoConfig mConfig;
	//private String       mAuthToken; // Session TODO
	private Uri.Builder  mRootUri;

	private SocksoServer(final SocksoConfig config) {
		this.mConfig = config;
		mRootUri = Uri.parse("http://" + config.getServer() + ":" + config.getPort()).buildUpon();
	}

	// Singleton
	public static synchronized SocksoServer getInstance(final SocksoConfig config) {
		if (instance == null) {
			instance = new SocksoServer(config);
		}
		return instance;
	}

	public Uri.Builder getRootUri() {
		return mRootUri;
	}

	/*
	 * TODO public SocksoSession authenticate(String username, String password)
	 * {
	 * 
	 * LoginAPI login = new LoginAPI(mConfig.getServer(), mConfig.getPort());
	 * Uri uri = login.getLoginURI(); Log.i(TAG, "login URL: " +
	 * uri.toString());
	 * 
	 * HttpPost httpPost = new HttpPost(); // Building post parameters key and
	 * value pair List<NameValuePair> nameValuePair = new
	 * ArrayList<NameValuePair>(2); nameValuePair.add(new
	 * BasicNameValuePair("todo", "login")); nameValuePair.add(new
	 * BasicNameValuePair("name", username)); nameValuePair.add(new
	 * BasicNameValuePair("pass", password));
	 * 
	 * // URL Encode the POST parameters try { httpPost.setEntity(new
	 * UrlEncodedFormEntity(nameValuePair)); } catch
	 * (UnsupportedEncodingException e) { Log.e(TAG, "Error encoding params");
	 * e.printStackTrace(); }
	 * 
	 * SocksoSession session = null; try { HttpResponse response =
	 * doPost(httpPost); if(response != null){ // TODO server has not
	 * implemented OAuth yet, so using dummy token session = new
	 * SocksoSession(username, "authToken"); } } catch (IOException e) {
	 * Log.e(TAG, "IOException: " + e.getMessage()); e.printStackTrace(); }
	 * 
	 * return session; }
	 */

	// TODO
	public static Bitmap getImage(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestProperty("connection", "close");
		conn.setRequestMethod("GET");

		return BitmapFactory.decodeStream(conn.getInputStream());
	}

	public String doGet(String url) throws IOException {

		Log.d(TAG, "doGet() url: " + url);

		String data = null;
		URI encodedUri = null;
		
		try {
			encodedUri = new URI(url);
		} catch (URISyntaxException e) {
			Log.e(TAG, "Bad URI: " + e.getMessage());
			e.printStackTrace();
		}
		HttpGet httpGet = new HttpGet(encodedUri);

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			
			// response headers
			Log.d(TAG, "Http Response: " + httpResponse.toString());
			Log.d(TAG, "Status: " + httpResponse.getStatusLine());
			for (Header h : httpResponse.getAllHeaders()) {
				Log.d(TAG, h.getName() + ": " + h.getValue());
			}

		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error in HTTP protocol: " + e.getMessage());
			e.printStackTrace();
		}

		HttpEntity httpEntity = httpResponse.getEntity();

		if (httpEntity != null) {

			InputStream inputStream = httpEntity.getContent();
			data = convertStreamToString(inputStream);

			// clean the contents of the response
			httpEntity.consumeContent();
		}

		return data;
	}
	
	/*
	 * public HttpResponse doPost(HttpPost httpPost) throws IOException {
	 * 
	 * DefaultHttpClient httpClient = new DefaultHttpClient();
	 * 
	 * HttpResponse response = null;
	 * 
	 * try { Log.i(TAG, "Connecting to Sockso server..."); // Pass local context
	 * as a parameter response = httpClient.execute(httpPost);
	 * 
	 * // response headers Log.i(TAG, "Http Response: " + response.toString());
	 * Log.i(TAG, "Status: " + response.getStatusLine()); for (Header h :
	 * response.getAllHeaders()) { Log.i(TAG, h.getName() + ": " +
	 * h.getValue()); }
	 * 
	 * // clean the contents of the response
	 * response.getEntity().consumeContent();
	 * 
	 * } catch (ClientProtocolException e) { Log.e(TAG,
	 * "Error in HTTP protocol: " + e.getMessage()); e.printStackTrace(); }
	 * 
	 * return response; }
	 */
	
	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			Log.e(TAG, "Error coverting stream to String: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
