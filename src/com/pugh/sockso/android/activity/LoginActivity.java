package com.pugh.sockso.android.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.SocksoConfig;
import com.pugh.sockso.android.SocksoSession;
import com.pugh.sockso.android.account.SocksoAccountAuthenticator;

public class LoginActivity extends Activity {

	private final static String TAG = LoginActivity.class.getName();

	public static final String LOGIN_INTENT    = "com.pugh.sockso.android.activity.LOGIN";
	public static final String LOGIN_PREFS     = "sockso-login-preferences";
	public static final String SOCKSO_USER     = "sockso_user";
	public static final String SOCKSO_SESS_KEY = "sockso_session_key";
	public static final String SOCKSO_PASS     = "sockso_password";
	public static final String SOCKSO_SERVER   = "sockso_server";
	

	private LoginTask mLoginTask;
	private Button mLoginButton;
	//private boolean mNewUser = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "\"onCreate\" ran");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		mLoginButton = (Button) findViewById(R.id.login_button);
		
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.i(TAG, "\"Connect\" button clicked!");

				// If already running, pushing the button again should do
				// nothing!
				if (mLoginTask != null) {
					return;
				}

				// TODO
				EditText editServer = (EditText) findViewById(R.id.server_address);
				String server = editServer.getText().toString();

				EditText editUser = (EditText) findViewById(R.id.login_user);
				String user = editUser.getText().toString();

				EditText editPass = (EditText) findViewById(R.id.login_password);
				String password = editPass.getText().toString();

				// TODO improve validation
				if (user.length() == 0 || password.length() == 0 || server.length() == 0) {
					// SocksoApp.getInstance().presentError(v.getContext(),
					// getResources().getString(R.string.ERROR_MISSINGINFO_TITLE),
					// getResources().getString(R.string.ERROR_MISSINGINFO));
					Log.e(TAG, "Username or Password was blank!");
					return;
				}

				mLoginTask = new LoginTask(view.getContext());
				mLoginTask.execute(user, password, server);
			}
		});

	}

	private class LoginTask extends AsyncTask<String, Void, SocksoSession> {

		ProgressDialog mDialog;
		SocksoConfig mConfig;

		LoginTask(Context context) {
			mLoginButton.setEnabled(false);

			mDialog = ProgressDialog.show(context, "", getString(R.string.login_authenticating), true, false);
			mDialog.setCancelable(true);
		}

		@Override
		public SocksoSession doInBackground(String... params) {
			
			String user   = params[0];
			String pass   = params[1];
			String server = params[2];

			mConfig = new SocksoConfig(user, pass, server);
				
			SocksoSession session = login(mConfig);

			return null;
		}

		SocksoSession login(final SocksoConfig config) {

			// TODO Implement the server later

			Parcelable authResponse = null;
			if (getIntent() != null && getIntent().getExtras() != null) {
				authResponse = getIntent().getExtras().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
			}

			// TODO remove
			String sessionKey = "ABCDEF0123456789";
			SocksoSession session = new SocksoSession(config.getUser(), sessionKey);
			
			SocksoAccountAuthenticator.addAccount(LoginActivity.this, config, authResponse);

			return session;
		}

		@Override
		public void onPostExecute(SocksoSession session) {
			
			mDialog.dismiss();
			mLoginButton.setEnabled(true);
			mLoginTask = null;

			if (session != null) {
				Log.i(TAG, "We have a SocksoSession!");
				// TODO SocksoApp.getInstance().session = session;
			} 
			else {
				Log.e(TAG, "No Session!");
			}
			
			finish();
		}
	}
}
