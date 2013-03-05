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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.MusicManager;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;
import com.pugh.sockso.android.music.Album;


public class PlayerPlaylistActivity extends FragmentActivity {

    private static final String TAG = PlayerPlaylistActivity.class.getSimpleName();

    
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

            int trackNumberCol = cursor.getColumnIndex(TrackColumns.TRACK_NO);
            int trackNumber = cursor.getInt(trackNumberCol);

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
            mAlbumId = bundle.getLong(MusicManager.ALBUM, -1);

            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView() called");

            mAlbumDetailsView = inflater.inflate(R.layout.album_details_header, null, false);

            Album album = MusicManager.getAlbum(getActivity().getContentResolver(), mAlbumId);

            TextView artistText = (TextView) mAlbumDetailsView.findViewById(R.id.album_artist_id);
            artistText.setText(album.getArtist());

            TextView titleText = (TextView) mAlbumDetailsView.findViewById(R.id.album_title_id);
            titleText.setText(album.getName());

            // Album Cover
            ImageView albumCover = (ImageView) mAlbumDetailsView.findViewById(R.id.album_image_id);

            CoverArtFetcher coverFetcher = new CoverArtFetcher(getActivity());
            coverFetcher.setDimensions(150, 150);
            coverFetcher.loadCoverArtAlbum(album.getServerId(), albumCover);

            ImageButton playButton = (ImageButton) mAlbumDetailsView.findViewById(R.id.play_album_button);

            playButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Call PlayerActivity with the album tracks set
                    Log.i(TAG, "playButton clicked: " + view);

                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.setAction(PlayerActivity.ACTION_PLAY_ALBUM);
                    intent.putExtra(MusicManager.ALBUM, mAlbumId);

                    startActivity(intent);
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
            // Play selected album, starting from track position
            intent.setAction(PlayerActivity.ACTION_PLAY_ALBUM);
            intent.putExtra(MusicManager.ALBUM, mAlbumId);
            intent.putExtra("track_position", position);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

        case R.id.menu_item_library:

            intent = new Intent(this, TabControllerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            break;
        case R.id.menu_item_player:

            intent = new Intent(this, PlayerActivity.class);
            intent.setAction(PlayerActivity.ACTION_VIEW_PLAYER);
            startActivity(intent);

            break;
        case R.id.menu_item_settings:

            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            break;

        default:
            // No-op
            break;
        }

        return true;
    }

}
