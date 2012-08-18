package com.pugh.sockso.android.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.pugh.sockso.android.R;

public class SettingsActivity extends PreferenceActivity {

    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);

        addPreferencesFromResource(R.xml.sockso_preferences);

        // findPreference("scrobble").setOnPreferenceChangeListener(scrobbletoggle);
        // findPreference("changes").setOnPreferenceClickListener(urlClick);
    }

}
