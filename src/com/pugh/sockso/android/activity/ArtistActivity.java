package com.pugh.sockso.android.activity;

import android.app.SearchManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.CoverArtFetcher;
import com.pugh.sockso.android.data.MusicManager;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.ArtistColumns;
import com.pugh.sockso.android.music.Artist;

public class ArtistActivity extends FragmentActivity {

    private static final String TAG = ArtistActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.artist_details);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it to the content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            AlbumListFragment list = new AlbumListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class AlbumViewHolder {

        TextView title;
        ImageView cover;
        public TextView trackCount;
    }

    // Custom list view item (cover image | artist/album text)
    public static class AlbumCursorAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private int mLayout;
        private CoverArtFetcher mCoverFetcher;


        public AlbumCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
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

            AlbumViewHolder viewHolder = new AlbumViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.album_title_id);
            viewHolder.trackCount = (TextView) view.findViewById(R.id.album_track_count_id);
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

            int albumTrackCountCol = cursor.getColumnIndex(AlbumColumns.TRACK_COUNT);
            viewHolder.trackCount.setText(cursor.getString(albumTrackCountCol) + " " + mContext.getString(R.string.track_count));
            
            viewHolder.cover = (ImageView) view.findViewById(R.id.album_image_id);
            
            mCoverFetcher.loadCoverArtAlbum(albumId, viewHolder.cover);
        }
    }

    public static class AlbumListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = AlbumListFragment.class.getSimpleName();
        private static final int ALBUM_LIST_LOADER = 1;
        private AlbumCursorAdapter mAdapter;
        private View mArtistDetailsView;
        private long mArtistId = -1;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d(TAG, "onCreate() called");

            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            mArtistId = bundle.getLong(MusicManager.ARTIST, -1);
            
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView() called");

            mArtistDetailsView = inflater.inflate(R.layout.artist_details_header, null, false);
            
            Artist artist = MusicManager.getArtist(getActivity().getContentResolver() , mArtistId);

            TextView titleText = (TextView) mArtistDetailsView.findViewById(R.id.artist_title_id);
            titleText.setText(artist.getName());

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated() called");
            super.onActivityCreated(savedInstanceState);

            // Add the album details to the list header:
            ListView listView = getListView();
            listView.addHeaderView(mArtistDetailsView, null, false);
            // https://groups.google.com/forum/?fromgroups=#!topic/android-developers/DfZ8u_ORrPA
            listView.setCacheColorHint(0);

            String[] uiBindFrom = { AlbumColumns.NAME, AlbumColumns.TRACK_COUNT };
            int[] uiBindTo = { R.id.album_title_id, R.id.album_track_count_id };

            getLoaderManager().initLoader(ALBUM_LIST_LOADER, null, this);

            mAdapter = new AlbumCursorAdapter(getActivity().getApplicationContext(), R.layout.artist_album_list_item,
                    null, uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            setListAdapter(mAdapter);

            setEmptyText(getString(R.string.no_albums));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i(TAG, "onListItemClick(): Item clicked: " + id + ", position: " + position);

            // TODO This should goto the album details view
            // but also have a button to play it
            
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            intent.putExtra(MusicManager.ALBUM, id);
             
            startActivity(intent);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String[] projection = { AlbumColumns._ID, AlbumColumns.SERVER_ID, AlbumColumns.NAME, AlbumColumns.TRACK_COUNT };

            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + ArtistColumns.TABLE_NAME + "/" + mArtistId
                    + "/" + AlbumColumns.TABLE_NAME);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null,
                    AlbumColumns.FULL_NAME + " ASC");

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

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

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
