package com.cs442.group5.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.service.StoreIntentService;
import com.cs442.group5.feedback.utils.Libs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class BookMarkActivity extends AppCompatActivity {
	private static final String TAG = "BookMarkActivity";
	Context context;
	ListView recyclerView_bookmarks;
	MyStoreDashboardArrayAdapter bookmarksArrayAdapter;
	ArrayList<Store> storeList=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_mark);
		context=this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);

		}


	}

	@Override
	protected void onResume() {

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
						storeList=new Gson().fromJson(intent.getStringExtra(StoreIntentService.GET_BOOKMARKED_STORES),new TypeToken<ArrayList<Store>>() {}.getType());
						recyclerView_bookmarks=(ListView) findViewById(R.id.recyclerView_bookmarks);
						Log.e(TAG, "onReceive: "+new Gson().toJson(storeList)) ;
						bookmarksArrayAdapter=new MyStoreDashboardArrayAdapter(context,storeList);
						recyclerView_bookmarks.setAdapter(bookmarksArrayAdapter);
						bookmarksArrayAdapter.notifyDataSetChanged();
					}
				}, new IntentFilter(StoreIntentService.GET_BOOKMARKED_STORES)
		);
		super.onResume();
	}
	private static class ViewHolder
	{
		ImageView imageView_img;
		TextView textView_rating;
		TextView textView_name;
		TextView textView_address;
		TextView textView_tags;
	}
	private class MyStoreDashboardArrayAdapter extends ArrayAdapter<Store>
	{



		public MyStoreDashboardArrayAdapter(Context context, ArrayList<Store> store) {
			super(context, R.layout.bookmark_list_item, store);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			Store store = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			ViewHolder viewHolder; // view lookup cache stored in tag
			if (convertView == null) {

				// If there's no view to re-use, inflate a brand new view for row
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.bookmark_list_item, parent, false);
				//viewHolder.textView_rating = (TextView) convertView.findViewById(R.id.textView_rating);
				viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
				viewHolder.textView_address = (TextView) convertView.findViewById(R.id.textView_address);
				//viewHolder.textView_tags = (TextView) convertView.findViewById(R.id.textView_tags);
				viewHolder.imageView_img = (ImageView) convertView.findViewById(R.id.imageView_img);

				// Cache the viewHolder object inside the fresh view
				convertView.setTag(viewHolder);
			} else {
				// View is being recycled, retrieve the viewHolder object from tag
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// Populate the data from the data object via the viewHolder object
			// into the template view.
			//viewHolder.textView_rating.setText((String.valueOf(store.getRating())).substring(0,3));
			//int color= RatingColor.getRatingColor(store.getRating(),context);
			//((GradientDrawable)viewHolder.textView_rating.getBackground()).setStroke(10, color);
			//((GradientDrawable)viewHolder.textView_rating.getBackground()).setColor( color);
			viewHolder.textView_address.setText(store.getAddress()+"\n"+store.getLocation());
			//viewHolder.textView_tags.setText(store.getTags());
			viewHolder.textView_name.setText(store.getName());
			Log.e(TAG, "Tags: "+store.getTags() );
			Glide.with(context).load(store.getImgurl()).centerCrop().into(viewHolder.imageView_img);
			// Return the completed view to render on screen
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent=new Intent(context,StoreActivity.class);
					Log.e(TAG, "onClick: "+storeList.get(position).getId() );
					intent.putExtra("storeid",storeList.get(position).getId());
					Log.e(TAG, "onClick: "+ storeList.get(position).getId());
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

}
