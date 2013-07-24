package com.pugh.sockso.android.activity;

import android.accounts.Account;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.account.SocksoAccountAuthenticator;
import com.pugh.sockso.android.activity.TrackListFragmentActivity.TrackCursorAdapter;
import com.pugh.sockso.android.data.MusicManager;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.GenreColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;


public class TrackListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String TAG = TrackListFragment.class.getSimpleName();
    
    private static final int TRACK_LIST_LOADER = 1;    
    public static final String ACTION_FILTER_BY_GENRE = "com.pugh.sockso.android.activity.tracklist.FILTER_BY_GENRE";

    private TrackCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] uiBindFrom = { TrackColumns.NAME };
        int[] uiBindTo = { R.id.track_title_id };

        mAdapter = new TrackCursorAdapter(getActivity().getApplicationContext(), R.layout.track_list_item, null,
                uiBindFrom, uiBindTo, 0);

        setListAdapter(mAdapter);

        setEmptyText(getString(R.string.no_tracks));
        
        // Start out with a progress indicator
        setListShown(false);
        
        final Intent intent = getActivity().getIntent();        
        final String action = intent.getAction();
        Log.d(TAG, "intent action: " + action);
        Bundle args = new Bundle();;
        if (action != null) {
            long genreId = intent.getLongExtra(MusicManager.GENRE, -1L);
            args.putString("action", action);
            args.putLong(MusicManager.GENRE, genreId);
        }
        
        getLoaderManager().initLoader(TRACK_LIST_LOADER, args, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick(): Item clicked: " + id + ", position: " + position);
        
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.setAction(PlayerActivity.ACTION_PLAY_TRACK);
        intent.putExtra(MusicManager.TRACK, id);
        
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() ran");
        
        String[] projection = { TrackColumns._ID, TrackColumns.SERVER_ID, TrackColumns.NAME,
                TrackColumns.ARTIST_NAME, };

        Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + TrackColumns.TABLE_NAME);

        if (args != null) {
            String action = args.getString("action");
            if (ACTION_FILTER_BY_GENRE.equals(action)) {
                long genreId = args.getLong(MusicManager.GENRE);
                contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" 
                        + GenreColumns.TABLE_NAME + "/" + genreId +"/" + TrackColumns.TABLE_NAME);
            }
        }
        
        CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, 
                null, null, TrackColumns.FULL_NAME + " ASC");
        
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished: " + cursor.getCount());
        mAdapter.swapCursor(cursor);
        
        // Enable FastScrolling
        final ListView view = getListView();
        view.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);
        view.setFastScrollEnabled(true);
        
        Account account = SocksoAccountAuthenticator.getSocksoAccount(getActivity().getApplicationContext());

        if ( account != null ) {
            boolean isNewAccount = SocksoAccountAuthenticator.isNewAccount(account, getActivity().getApplicationContext());
            // Show the list once the initial sync finishes (indicated by setting isNewAccount = false)
            if ( ! isNewAccount ) {
                setListShown(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
