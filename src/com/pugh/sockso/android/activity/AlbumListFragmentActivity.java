package com.pugh.sockso.android.activity;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.data.SocksoProvider;
import com.pugh.sockso.android.music.Album;

public class AlbumListFragmentActivity extends FragmentActivity {

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
	
	public static class AlbumListAdapter extends ArrayAdapter<Album> {

		private final Context context;
		private final Album[] values;
		
        private final LayoutInflater mInflater;
		
		public AlbumListAdapter(Context context, Album[] albums) {
			super(context, R.layout.album_list_item, albums);
			this.context = context;
			this.values = albums;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.album_list_item,
						parent, false);
			}
			else{
				view = convertView;
			}

			Album album = getItem(position);
			
			TextView albumTitleText = (TextView) view.findViewById(R.id.album_title_text);
			albumTitleText.setText(album.getTitle());

			TextView artistTitleText = (TextView) view.findViewById(R.id.album_artist_text);
			artistTitleText.setText(album.getArtist());
			
			ImageView imageView = (ImageView) view.findViewById(R.id.album_cover_image);
			imageView.setImageResource(R.drawable.icon);

            return view;
		}
	}

	public static class AlbumListFragment extends ListFragment 
		implements LoaderManager.LoaderCallbacks<Cursor> {

		private final static String TAG = AlbumListFragment.class.getName();

		// This is the Adapter being used to display the list's data.
		AlbumListAdapter mAlbumAdapter;
		
		// TutListFragment class member variables
		private static final int ALBUM_LIST_LOADER = 0x01;
		 
		private SimpleCursorAdapter adapter;
		 
		@Override
		public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
			Log.i(TAG, "onCreate() ran");
		    // TODO How do I bind to a column that's in another table ref
		    // by a foreign key?
		    String[] uiBindFrom = { SocksoProvider.Album.Columns.NAME };
		    int[]    uiBindTo   = { R.id.album_title_text }; 
		 
		    getLoaderManager().initLoader(ALBUM_LIST_LOADER, null, this);
		 
			// Create an empty adapter we will use to display the loaded data.
		    adapter = new SimpleCursorAdapter(
		            getActivity().getApplicationContext(), 
		            R.layout.album_list_item,
		            null,
		            uiBindFrom, 
		            uiBindTo,
		            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		 
		    setListAdapter(adapter);
		}
		
		/*
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
					
			Album[] albums = {
					new Album("Human After All", "Daft Punk"),
					new Album("Rubber Soul", "Beatles"),
					new Album("Ziggy Stardust", "David Bowie")
			};

			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			setEmptyText("No albums found.");

			// Create an empty adapter we will use to display the loaded data.
			mAlbumAdapter = new AlbumListAdapter(getActivity(), albums);
			setListAdapter(mAlbumAdapter);

		}
		*/

		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
			savedInstanceState.putString("bla", "Value1");
			super.onSaveInstanceState(savedInstanceState);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i(TAG, "onListItemClick(): Item clicked: " + id);
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Log.i(TAG, "onCreateLoader() ran");
		    String[] projection = { SocksoProvider.Album.Columns._ID, 
		    						SocksoProvider.Album.Columns.NAME 
		    					  };
		    // TODO Define Uri's for SocksoProvider
		    CursorLoader cursorLoader = new CursorLoader(getActivity(),
		            Uri.parse(SocksoProvider.CONTENT_URI + "/" + SocksoProvider.Album.TABLE_NAME), 
		            projection, null, null, null);
		    return cursorLoader;
		}
		
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		    adapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}

	}
}