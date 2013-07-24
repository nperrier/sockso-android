package com.pugh.sockso.android.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.CoverArtFetcher;
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
    public static class TrackCursorAdapter extends SimpleCursorAdapter implements SectionIndexer {

        private Context mContext;
        private int mLayout;
        private CoverArtFetcher mCoverFetcher;
        private SectionIndexer mAlphaIndexer;

        public TrackCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
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

            mCoverFetcher.loadCoverArtTrack(trackId, viewHolder.cover);
        }

        @Override
        public Cursor swapCursor(Cursor cursor) {
            // Create our indexer
            if (cursor != null) {
                mAlphaIndexer = new AlphabetIndexer(cursor, cursor.getColumnIndex(TrackColumns.NAME), 
                        " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            }
            return super.swapCursor(cursor);
        }

        public int getPositionForSection(int section) {
            return mAlphaIndexer.getPositionForSection(section);
        }

        public int getSectionForPosition(int position) {
            return mAlphaIndexer.getSectionForPosition(position);
        }

        public Object[] getSections() {
            return mAlphaIndexer.getSections();
        }
        
        // @Override
        // TODO, this is for filtered searches
        // public Cursor runQueryOnBackgroundThread(CharSequence constraint) {}
    }

}