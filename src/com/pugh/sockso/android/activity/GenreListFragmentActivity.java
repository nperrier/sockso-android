package com.pugh.sockso.android.activity;

import android.accounts.Account;
import android.content.Context;
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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.account.SocksoAccountAuthenticator;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.MusicManager;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.GenreColumns;

public class GenreListFragmentActivity extends FragmentActivity {

    private static final String TAG = GenreListFragmentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            GenreListFragment list = new GenreListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class GenreViewHolder {

        TextView genre;
        ImageView cover;
    }

    // Custom list view item (cover image | artist/album text)
    public static class GenreCursorAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private int mLayout;
        //CoverArtFetcher mCoverFetcher;

        public GenreCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);
            this.mContext = context;
            this.mLayout = layout;

            // TODO
            //this.mCoverFetcher = new CoverArtFetcher(mContext);
            //this.mCoverFetcher.setDimensions(115, 115);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(TAG, "newView() ran");

            final LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(mLayout, parent, false);

            GenreViewHolder viewHolder = new GenreViewHolder();

            viewHolder.genre = (TextView) view.findViewById(R.id.genre_name_id);
            viewHolder.cover = (ImageView) view.findViewById(R.id.genre_image_id);

            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView() ran");

            GenreViewHolder viewHolder = (GenreViewHolder) view.getTag();

            int genreNameCol = cursor.getColumnIndex(GenreColumns.NAME);
            viewHolder.genre.setText(cursor.getString(genreNameCol));

            // TODO
            //mCoverFetcher.loadCoverArtTrack(trackId, viewHolder.cover);
        }

        // @Override
        // TODO, this is for filtered searches
        // public Cursor runQueryOnBackgroundThread(CharSequence constraint) {}
    }

    public static class GenreListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = GenreListFragment.class.getSimpleName();

        private static final int GENRE_LIST_LOADER = 1;

        private GenreCursorAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            String[] uiBindFrom = { GenreColumns.NAME };
            int[] uiBindTo = { R.id.genre_name_id };

            mAdapter = new GenreCursorAdapter(getActivity().getApplicationContext(), R.layout.genre_list_item, null,
                    uiBindFrom, uiBindTo, 0);

            setListAdapter(mAdapter);

            setEmptyText(getString(R.string.no_genres));
            
            // Start out with a progress indicator
            setListShown(false);
            
            getLoaderManager().initLoader(GENRE_LIST_LOADER, null, this);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i(TAG, "onListItemClick(): Item clicked: " + id + ", position: " + position);
            
            Intent intent = new Intent(getActivity(), TrackListFragmentActivity.class);
            intent.setAction(TrackListFragment.ACTION_FILTER_BY_GENRE);
            intent.putExtra(MusicManager.GENRE, id);
            
            startActivity(intent);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String[] projection = { GenreColumns._ID, GenreColumns.SERVER_ID, GenreColumns.NAME };

            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + GenreColumns.TABLE_NAME);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null, 
                    GenreColumns.FULL_NAME + " ASC");

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "onLoadFinished: " + cursor.getCount());
            mAdapter.swapCursor(cursor);

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
}