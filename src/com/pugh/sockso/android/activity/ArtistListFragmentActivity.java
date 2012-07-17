package com.pugh.sockso.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pugh.sockso.android.R;
import com.pugh.sockso.android.music.Artist;

public class ArtistListFragmentActivity extends FragmentActivity {

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

	public static class ArtistListAdapter extends ArrayAdapter<Artist> {

		private final Context context;
		private final Artist[] values;
		
        private final LayoutInflater mInflater;
		
		public ArtistListAdapter(Context context, Artist[] artists) {
			super(context, R.layout.artist_list_item, artists);
			this.context = context;
			this.values = artists;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.artist_list_item,
						parent, false);
			}
			else{
				view = convertView;
			}

			Artist artist = getItem(position);
			
			TextView artistTitleText = (TextView) view.findViewById(R.id.artist_name_id);
			artistTitleText.setText(artist.getName());
			
			//ImageView imageView = (ImageView) view.findViewById(R.id.artist_cover_image);
			//imageView.setImageResource(R.drawable.icon);

            return view;
		}
	}

	public static class ArtistListFragment extends ListFragment {

		private final static String TAG = ArtistListFragment.class.getSimpleName();

		// This is the Adapter being used to display the list's data.
		ArtistListAdapter mArtistAdapter;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			// TODO REMOVE TEST DATA
			Artist[] artists = {
					new Artist("Daft Punk"),
					new Artist("Beatles"),
					new Artist("David Bowie")
			};

			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			setEmptyText("No applications");

			// Create an empty adapter we will use to display the loaded data.
			mArtistAdapter = new ArtistListAdapter(getActivity(), artists);
			setListAdapter(mArtistAdapter);

		}

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

	}
}