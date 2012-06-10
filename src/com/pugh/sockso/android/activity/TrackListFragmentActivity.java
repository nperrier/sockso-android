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
import com.pugh.sockso.android.music.Track;

public class TrackListFragmentActivity extends FragmentActivity {

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

	public static class TrackListAdapter extends ArrayAdapter<Track> {

		private final Context context;
		private final Track[] values;
		
        private final LayoutInflater mInflater;
		
		public TrackListAdapter(Context context, Track[] tracks) {
			super(context, R.layout.track_list_item, tracks);
			this.context = context;
			this.values = tracks;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.track_list_item,
						parent, false);
			}
			else{
				view = convertView;
			}

			Track track = getItem(position);
			
			TextView trackTitleText = (TextView) view.findViewById(R.id.track_title_text);
			trackTitleText.setText(track.getTitle());

			TextView artistTitleText = (TextView) view.findViewById(R.id.track_artist_text);
			artistTitleText.setText(track.getArtist());
			
			ImageView imageView = (ImageView) view.findViewById(R.id.track_cover_image);
			imageView.setImageResource(R.drawable.icon);

            return view;
		}
	}

	public static class TrackListFragment extends ListFragment {

		private final static String TAG = TrackListFragment.class.getName();

		// This is the Adapter being used to display the list's data.
		TrackListAdapter mTrackAdapter;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			Track[] tracks = {
					new Track("Robot Rock", "Human After All", "Daft Punk"),
					new Track("Paperback Writer", "Rubber Soul", "Beatles"),
					new Track("5 Years", "Ziggy Stardust", "David Bowie")
			};

			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			setEmptyText("No applications");

			// Create an empty adapter we will use to display the loaded data.
			mTrackAdapter = new TrackListAdapter(getActivity(), tracks);
			setListAdapter(mTrackAdapter);

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