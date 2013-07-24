package com.pugh.sockso.android.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.pugh.sockso.android.Preferences;
import com.pugh.sockso.android.R;
import com.pugh.sockso.android.Config;
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

	private static final String TAG = "SocksoAccAuthenticator";    
	
	public static final String ACCOUNT_TYPE = "com.pugh.sockso.account";
	public static final String PARAM_AUTHTOKEN_TYPE = "com.pugh.sockso.android.AUTH_TOKEN";
    public static final String NEW_ACCOUNT = "com.pugh.sockso.android.NEW_ACCOUNT";
    

	// Dummy account when not using credentials to access server
	private static final String DUMMY_ACCOUNT = "Sockso";
	
	private final Context mContext;

	public SocksoAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	// Called from the "Add Account" page in the Accounts setting
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		Log.d(TAG, "Adding Sockso account");

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
			intent.setAction(LoginActivity.LOGIN_INTENT);
			intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			result.putParcelable(AccountManager.KEY_INTENT, intent);
		}
		
		return result;
	}

	// LoginTask calls this one
	public static void addAccount(Context context, Config config, Parcelable response) {
		Log.d(TAG, "Adding Sockso account explicitly");
		
		Bundle result = null;
		String username = config.getUser();
		String password = config.getPassword();
		
		if (username == null) {
		    username = DUMMY_ACCOUNT;
		}
		
		Account account = new Account(username, ACCOUNT_TYPE);
		AccountManager am = AccountManager.get(context);
		
		// TODO store a hashed password 
		// String hashedPassword = MD5.getInstance().hash(config.getPassword());
		
		Bundle accountExtras = new Bundle();            
		// This is a new account, so let the rest of the app know
        // Once the initial sync has completed, this value will be cleared:
		accountExtras.putString(NEW_ACCOUNT, Boolean.toString(true));
		
		if (am.addAccountExplicitly(account, password, accountExtras)) {
		    
			// Add the server and port number along with the account name and password:
			result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			
			// Set the server/port config associated with the account
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Editor editPrefs = prefs.edit();
            editPrefs.putString(Preferences.HOSTNAME, config.getHostname());
            editPrefs.putString(Preferences.PORT, Integer.toString(config.getPort()));
            editPrefs.commit();
			
			// Set sync enabled (if false, user must explicitly enable it through Account settings)
			ContentResolver.setSyncAutomatically(account, SocksoProvider.AUTHORITY, true);
			
			// This tells Android to run the sync as soon as possible (otherwise it waits about 15-20 seconds
			// So for new accounts, we want to initialize the database immediately, otherwise the user
			// will think the application is broken :(
			// TODO consider using a dialog for the first sync that lets the user know that the sync is running
			Bundle syncExtras = new Bundle();
			syncExtras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			syncExtras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // TODO not sure what this does
			ContentResolver.requestSync(account, SocksoProvider.AUTHORITY, syncExtras);
		}
		
		AccountAuthenticatorResponse authResponse = (AccountAuthenticatorResponse) response;
		
		if(authResponse != null){
			authResponse.onResult(result);
		}
	}
	
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle options) throws NetworkErrorException {
		Log.d(TAG, "getAuthToken() ran");
		
		// Bad auth token type
		if ( ! authTokenType.equals(PARAM_AUTHTOKEN_TYPE) ) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid authTokenType");
			return result;
		}

		final AccountManager accountManager = AccountManager.get(mContext);
		final String password = accountManager.getPassword(account);
		final String username = account.name;

		// Not authenticating if dummy account
		if(DUMMY_ACCOUNT.equalsIgnoreCase(username)) {
		    return null;
		}
		
		// Checks if the account is valid and get auth token
		if (password != null) {
			boolean verified = true;
			
			//callSomeLoginServiceThatReturnsTrueIfValid(account.name, password);
			String authToken = "ABCDEF0123456789"; // TODO
			
			if (verified) {
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
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
	    return getSocksoAccount(context) != null ? true : false;
	}

	public static Account getSocksoAccount(Context context) {
	    
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);

        Log.d(TAG, "Found " + accounts.length + " Sockso accounts");
        if (accounts != null && accounts.length > 0) {
            return accounts[0];
        }
        
        return null;
	}
	
    /**
     * Checks the account to see if it is brand-spankin' new
     * 
     * @param account
     * @return boolean
     */
    public static boolean isNewAccount(Account account, Context context) {

        AccountManager am = AccountManager.get(context);
        String newAccount = am.getUserData(account, NEW_ACCOUNT);
        Log.d(TAG, "isNewAccount(): " + newAccount);

        if (!TextUtils.isEmpty(newAccount)) {
            return Boolean.parseBoolean(newAccount);
        }

        return true;
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
