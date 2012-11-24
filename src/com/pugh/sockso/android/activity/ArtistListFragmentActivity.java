package com.pugh.sockso.android.activity;

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
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.ArtistColumns;

public class ArtistListFragmentActivity extends FragmentActivity {

    private static final String TAG = ArtistListFragmentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            ArtistListFragment list = new ArtistListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class ArtistViewHolder {

        TextView artist;
        ImageView cover;
    }

    // Custom list view item (cover image | artist text)
    public static class ArtistCursorAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private int mLayout;
        CoverArtFetcher mCoverFetcher;


        public ArtistCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);
            
            this.mContext = context;
            this.mLayout = layout;
            this.mCoverFetcher = new CoverArtFetcher(mContext);
            this.mCoverFetcher.setDimensions(115, 115);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(TAG, "newView() ran");

            final LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(mLayout, parent, false);

            ArtistViewHolder viewHolder = new ArtistViewHolder();

            viewHolder.artist = (TextView) view.findViewById(R.id.artist_name_id);
            viewHolder.cover = (ImageView) view.findViewById(R.id.artist_image_id);

            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView() ran");

            ArtistViewHolder viewHolder = (ArtistViewHolder) view.getTag();

            int artistIdCol = cursor.getColumnIndex(ArtistColumns.SERVER_ID);
            int artistId = cursor.getInt(artistIdCol);

            int artistNameCol = cursor.getColumnIndex(ArtistColumns.NAME);
            viewHolder.artist.setText(cursor.getString(artistNameCol));

            mCoverFetcher.loadCoverArtArtist(artistId, viewHolder.cover);
        }

        // @Override
        // TODO, this is for filtered searches
        // public Cursor runQueryOnBackgroundThread(CharSequence constraint) {}
    }

    public static class ArtistListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = ArtistListFragment.class.getSimpleName();

        private static final int ARTIST_LIST_LOADER = 0x01;

        private ArtistCursorAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            String[] uiBindFrom = { ArtistColumns.NAME };
            int[] uiBindTo = { R.id.artist_name_id };

            getLoaderManager().initLoader(ARTIST_LIST_LOADER, null, this);

            mAdapter = new ArtistCursorAdapter(getActivity().getApplicationContext(), R.layout.artist_list_item, null,
                    uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            setListAdapter(mAdapter);

            setEmptyText(getString(R.string.no_artists));
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            savedInstanceState.putString("bla", "Value1");
            super.onSaveInstanceState(savedInstanceState);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i(TAG, "onListItemClick(): Item clicked: " + id);
            
            Intent intent = new Intent(getActivity(), ArtistActivity.class);
            intent.putExtra("artist_id", id);
             
            startActivity(intent);
        }

        @Override 
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String[] projection = { ArtistColumns._ID, ArtistColumns.SERVER_ID, ArtistColumns.NAME };
            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + ArtistColumns.TABLE_NAME);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null, null);

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            mAdapter.swapCursor(null);
        }

    }
}