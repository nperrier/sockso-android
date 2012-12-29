package com.pugh.sockso.android.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.pugh.sockso.android.Config;
import com.pugh.sockso.android.R;
import com.pugh.sockso.android.Session;
import com.pugh.sockso.android.account.SocksoAccountAuthenticator;

public class LoginActivity extends Activity {

	private final static String TAG = LoginActivity.class.getSimpleName();

	public static final String LOGIN_INTENT    = "com.pugh.sockso.android.activity.LOGIN";

	// TODO Unused currently
    public static final String SOCKSO_SESS_KEY = "session_key";
	
	private LoginTask mLoginTask;
	private Button    mLoginButton;
	private CheckBox  mLoginRequiredCheckBox;
	private ViewGroup mCredentialsView;
	private View      mCredentialsLayout;
	private EditText  mEditUser;
	private EditText  mEditPass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate() ran");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		mLoginButton = (Button) findViewById(R.id.login_button);
		mLoginRequiredCheckBox = (CheckBox) findViewById(R.id.login_required);
		
        mCredentialsView = (ViewGroup) findViewById(R.id.credentials);
        
        mCredentialsLayout = getLayoutInflater().inflate(R.layout.login_credentials, null);
        
        mEditUser = (EditText) mCredentialsLayout.findViewById(R.id.login_user);
        mEditPass = (EditText) mCredentialsLayout.findViewById(R.id.login_password);
        
		mLoginButton.setOnClickListener(new View.OnClickListener() {
		    
		    @Override
			public void onClick(View view) {
		        doLogin();
			}
		});
		
		mLoginRequiredCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged() ran");
                
                if (isChecked) {
                    mCredentialsView.addView(mCredentialsLayout);
                }
                else {
                    mCredentialsView.removeView(mCredentialsLayout);
                }
            }
		});
		
	}
	
	private void doLogin() {

        // If already logging in, pushing the button again should do nothing
        if (mLoginTask != null) {
            return;
        }

        EditText editServer = (EditText) findViewById(R.id.server_address);
        String server = editServer.getText().toString();

        EditText editPort = (EditText) findViewById(R.id.port_number);
        
        int port;
        try {
            port = Integer.parseInt(editPort.getText().toString());         
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "Port number was not a number");
            return;
        }
        
        Config config = new Config(server, port);

        // Username/Password is optional
        if (mLoginRequiredCheckBox.isChecked()) {

            String username = mEditUser.getText().toString();
            String password = mEditPass.getText().toString();

            // TODO improve validation
            if (username.length() == 0 || password.length() == 0 || server.length() == 0) {
                Log.e(TAG, "Username or Password was blank");
                return;
            }
            config.setUser(username);
            config.setPassword(password);
        }
        
        mLoginTask = new LoginTask(this);
        mLoginTask.execute(config);
	}

	private class LoginTask extends AsyncTask<Config, Void, Session> {

		ProgressDialog mDialog;
		
		LoginTask(Context context) {
			mLoginButton.setEnabled(false);

			mDialog = ProgressDialog.show(context, "", getString(R.string.login_authenticating), true, false);
			mDialog.setCancelable(true);
		}

		@Override
		public Session doInBackground(Config... configs) {
		    
		    // TODO
			Config config = configs[0];
			
			Session session = login(config);

			return null;
		}

		Session login(final Config config) {

			// TODO Implement the server later
		    
			Parcelable authResponse = null;
			if (getIntent() != null && getIntent().getExtras() != null) {
				authResponse = getIntent().getExtras().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
			}

			// TODO remove
			String sessionKey = "ABCDEF0123456789";
			Session session = new Session(config.getUser(), sessionKey);
			
			SocksoAccountAuthenticator.addAccount(LoginActivity.this, config, authResponse);

			return session;
		}

		@Override
		public void onPostExecute(Session session) {
			
			mDialog.dismiss();
			mLoginButton.setEnabled(true);
			mLoginTask = null;

			if (session != null) {
				Log.i(TAG, "We have a Session!");
				// TODO SocksoApp.getInstance().session = session;
			} 
			else {
				Log.e(TAG, "No Session!");
			}
			
			finish();
		}
	}
}
