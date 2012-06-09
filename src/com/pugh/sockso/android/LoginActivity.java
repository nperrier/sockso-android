package com.pugh.sockso.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.pugh.sockso.android.activity.TabControllerActivity;

public class LoginActivity extends Activity {

	private final static String TAG = LoginActivity.class.getName();
	
	private boolean hasSession = false;
	private ProgressDialog loginDialog;
	private Handler failedLoginHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "\"onCreate\" ran");
		super.onCreate(savedInstanceState);
		
		failedLoginHandler = new Handler();
		
		setContentView(R.layout.login);
		/*
		// TODO show progress dialog and login to server to get session
		Preferences preferences = new Preferences(getApplicationContext());
		
		if (preferences.isServerConfigured() && preferences.isLoginConfigured()) {
			if (!hasSession) {
				// get stored server config and connect to server
				login(preferences.getConfig());
			} else {
				Log.i(TAG, "Have session");
			}
			launchSocksoActivity();
		}
		*/
	}

	private void launchSocksoActivity() {
		try {
			// TODO Not sure if the intent needs these args or not...
			Intent intent = new Intent(this.getApplicationContext(),
					TabControllerActivity.class);
			Log.i(TAG, "Starting TabControllerActivity...");
			startActivity(intent);
			
		} catch (Exception e) {
			Log.i(TAG, "Failed to launch TabControllerActivity Activity "
					+ e.getMessage());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "\"onRestoreInstanceState\" ran");
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void onClickConnect(View view) {
		Log.i(TAG, "\"Connect\" button clicked!");

		EditText server = (EditText) findViewById(R.id.server_address);
		Log.i(TAG, "\"Server text entered is \"" + server.getText() + "\"");

		EditText user = (EditText) findViewById(R.id.login_user);
		Log.i(TAG, "\"User text entered is \"" + user.getText() + "\"");

		EditText password = (EditText) findViewById(R.id.login_password);
		Log.i(TAG, "\"Password text entered is \"" + password.getText() + "\"");

		Config config = new Config(server.getText().toString(), user.getText()
				.toString(), password.getText().toString());
		// store the config for future use
		//Preferences preferences = new Preferences(getApplicationContext());
		//preferences.saveConfig(config);

		login(config);
	}

	protected void login(final Config config) {

		Log.i(TAG, "Starting dialog...");
		loginDialog = ProgressDialog.show(LoginActivity.this, "",
				"Logging in to Sockso...", true);

		new Thread(new Runnable() {
			public void run() {
				
				boolean connected = connectToServer(config);
				Log.i(TAG, "Dismissing dialog...");
				loginDialog.dismiss();
				
				if(connected){
					launchSocksoActivity();
				}
				else {
					Log.w(TAG, "Failed to login");
					failedLoginHandler.post(new Runnable() { // This thread runs in the UI
	                    @Override
	                    public void run() {
	                        showAlert();
	                    }
	                });
				}
			}
		}).start();
	}

	protected void showAlert(){
	    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this);
	    dlgAlert.setMessage("Failed to login");
	    dlgAlert.setTitle("Sockso");
	    dlgAlert.setPositiveButton("OK", null);
	    dlgAlert.setCancelable(true);
	    dlgAlert.create().show();
	}
	
	protected boolean connectToServer(final Config config) {
		return true;
	}

}
