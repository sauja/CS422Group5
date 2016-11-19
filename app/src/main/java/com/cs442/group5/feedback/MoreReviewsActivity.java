package com.cs442.group5.feedback;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_reviews);
		context=this;
		reviewList=new Gson().fromJson(getIntent().getExtras().get("reviewList").toString(),new TypeToken<ArrayList<Review>>() {}.getType());

		ListView listView_reviews=(ListView) findViewById(R.id.listView_reviews);
		MyReviewAdapter myReviewAdapter=new MyReviewAdapter(context,reviewList);
		listView_reviews.setAdapter(myReviewAdapter);
		myReviewAdapter.notifyDataSetChanged();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


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
			viewHolder.textView_name.setText(review.getUid());

			viewHolder.textView_date.setText(new SimpleDateFormat("MMM dd, yyyy").format(review.getTimestamp()));
			viewHolder.textView_rating.setText((String.valueOf(review.getRating())).substring(0, 3));
			int color = RatingColor.getRatingColor(review.getRating(), context);
			((GradientDrawable)viewHolder.textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable) viewHolder.textView_rating.getBackground()).setColor(color);
			viewHolder.textView_comment.setText(review.getComment());
			Glide.with(context).load(R.drawable.gio).into(viewHolder.profile_image);
			// Return the completed view to render on screen


			return convertView;
		}
	}
}
