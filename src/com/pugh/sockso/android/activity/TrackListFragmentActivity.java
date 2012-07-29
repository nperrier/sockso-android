package com.pugh.sockso.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.SocksoProvider;

public class TrackListFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			TrackListFragment list = new TrackListFragment();
			fm.beginTransaction().add(android.R.id.content, list).commit();
		}
	}

	public static class TrackListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

		private final static String TAG = TrackListFragment.class.getSimpleName();

		private static final int TRACK_LIST_LOADER = 0x01;

		private SimpleCursorAdapter mAdapter;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			String[] uiBindFrom = { SocksoProvider.Track.Columns.NAME };
			int[] uiBindTo = { R.id.track_title_id };

			getLoaderManager().initLoader(TRACK_LIST_LOADER, null, this);

			mAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.track_list_item, null,
					uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

			setListAdapter(mAdapter);

			setEmptyText(getString(R.string.no_tracks));
		}

		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
			savedInstanceState.putString("bla", "Value1");
			super.onSaveInstanceState(savedInstanceState);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "onListItemClick(): Item clicked: " + id);
            
            // Start the PlayerActivity and send it the id
            // of the track which the player activity will retrieve
            // from the content provider and send to the player service
            
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("track_id", id);
            
            //getActivity().startActivity(intent);
            startActivity(intent);	
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Log.i(TAG, "onCreateLoader() ran");

			String[] projection = { SocksoProvider.Track.Columns._ID, SocksoProvider.Track.Columns.NAME };
			Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + SocksoProvider.Track.TABLE_NAME);
			CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null, null);

			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			mAdapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			mAdapter.swapCursor(null);
		}

	}
}