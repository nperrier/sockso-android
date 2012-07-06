package com.pugh.sockso.android.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pugh.sockso.android.activity.LoginActivity;

/**
 * This class is an implementation of AbstractAccountAuthenticator for
 * authenticating accounts in the com.example.android.samplesync domain.
 * 
 * The interesting thing that this class demonstrates is the use of authTokens
 * as part of the authentication process.
 * 
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
	private static final String PARAM_ACCOUNT_TYPE = "com.pugh.sockso.android.account";
	private static final String PARAM_AUTHTOKEN_TYPE = "com.pugh.sockso.android.AUTH_TOKEN";
	
	private Context mContext;

	public SocksoAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		Log.i(TAG, "Adding account");
		
		// call the activity to add a new account
		final Intent intent = new Intent(mContext, LoginActivity.class);
		
		intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);

		return bundle;
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

		// bad auth token type
		if (!authTokenType.equals(PARAM_AUTHTOKEN_TYPE)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
			return result;
		}

		final AccountManager accountManager = AccountManager.get(mContext);
		final String password = accountManager.getPassword(account);

		// checks if the account is validt and get auth token
		if (password != null) {
			boolean verified = true;
			
			//callSomeLoginServiceThatReturnsTrueIfValid(account.name, password);
			String authToken = "ABCDEF0123456789";
			
			if (verified) {
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);
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
