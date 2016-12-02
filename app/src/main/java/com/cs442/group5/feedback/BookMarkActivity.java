package com.cs442.group5.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.service.StoreIntentService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class BookMarkActivity extends AppCompatActivity {
	private static final String TAG = "BookMarkActivity";
	Context context;
	RecyclerView recyclerView_bookmarks;
	BookmarksArrayAdapter bookmarksArrayAdapter;
	ArrayList<Store> storeList=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_mark);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);

		}


	}

	@Override
	protected void onResume() {
		final SharedPreferences sf=getSharedPreferences("user",MODE_PRIVATE);
		Intent intent=new Intent(BookMarkActivity.this,StoreIntentService.class);
		intent.setAction(StoreIntentService.GET_BOOKMARKED_STORES);
		intent.putExtra(StoreIntentService.GET_BOOKMARKED_STORES,sf.getString("uid",""));
		intent.putExtra("activity",TAG);
		startService(intent);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						storeList=new Gson().fromJson(intent.getStringExtra(StoreIntentService.GET_BOOKMARKED_STORES),new TypeToken<ArrayList<Store>>() {}.getType());
						recyclerView_bookmarks=(RecyclerView)findViewById(R.id.recyclerView_bookmarks);
						bookmarksArrayAdapter=new BookmarksArrayAdapter(context,storeList);
						bookmarksArrayAdapter.notifyDataSetChanged();
					}
				}, new IntentFilter(StoreIntentService.GET_STORE)
		);
		super.onResume();
	}
	public class BookmarksArrayAdapter extends RecyclerView.Adapter<BookMarkActivity.BookmarksViewHolder>{
		private ArrayList<Store> storeArrayList;
		private Context context;
		public BookmarksArrayAdapter(Context context, ArrayList<Store> store) {
			this.storeArrayList=store;
			this.context=context;
		}

		@Override
		public BookmarksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_list_item, null);
			BookmarksViewHolder viewHolder = new BookmarksViewHolder(view);
			return viewHolder;
		}

		@Override
		public void onBindViewHolder(BookmarksViewHolder holder, int position) {
			Store store=storeArrayList.get(position);
			holder.textView_name.setText(store.getName().toString());
			holder.textView_location.setText(store.getLocation().toString());
			Glide.with(context).load(store.getImgurl()).centerCrop().fitCenter().into(holder.imageView_img);
		}

		@Override
		public int getItemCount() {
			return (null != storeArrayList ? storeArrayList.size() : 0);
		}
	}
	public class BookmarksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

		ImageView imageView_img;
		TextView textView_name;
		TextView textView_location;
		public BookmarksViewHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			imageView_img=(ImageView)findViewById(R.id.imageView_imgurl);
			textView_location=(TextView)findViewById(R.id.textView_location);
			textView_name=(TextView) findViewById(R.id.textView_name);
		}

		@Override
		public void onClick(View view) {
			if (mOnEntryClickListener != null) {
				mOnEntryClickListener.onEntryClick(view, getLayoutPosition());
			}
		}

	}
	private OnEntryClickListener mOnEntryClickListener;

	private interface OnEntryClickListener {
		void onEntryClick(View view, int position);
	}

	public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
		mOnEntryClickListener = onEntryClickListener;
	}
}
