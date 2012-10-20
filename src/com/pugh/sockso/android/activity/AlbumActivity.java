package com.pugh.sockso.android.activity;

import android.content.ContentResolver;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.ServerFactory;
import com.pugh.sockso.android.SocksoServer;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;
import com.pugh.sockso.android.music.Album;

// Album details activity
// Shows album title, artist, track listing, cover art, etc
// for a single album
public class AlbumActivity extends FragmentActivity {

    private static final String TAG = AlbumActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.album_details);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it to the content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            TrackListFragment list = new TrackListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class TrackViewHolder {

        TextView trackNumber;
        TextView title;
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

            viewHolder.title = (TextView) view.findViewById(R.id.track_title_id);
            viewHolder.trackNumber = (TextView) view.findViewById(R.id.track_number_id);

            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView() ran");

            TrackViewHolder viewHolder = (TrackViewHolder) view.getTag();

            int trackTitleCol = cursor.getColumnIndex(TrackColumns.NAME);
            viewHolder.title.setText(cursor.getString(trackTitleCol));

            // TODO
            int trackNumberCol = cursor.getColumnIndex(TrackColumns.TRACK_NO);

            Log.d(TAG, "trackNumberCol: " + trackNumberCol);

            int trackNumber = cursor.getInt(trackNumberCol);

            Log.d(TAG, "trackNumber: " + trackNumber);

            viewHolder.trackNumber.setText(String.valueOf(trackNumber));
        }
    }

    public static class TrackListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = TrackListFragment.class.getSimpleName();
        private static final int TRACK_LIST_LOADER = 0x01;
        private TrackCursorAdapter mAdapter;
        private View mAlbumDetailsView;
        private long mAlbumId = -1;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d(TAG, "onCreate() called");

            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            mAlbumId = bundle.getLong("album_id", -1);

            super.onCreate(savedInstanceState);
        }

        // TODO: Have the MusicManager handle this?
        private Album getAlbum(long albumId) {
            Log.d(TAG, "getAlbumInfo() called");

            Album album = null;

            String[] projection = { AlbumColumns.SERVER_ID, AlbumColumns.ARTIST_NAME, AlbumColumns.NAME };
            Uri uri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + AlbumColumns.TABLE_NAME + "/" + albumId);

            ContentResolver cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(uri, projection, null, null, null);

            Log.d(TAG, "col count: " + cursor.getColumnCount());
            Log.d(TAG, "column_name[0]: " + cursor.getColumnName(0));
            Log.d(TAG, "row count: " + cursor.getCount());

            cursor.moveToNext();

            long serverAlbumId = cursor.getLong(0);
            String artistName = cursor.getString(1);
            String trackName = cursor.getString(2);

            cursor.close();
            Log.d(TAG, "serverAlbumId: " + serverAlbumId);

            album = new Album();
            album.setId(albumId);
            album.setServerId(serverAlbumId);
            album.setName(trackName);
            album.setArtist(artistName);

            return album;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView() called");

            mAlbumDetailsView = inflater.inflate(R.layout.album_details_header, null, false);

            Album album = getAlbum(mAlbumId);

            TextView artistText = (TextView) mAlbumDetailsView.findViewById(R.id.album_artist_id);
            artistText.setText(album.getArtist());

            TextView titleText = (TextView) mAlbumDetailsView.findViewById(R.id.album_title_id);
            titleText.setText(album.getName());

            // Album Cover 
            ImageView albumCover = (ImageView) mAlbumDetailsView.findViewById(R.id.album_image_id);
            SocksoServer server = ServerFactory.getServer(getActivity());
            CoverArtFetcher coverFetcher = new CoverArtFetcher(server);

            coverFetcher.download("al" + album.getServerId(), albumCover);

            ImageButton playButton = (ImageButton) mAlbumDetailsView.findViewById(R.id.play_album_button);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "view: " + view.getClass());
                    
                    // Call PlayerActivity with the album tracks set
                    
                }
            });

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated() called");
            super.onActivityCreated(savedInstanceState);

            // Add the album details to the list header:
            ListView listView = getListView();
            listView.addHeaderView(mAlbumDetailsView, null, false);
            // https://groups.google.com/forum/?fromgroups=#!topic/android-developers/DfZ8u_ORrPA
            listView.setCacheColorHint(0);

            String[] uiBindFrom = { TrackColumns.TRACK_NO, TrackColumns.NAME };
            int[] uiBindTo = { R.id.track_number_id, R.id.track_title_id };

            getLoaderManager().initLoader(TRACK_LIST_LOADER, null, this);

            mAdapter = new TrackCursorAdapter(getActivity().getApplicationContext(), R.layout.album_track_list_item,
                    null, uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            setListAdapter(mAdapter);

            setEmptyText(getString(R.string.no_tracks));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {

            Log.i(TAG, "onListItemClick(): Item clicked: " + id + ", position: " + position);

            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.setAction(PlayerActivity.ACTION_PLAY);
            intent.putExtra("track_id", id);

            startActivity(intent);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String[] projection = { TrackColumns._ID, TrackColumns.TRACK_NO, TrackColumns.NAME };

            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + AlbumColumns.TABLE_NAME + "/" + mAlbumId
                    + "/" + TrackColumns.TABLE_NAME);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null,
                    TrackColumns.TRACK_NO + " ASC");

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
