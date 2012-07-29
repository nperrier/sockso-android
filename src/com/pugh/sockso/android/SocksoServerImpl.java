package com.pugh.sockso.android;

import java.io.IOException;
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
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

// Singleton
public class SocksoServerImpl implements SocksoServer {

	private static final String TAG = SocksoServerImpl.class.getSimpleName();
	
	private Config mConfig;
	//private String mAuthToken;   // TODO Session 
	private final String mRootUrl;

	// TODO Singleton?
	public SocksoServerImpl(final Config config) {
		this.mConfig = config;
		mRootUrl = "http://" + config.getServer() + ":" + config.getPort();
	}

	public String getRootUrl() {
		return mRootUrl;
	}

	/*
	 * TODO public Session authenticate(String username, String password)
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
	 * Session session = null; try { HttpResponse response =
	 * doPost(httpPost); if(response != null){ // TODO server has not
	 * implemented OAuth yet, so using dummy token session = new
	 * Session(username, "authToken"); } } catch (IOException e) {
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
			
			data = EntityUtils.toString(httpEntity);
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
	
}
