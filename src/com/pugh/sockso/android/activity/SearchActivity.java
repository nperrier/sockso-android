package com.pugh.sockso.android.activity;

import java.util.Map;
import java.util.TreeMap;

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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.MusicManager;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.data.SocksoProvider.AlbumColumns;
import com.pugh.sockso.android.data.SocksoProvider.ArtistColumns;
import com.pugh.sockso.android.data.SocksoProvider.SearchColumns;
import com.pugh.sockso.android.data.SocksoProvider.TrackColumns;
import com.pugh.sockso.android.widget.MusicItemIndexer;

public class SearchActivity extends FragmentActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private SearchListFragment mlistFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            mlistFrag = new SearchListFragment();
            fm.beginTransaction().add(android.R.id.content, mlistFrag).commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent() called");

        super.onNewIntent(intent);
        setIntent(intent);
        mlistFrag.handleIntent(intent);
    }

    // Utility class to store the View ID's retrieved from the layout only once for efficiency
    static class ViewHolder {

        TextView name;
    }

    public static class SearchCursorAdapter extends SimpleCursorAdapter implements SectionIndexer {

        private int mLayout;
        private LayoutInflater mInflater;
        private MusicItemIndexer mIndexer;

        private int[] mUsedSectionNumbers = null; // List of sections "used" sections (ones with 1 or more list items)
        private SparseIntArray mSectionToOffset; // The number to offset the section by in the list
        private Map<Integer, Integer> mSectionToPosition; // The section to list position mapping
    
        // Types of list items (section separators and search result items)
        private final static int SEARCH_RESULT_VIEW = 0;
        private final static int SEARCH_SEPARATOR_VIEW = 1;
        private final static int VIEW_TYPE_COUNT = 2;

        // The sections (order is important!)
        private final static String[] SECTIONS = { 
            ArtistColumns.MIME_TYPE, 
            AlbumColumns.MIME_TYPE,
            TrackColumns.MIME_TYPE 
            };

        
        public SearchCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);

            this.mLayout = layout;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView() ran: " + position);

            final int type = getItemViewType(position);
            
            // Section separators are inserted here
            if (type == SEARCH_SEPARATOR_VIEW) {
                
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.search_result_section_header, parent, false);
                }
                
                TextView sectionTextView = (TextView) convertView.findViewById(R.id.section_header);
                
                Object[] sections = getSections();
                int sectionIndex  = getSectionForPosition(position);
                String section    = (String) sections[sectionIndex];
                sectionTextView.setText(section);
                convertView.setClickable(false);
                
                return convertView;
            }
            
            // Regular search item (not a header):
            int sectionIndex = getSectionForPosition(position);
            int positionOffset = position - mSectionToOffset.get(sectionIndex) - 1;
            
            return super.getView(positionOffset, convertView, parent);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(TAG, "newView() ran");

            View view = mInflater.inflate(mLayout, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name_id);
            view.setTag(viewHolder);

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            
            // Disable clicking section separators
            if (position == getPositionForSection(getSectionForPosition(position))) {
                return false;
            }
            
            return super.isEnabled(position);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView() ran");

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            String text = "";
            int mimeTypeCol = cursor.getColumnIndex(SearchColumns.MIME_TYPE);
            String mimeType = cursor.getString(mimeTypeCol);

            if (ArtistColumns.MIME_TYPE.equalsIgnoreCase(mimeType)) {
                int col = cursor.getColumnIndex(SearchColumns.ARTIST_NAME);
                text = cursor.getString(col);
            }
            else if (AlbumColumns.MIME_TYPE.equalsIgnoreCase(mimeType)) {
                int albumCol  = cursor.getColumnIndex(SearchColumns.ALBUM_NAME);
                int artistCol = cursor.getColumnIndex(SearchColumns.ARTIST_NAME);
                text = cursor.getString(albumCol) + " (" + cursor.getString(artistCol) + ")";
            }
            else if (TrackColumns.MIME_TYPE.equalsIgnoreCase(mimeType)) {
                int trackCol  = cursor.getColumnIndex(SearchColumns.TRACK_NAME);
                int artistCol = cursor.getColumnIndex(SearchColumns.ARTIST_NAME);
                text = cursor.getString(trackCol) + " (" + cursor.getString(artistCol) + ")";
            }

            viewHolder.name.setText(text);
        }

        @Override
        public Cursor swapCursor(Cursor cursor) {
            Log.d(TAG, "swapCursor() ran: " + cursor);

            // After the loader is finished and we have Cursor, 
            // then we can initialize the section indexer:
            if (cursor != null) {
                initIndexer(cursor);
            }

            return super.swapCursor(cursor);
        }

        // Setup all the data structures needed for handling section indexing the list
        private void initIndexer(Cursor cursor) {
            Log.d(TAG, "initIndexer() ran: " + cursor);

            mIndexer = new MusicItemIndexer(cursor, cursor.getColumnIndexOrThrow(SearchColumns.MIME_TYPE), SECTIONS);

            mSectionToPosition = new TreeMap<Integer, Integer>();
            
            final int count = cursor.getCount(); 
         
            // Temporarily have a map alphabet section to first index where it appears i the list
            // Iterate in reverse to get the first occurrence in the list
            for (int i = count - 1; i >= 0; i--) {
                mSectionToPosition.put(mIndexer.getSectionForPosition(i), i);
            }

            int i = 0;
            int size = mSectionToPosition.keySet().size();
            mSectionToOffset    = new SparseIntArray(size);
            mUsedSectionNumbers = new int[size];

            // For each section that appears before a position, we must offset the
            // indices by 1, to make room for an section header in the list
            for (Integer section : mSectionToPosition.keySet()) {
                mSectionToOffset.put(section, i);
                mUsedSectionNumbers[i] = section;
                i++;
            }
            
            // Use offset to map each section to its actual index in the list
            for (Integer section : mSectionToPosition.keySet()) {
                mSectionToPosition.put(section, mSectionToPosition.get(section) + mSectionToOffset.get(section));
            }
        }
        
        @Override
        public int getCount() {
            
            int count = super.getCount();

            if (count != 0) {
                if (mUsedSectionNumbers != null) {
                    count += mUsedSectionNumbers.length;
                }
            }

            return count;
        }

        @Override
        public int getItemViewType(int position) {
            
            if (position == getPositionForSection(getSectionForPosition(position))) {
                return SEARCH_SEPARATOR_VIEW;
            } 
            
            return SEARCH_RESULT_VIEW;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public Object getItem(int position) {
            
            if (getItemViewType(position) == SEARCH_RESULT_VIEW) {
                // If the list item is not a header, then we fetch the data set item with the same 
                // position offsetted by the number of headers that appear before the item in the list
                return super.getItem(position - mSectionToOffset.get(getSectionForPosition(position)) - 1);
            }

            return null;
        }
        
        @Override
        public long getItemId(int position) {

            if (getItemViewType(position) == SEARCH_RESULT_VIEW) {
                // If the list item is not a header, then we fetch the data set item id with the same 
                // position offsetted by the number of headers that appear before the item in the list
                return super.getItemId(position - mSectionToOffset.get(getSectionForPosition(position)) - 1);
            }
            
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            
            int i = 0;      
            int maxLength = mUsedSectionNumbers.length;
    
            // Linear scan over the used sections' positions to find where the given section fits in
            while (i < maxLength && position >= mSectionToPosition.get(mUsedSectionNumbers[i])) {
                i++;
            }
            
            return mUsedSectionNumbers[i - 1];
        }

        @Override
        public int getPositionForSection(int section) {
            
            if (mSectionToOffset.indexOfKey(section) >= 0) { 
                // This is only the case when the FastScroller is scrolling,
                // and this section doesn't appear in our data set. 
                // The implementation of FastScroller requires that missing sections 
                // have the same index as the beginning of the next non-missing section 
                // (or the end of the list if the rest of the sections are missing).
                int i = 0;
                int maxLength = mUsedSectionNumbers.length;
                
                // Linear scan over the sections (constant number of these) that appear in the 
                // data set to find the first used section that is greater than the given section
                while (i < maxLength && section > mUsedSectionNumbers[i]) {
                    i++;
                }

                // The given section is past all our data
                if (i == maxLength) {
                    return getCount(); 
                }

                int nextSection = mUsedSectionNumbers[i];
                
                return mIndexer.getPositionForSection(nextSection) + mSectionToOffset.get(nextSection);
            }

            return mIndexer.getPositionForSection(section) + mSectionToOffset.get(section);
        }
        
        @Override
        public Object[] getSections() {         
            return mIndexer.getSections();
        }
    }
    
    
    public static class SearchListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private final static String TAG = SearchListFragment.class.getSimpleName();
        private static final String QUERY_STRING = "query";
        private static final int SEARCH_LIST_LOADER = 1;

        private SearchCursorAdapter mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated() ran");
            super.onActivityCreated(savedInstanceState);

            Intent intent = getActivity().getIntent();

            String[] uiBindFrom = { SearchColumns.ARTIST_NAME };
            int[] uiBindTo = { R.id.name_id };

            mAdapter = new SearchCursorAdapter(getActivity().getApplicationContext(), R.layout.search_result_list_item,
                    null, uiBindFrom, uiBindTo, 0); // CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER

            setListAdapter(mAdapter);

            handleIntent(intent);
        }

        private void handleIntent(Intent intent) {
            
            // if (Intent.ACTION_VIEW) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                Log.d(TAG, "query: " + query);
                performSearch(query);
            }
        }

        private void performSearch(String query) {

            Bundle queryBundle = new Bundle();
            queryBundle.putString(QUERY_STRING, query);

            getLoaderManager().restartLoader(SEARCH_LIST_LOADER, queryBundle, this);
        }
        
        @Override
        public void onListItemClick(ListView listView, View view, int position, long id) {
            Log.i(TAG, "onListItemClick() - id: " + id + ", position: " + position);
            Log.i(TAG, "onListItemClick() - listview: " + listView + ", view: " + view);

            Object[] sections = mAdapter.getSections();
            int sectionIndex  = mAdapter.getSectionForPosition(position);
            String mimeType   = (String) sections[sectionIndex];
            
            Intent intent = new Intent();
            if (ArtistColumns.MIME_TYPE.equalsIgnoreCase(mimeType)) {

                intent = new Intent(getActivity(), ArtistActivity.class);
                intent.putExtra(MusicManager.ARTIST, id);
            }
            else if (AlbumColumns.MIME_TYPE.equalsIgnoreCase(mimeType)) {

                intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra(MusicManager.ALBUM, id);
            }
            else if (TrackColumns.MIME_TYPE.equalsIgnoreCase(mimeType)) {

                intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(MusicManager.TRACK, id);
                intent.setAction(PlayerActivity.ACTION_PLAY_TRACK);
            }

            startActivity(intent);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader() ran");

            String query = args.getString(QUERY_STRING);
            String[] projection = { 
                    SearchColumns._ID, 
                    SearchColumns.ARTIST_NAME, 
                    SearchColumns.ALBUM_NAME,
                    SearchColumns.TRACK_NAME, 
                    SearchColumns.MIME_TYPE, 
                    SearchColumns.GROUP_ORDER 
                    };
            Uri contentUri = Uri.parse(SocksoProvider.CONTENT_URI + "/" + SearchColumns.TABLE_NAME + "/" + query);

            CursorLoader cursorLoader = new CursorLoader(getActivity(), contentUri, projection, null, null,
                    SearchColumns.GROUP_ORDER);

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(TAG, "onLoadFinished() ran: cursor.count: " + cursor.getCount());

            if (cursor.getCount() == 0) {
                setEmptyText(getString(R.string.no_search_results));
            }
            
            mAdapter.swapCursor(cursor);
            // Enable FastScrolling
            ListView view = getListView();
            view.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);
            view.setFastScrollEnabled(true);
            
            jiggleWidth(); // see method description for why this is here
        }

        /** 
         * Fixes FastScrolling display bug
         * (http://code.google.com/p/android/issues/detail?id=9054)
         */
        private boolean mFlagThumbPlus = false;
        
        private void jiggleWidth() { 

            ListView view = getListView();
            
            if (view.getWidth() <= 0) {
                return;
            }

            int newWidth = mFlagThumbPlus ? view.getWidth() - 1 : view.getWidth() + 1;
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = newWidth;
            
            // Jiggle:
            view.setLayoutParams( params );

            // Flip it
            mFlagThumbPlus = ! mFlagThumbPlus;
        }
        
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    }
}
