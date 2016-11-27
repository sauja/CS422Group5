package com.cs442.group5.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.model.Review;
import com.cs442.group5.feedback.utils.RatingColor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreReviewsActivity extends AppCompatActivity {
	private static final String TAG=MoreReviewsActivity.class.getSimpleName();
	Context context;
	ArrayList<Review> reviewList;
	String storeid="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_reviews);
		context=this;
		reviewList=new Gson().fromJson(getIntent().getExtras().get("reviewList").toString(),new TypeToken<ArrayList<Review>>() {}.getType());
		storeid=getIntent().getExtras().get("storeid").toString();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		ListView listView_reviews=(ListView) findViewById(R.id.listView_reviews);
		MyReviewAdapter myReviewAdapter=new MyReviewAdapter(context,reviewList);
		listView_reviews.setAdapter(myReviewAdapter);
		myReviewAdapter.notifyDataSetChanged();



	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent=new Intent(MoreReviewsActivity.this,StoreActivity.class);
		intent.putExtra("storeid",storeid);
		startActivity(intent);
	}

	private static class ViewHolder
	{
		CircleImageView profile_image;
		TextView textView_name;
		TextView textView_date;
		TextView textView_rating;
		TextView textView_comment;
	}
	private class MyReviewAdapter extends ArrayAdapter<Review>
	{



		public MyReviewAdapter(Context context, ArrayList<Review> review) {
			super(context, R.layout.content_store_review, review);
		}



		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			Review review = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			ViewHolder viewHolder; // view lookup cache stored in tag
			if (convertView == null) {

				// If there's no view to re-use, inflate a brand new view for row
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.content_store_review, parent, false);
				viewHolder.textView_rating = (TextView) convertView.findViewById(R.id.textView_rating);
				viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
				viewHolder.textView_date = (TextView) convertView.findViewById(R.id.textView_date);
				viewHolder.textView_comment = (TextView) convertView.findViewById(R.id.textView_comment);
				viewHolder.profile_image = (CircleImageView) convertView.findViewById(R.id.profile_image);

				// Cache the viewHolder object inside the fresh view
				convertView.setTag(viewHolder);
			} else {
				// View is being recycled, retrieve the viewHolder object from tag
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// Populate the data from the data object via the viewHolder object
			// into the template view.
			viewHolder.textView_name.setText(review.getFullname());

			viewHolder.textView_date.setText(new SimpleDateFormat("MMM dd, yyyy").format(review.getTimestamp()));
			viewHolder.textView_rating.setText((String.valueOf(review.getRating())).substring(0, 3));
			int color = RatingColor.getRatingColor(review.getRating(), context);
			((GradientDrawable)viewHolder.textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable) viewHolder.textView_rating.getBackground()).setColor(color);
			viewHolder.textView_comment.setText(review.getComment());
			Glide.with(context).load(review.getImgurl()).into(viewHolder.profile_image);
			// Return the completed view to render on screen


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
