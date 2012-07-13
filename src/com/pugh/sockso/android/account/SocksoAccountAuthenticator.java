package com.pugh.sockso.android.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.SocksoConfig;
import com.pugh.sockso.android.activity.AccountFailActivity;
import com.pugh.sockso.android.activity.LoginActivity;
import com.pugh.sockso.android.data.SocksoProvider;

/**
 * In the account setup UI, the user enters their username and password.
 * 
 * But for our subsequent calls off to the service for syncing, we want to use
 * an authtoken instead - so we're not continually sending the password over the
 * wire.
 * 
 * getAuthToken() will be called when SyncAdapter calls
 * AccountManager.blockingGetAuthToken().
 * 
 * When we get called, we need to return the appropriate authToken for the
 * specified account.
 * 
 * If we already have an authToken stored in the account, we return that
 * authToken.
 * 
 * If we don't, but we do have a username and password, then we'll attempt to
 * talk to the sample service to fetch an authToken.
 * 
 * If that fails (or we didn't have a username/password), then we need to prompt
 * the user - so we create an AuthenticatorActivity intent and return that.
 * 
 * That will display the dialog that prompts the user for their login
 * information.
 */
public class SocksoAccountAuthenticator extends AbstractAccountAuthenticator {

	private static final String TAG = SocksoAccountAuthenticator.class.getName();
	private static final String PARAM_AUTHTOKEN_TYPE = "com.pugh.sockso.android.AUTH_TOKEN";
	
	private Context mContext;

	public SocksoAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	// Called from the "Add Account" page in the Accounts setting
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		Log.i(TAG, "Adding Sockso account");

		Bundle result = new Bundle();
		
		if(hasSocksoAccount(mContext)) { 
			// Hey! We already have an account
			Intent intent = new Intent(mContext, AccountFailActivity.class);
			intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			result.putParcelable(AccountManager.KEY_INTENT, intent);
		}
		else {
			// New accounts redirect to LoginActivity with Intent to create a new account
			Intent intent = new Intent(mContext, LoginActivity.class);
			intent.setAction("com.pugh.sockso.android.activity.LOGIN");
			intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			result.putParcelable(AccountManager.KEY_INTENT, intent);			
		}			
		
		return result;
	}

	// LoginTask calls this one
	public static void addAccount(Context context, SocksoConfig config, Parcelable response) {
		
		Log.i(TAG, "Adding Sockso account explicitly");
		
		Bundle result = null;
		
		Account account = new Account(config.getUser(), context.getString(R.string.ACCOUNT_TYPE));
		AccountManager am = AccountManager.get(context);
		// TODO store a hashed password 
		// String hashedPassword = MD5.getInstance().hash(config.getPassword());
		
	    final Bundle extraData = new Bundle();
	    extraData.putString("server", config.getServer());
	    extraData.putString("port", Integer.valueOf(config.getPort()).toString() ); // I wish I could just use putInt()...
	    
		if (am.addAccountExplicitly(account, config.getPassword(), extraData)) {
			result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
		}
		
		AccountAuthenticatorResponse authResponse = (AccountAuthenticatorResponse) response;
		
		if(authResponse != null){
			authResponse.onResult(result);
		}
		
	}
	
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle options) throws NetworkErrorException {
		Log.i(TAG, "getAuthToken() ran");
		
		// bad auth token type
		if (!authTokenType.equals(PARAM_AUTHTOKEN_TYPE)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
			return result;
		}

		final AccountManager accountManager = AccountManager.get(mContext);
		final String password = accountManager.getPassword(account);

		// checks if the account is valid and get auth token
		if (password != null) {
			boolean verified = true;
			
			//callSomeLoginServiceThatReturnsTrueIfValid(account.name, password);
			String authToken = "ABCDEF0123456789"; // TODO
			
			if (verified) {
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putLong(AccountManager.KEY_ACCOUNT_TYPE, R.string.ACCOUNT_TYPE);
				result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
				return result;
			}
		}
		// Password is missing or incorrect.
		// Start the activity to add the missing data.
		final Intent intent = new Intent(mContext, LoginActivity.class);

		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);

		return bundle;
	}

	public static Boolean hasSocksoAccount(Context context) {
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
		if(accounts != null && accounts.length > 0)
			return true;
		else
			return false;
	}
	
	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

}
