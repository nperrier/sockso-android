package com.pugh.sockso.android.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.pugh.sockso.android.R;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    
    public static final String KEY_LOGIN_REQUIRED_PREF = "login_required";
    public static final String KEY_USERNAME_PREF       = "username";
    public static final String KEY_PASSWORD_PREF       = "password";
    public static final String KEY_SERVER_PREF         = "server";
    public static final String KEY_PORT_PREF           = "port";

    private CheckBoxPreference mLoginRequired;
    private EditTextPreference mUsername;
    private EditTextPreference mPassword;
    private EditTextPreference mServer;
    private EditTextPreference mPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.sockso_preferences);

        mLoginRequired = (CheckBoxPreference) getPreferenceScreen().findPreference(KEY_LOGIN_REQUIRED_PREF);
        mUsername      = (EditTextPreference) getPreferenceScreen().findPreference(KEY_USERNAME_PREF);
        mPassword      = (EditTextPreference) getPreferenceScreen().findPreference(KEY_PASSWORD_PREF);
        mServer        = (EditTextPreference) getPreferenceScreen().findPreference(KEY_SERVER_PREF);
        mPort          = (EditTextPreference) getPreferenceScreen().findPreference(KEY_PORT_PREF);
        
        updateSummaries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged() ran: " + key);
        
        updateSummaries();
    }

    private void updateSummaries() {
        mLoginRequired.setSummary(getPreferenceScreen().getSharedPreferences()
                .getBoolean(KEY_LOGIN_REQUIRED_PREF, false) ? "Disable this setting" : "Enable this setting");
        mUsername.setSummary(mUsername.getText());
        mPassword.setSummary(mPassword.getText());
        mServer.setSummary(mServer.getText());
        mPort.setSummary(mPort.getText());
    }
}
