package com.pugh.sockso.android.activity;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.widget.Toast;

public class AccountFailActivity extends AccountAuthenticatorActivity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Toast.makeText(this, "You can only sync one account!", Toast.LENGTH_LONG).show();
		finish();
	}
}
