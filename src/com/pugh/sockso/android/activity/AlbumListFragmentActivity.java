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
import com.pugh.sockso.android.ServerFactory;
import com.pugh.sockso.android.SocksoServer;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;

public class AlbumListFragmentActivity extends FragmentActivity {

    private static final String TAG = AlbumListFragmentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            AlbumListFragment list = new AlbumListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class AlbumViewHolder {

        TextView artist;
        TextView title;
        ImageView cover;
    }

    // Custom list view item (cover image | artist/album text)
    public static class AlbumCursorAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private int mLayout;

        public AlbumCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);
            this.mContext = context;
            this.mLayout = layout;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(TAG, "newView() ran");

            final LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(mLayout, parent, false);

            AlbumViewHolder viewHolder = new AlbumViewHolder();

            viewHolder.artist = (TextView) view.findViewById(R.id.album_artist_id);
            viewHolder.title = (TextView) view.findViewById(R.id.album_title_id);
            viewHolder.cover = (ImageView) view.findViewById(R.id.album_image_id);

            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView() ran");

            AlbumViewHolder viewHolder = (AlbumViewHolder) view.getTag();

            int albumIdCol = cursor.getColumnIndex(AlbumColumns.SERVER_ID);
            int albumId = cursor.getInt(albumIdCol);

            int albumTitleCol = cursor.getColumnIndex(AlbumColumns.NAME);
            viewHolder.title.setText(cursor.getString(albumTitleCol));

            int albumArtistCol = cursor.getColumnIndex(AlbumColumns.ARTIST_NAME);
            viewHolder.artist.setText(cursor.getString(albumArtistCol));

            SocksoServer server = ServerFactory.getServer(mContext);
            CoverArtFetcher coverFetcher = new CoverArtFetcher(server);
            // TODO REMOVE & REPLACE
            coverFetcher.download("al" + albumId, viewHolder.cover);
        }

        // @Override
        // TODO, this is for filtered searches
        // public Cursor runQueryOnBackgroundThread(CharSequence constraint) {}
    }

    public static class AlbumListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = AlbumListFragment.class.getSimpleName();

        private static final int ALBUM_LIST_LOADER = 0x01;

        private AlbumCursorAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            String[] uiBindFrom = { AlbumColumns.NAME };
            int[] uiBindTo = { R.id.album_title_id };

            getLoaderManager().initLoader(ALBUM_LIST_LOADER, null, this);

            mAdapter = new AlbumCursorAdapter(getActivity().getApplicationContext(), R.layout.album_list_item, null,
                    uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            setListAdapter(mAdapter);

            setEmptyText(getString(R.string.no_albums));
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            savedInstanceState.putString("bla", "Value1");
            super.onSaveInstanceState(savedInstanceState);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            // TODO Insert desired behavior here.
            Log.i(TAG, "onListItemClick(): Item clicked: " + id);
            
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            intent.putExtra("album_id", id);
            
            startActivity(intent);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String[] projection = { AlbumColumns._ID, AlbumColumns.SERVER_ID, AlbumColumns.NAME,
                    AlbumColumns.ARTIST_NAME };
            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + AlbumColumns.TABLE_NAME);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null, null);

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "onLoadFinished() ran");
            mAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }

    }
}