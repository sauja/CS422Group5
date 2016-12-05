package com.cs442.group5.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.service.StoreIntentService;
import com.cs442.group5.feedback.utils.Libs;
import com.cs442.group5.feedback.utils.RatingColor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class BookMarkActivity extends AppCompatActivity {
	private static final String TAG = "BookMarkActivity";
	private Context context;
	RequestQueue queue;
	ArrayList<Store> myStores;
	GridView gridview_myStores;
	MyStoreArrayAdapter arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_mark);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		context=this;
		queue = Volley.newRequestQueue(this);
		gridview_myStores=(GridView)findViewById(R.id.gridview_myStores);





		Log.e(TAG, "onCreate: " );

	}

	@Override
	protected void onResume() {
		refreshBookmarkedStores();
		super.onResume();
	}

	public void refreshBookmarkedStores()
	{
		Intent intent=new Intent(BookMarkActivity.this,StoreIntentService.class);
		intent.setAction(StoreIntentService.GET_BOOKMARKED_STORES);
		intent.putExtra(StoreIntentService.GET_BOOKMARKED_STORES, Libs.getUser().getUid());
		intent.putExtra("activity",TAG);
		startService(intent);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						//Log.e(TAG, "onReceive: "+intent.getStringExtra(StoreIntentService.GET_BOOKMARKED_STORES));
						myStores=new Gson().fromJson(intent.getStringExtra(StoreIntentService.GET_BOOKMARKED_STORES),new TypeToken<ArrayList<Store>>() {}.getType());

						//Log.e(TAG, "onReceive: "+new Gson().toJson(myStores)) ;
						arrayAdapter=new MyStoreArrayAdapter(context,myStores);
						gridview_myStores.setAdapter(arrayAdapter);
						arrayAdapter.notifyDataSetChanged();
					}
				}, new IntentFilter(StoreIntentService.GET_BOOKMARKED_STORES)
		);
	}

	private class MyStoreArrayAdapter extends ArrayAdapter<Store>
	{
		private  class ViewHolder {
			ImageView imageView_img;
			TextView textView_rating;
			TextView textView_name;
			TextView textView_address;
			TextView textView_tags;
		}
		public MyStoreArrayAdapter(Context context, ArrayList<Store> store) {
			super(context, R.layout.bookmark_list_item, store);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			Store adapterStore = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			ViewHolder viewHolder; // view lookup cache stored in tag
			if (convertView == null) {

				// If there's no view to re-use, inflate a brand new view for row
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.store_list_item, parent, false);
				viewHolder.textView_rating = (TextView) convertView.findViewById(R.id.textView_rating);
				viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
				viewHolder.textView_address = (TextView) convertView.findViewById(R.id.textView_address);
				viewHolder.textView_tags = (TextView) convertView.findViewById(R.id.textView_tags);
				viewHolder.imageView_img = (ImageView) convertView.findViewById(R.id.imageView_img);

				// Cache the viewHolder object inside the fresh view
				convertView.setTag(viewHolder);
			} else {
				// View is being recycled, retrieve the viewHolder object from tag
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// Populate the data from the data object via the viewHolder object
			// into the template view.

			viewHolder.textView_rating.setText((String.valueOf(adapterStore.getRating())).substring(0,3));
			int color= RatingColor.getRatingColor(adapterStore.getRating(),context);
			((GradientDrawable)viewHolder.textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable)viewHolder.textView_rating.getBackground()).setColor( color);
			viewHolder.textView_address.setText(adapterStore.getAddress());
			viewHolder.textView_tags.setText(adapterStore.getTags());
			viewHolder.textView_name.setText(adapterStore.getName());
			Glide.with(context).load(adapterStore.getImgurl()).into(viewHolder.imageView_img);
			// Return the completed view to render on screen
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent=new Intent(context,StoreActivity.class);
					Log.e(TAG, "onClick: "+myStores.get(position).getId() );
					intent.putExtra("storeid",myStores.get(position).getId());
					intent.putExtra("storename",myStores.get(position).getName());
					intent.putExtra("mode","EDIT");
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// handle arrow click here
		if (item.getItemId() == android.R.id.home)
		{
			finish(); // close this activity and return to preview activity (if there is any)
		}

		return super.onOptionsItemSelected(item);
	}
}
