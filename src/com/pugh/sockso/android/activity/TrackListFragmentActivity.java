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
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;

public class TrackListFragmentActivity extends FragmentActivity {

    private static final String TAG = TrackListFragmentActivity.class.getSimpleName();

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

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class TrackViewHolder {

        TextView artist;
        TextView title;
        ImageView cover;
    }

    // Custom list view item (cover image | artist/album text)
    public static class TrackCursorAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private int mLayout;

        public TrackCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);
            this.mContext = context;
            this.mLayout = layout;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(TAG, "newView() ran");

            final LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(mLayout, parent, false);

            TrackViewHolder viewHolder = new TrackViewHolder();

            viewHolder.artist = (TextView) view.findViewById(R.id.track_artist_id);
            viewHolder.title = (TextView) view.findViewById(R.id.track_title_id);
            viewHolder.cover = (ImageView) view.findViewById(R.id.track_image_id);

            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView() ran");

            TrackViewHolder viewHolder = (TrackViewHolder) view.getTag();

            int trackIdCol = cursor.getColumnIndex(TrackColumns.SERVER_ID);
            int trackId = cursor.getInt(trackIdCol);

            int trackTitleCol = cursor.getColumnIndex(TrackColumns.NAME);
            viewHolder.title.setText(cursor.getString(trackTitleCol));

            int trackArtistCol = cursor.getColumnIndex(TrackColumns.ARTIST_NAME);
            viewHolder.artist.setText(cursor.getString(trackArtistCol));

            SocksoServer server = ServerFactory.getServer(mContext);
            CoverArtFetcher coverFetcher = new CoverArtFetcher(server);
            // TODO REMOVE & REPLACE
            coverFetcher.download("tr" + trackId, viewHolder.cover);
        }

        // @Override
        // TODO, this is for filtered searches
        // public Cursor runQueryOnBackgroundThread(CharSequence constraint) {}
    }

    public static class TrackListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = TrackListFragment.class.getSimpleName();

        private static final int TRACK_LIST_LOADER = 0x01;

        private TrackCursorAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            String[] uiBindFrom = { TrackColumns.NAME };
            int[] uiBindTo = { R.id.track_title_id };

            getLoaderManager().initLoader(TRACK_LIST_LOADER, null, this);

            mAdapter = new TrackCursorAdapter(getActivity().getApplicationContext(), R.layout.track_list_item, null,
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
            Log.i(TAG, "onListItemClick(): Item clicked: " + id + ", position: " + position);
            
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.setAction(PlayerActivity.ACTION_PLAY);
            intent.putExtra("track_id", id);
            
            startActivity(intent);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String[] projection = { TrackColumns._ID, TrackColumns.SERVER_ID, TrackColumns.NAME,
                    TrackColumns.ARTIST_NAME, };

            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + TrackColumns.TABLE_NAME);
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