package com.pugh.sockso.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;


public class ServerFactory {

    private static final String TAG = ServerFactory.class.getSimpleName();

    private ServerFactory(){}
    
    public static SocksoServer getServer(final Context context){
        
        SocksoServer server = null;
                
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
        
        if(accounts != null && accounts.length > 0) {
            
            Account account = accounts[0];
  
            // move to shared preferences:
            String host = accountManager.getUserData(account, "server");
            int    port   = Integer.valueOf(accountManager.getUserData(account, "port"));
        
            //SharedPreferences settings = getSharedPreferences(Preferences.FILE, 0);
            //String user = settings.getString("server", "");
            //int port = settings.getString("port", "");
        
            // Consider putting all this stuff in the SocksoApp global class
            //Config config = new Config(server, port);
            server = new SocksoServerImpl(host, port);
        }
        
        return server;
    }
    
}
