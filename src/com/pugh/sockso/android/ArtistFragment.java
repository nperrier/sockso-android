package com.pugh.sockso.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ArtistFragment extends ListFragment {

	private final static String SOCKSO_TAG = "[sockso]";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

	@Override
	public void onPause() {
		// This will be executed when the screen sleeps, and for pretty much
		// every other actions that is run when this activity isn't in focus
		Log.i(SOCKSO_TAG, "\"onPause\" ran");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i(SOCKSO_TAG, "\"onResume\" ran");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.i(SOCKSO_TAG, "\"onStart\" ran");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.i(SOCKSO_TAG, "\"onStop\" ran");
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("bla", "Value1");
		super.onSaveInstanceState(savedInstanceState);
	}
}