package com.cs442.group5.feedback.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cs442.group5.feedback.App;
import com.cs442.group5.feedback.MyStorePageActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.model.Review;
import com.cs442.group5.feedback.utils.Libs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReviewIntentService extends IntentService {
	private static final String TAG = "ReviewIntentService";
Context context;
	// TODO: Rename actions, choose action names that describe tasks that this
	// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
	public static final String GET_ALL_REVIEWS = "getAllReviews";
	public static final String GET_REVIEW_FOR_CHART = "getReviewRatingCountChart";
	public static final String ADD_REVIEW = "addReview";

	public ReviewIntentService() {
		super("ReviewIntentService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		context=this;
		if (intent != null) {
			final String action = intent.getAction();
			switch(action)
			{
				case GET_ALL_REVIEWS:

					getAllReviews(intent.getIntExtra(GET_ALL_REVIEWS,-1));
					break;
				case GET_REVIEW_FOR_CHART:
					/*final String param1 = intent.getStringExtra(EXTRA_PARAM1);
					final String param2 = intent.getStringExtra(EXTRA_PARAM2);
					handleActionBaz(param1, param2);*/
					Log.e(TAG, "onHandleIntent: GET_REVIEW_FOR_CHART storeid "+intent.getLongExtra(GET_REVIEW_FOR_CHART,-1) );
					getReviewRatingCountChart(intent.getLongExtra(GET_REVIEW_FOR_CHART,-1));
					break;
				case ADD_REVIEW:
					Review r=new Gson().fromJson(intent.getStringExtra(ADD_REVIEW),new TypeToken<Review>() {}.getType());
					addReview(r);
					break;
			}

		}
	}

	public void getAllReviews(final long storeid) {
		final String url = context.getString(R.string.server_url) + "/review/getAllReviews";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Gson gson = new Gson();

				//reviewList = gson.fromJson(response, new TypeToken<ArrayList<Review>>() {}.getType());
				//updateReviewFields();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error", error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(storeid));
				return parameters;
			}
		};
		Libs.getQueueInstance().add(postRequest);
	}
	public void addReview(final Review r) {
		Log.e(TAG, "addReview: " + r.getStoreid() + "\n"
				+ r.getUid() + "\n" + r.getComment());
		final String url = App.getContext().getString(R.string.server_url) + "/review/addReview";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(App.getContext(), "Added", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: " + response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error", error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(r.getStoreid()));
				parameters.put("uid", r.getUid());
				parameters.put("comment", r.getComment());
				parameters.put("rating", String.valueOf(r.getRating()));
				return parameters;
			}
		};
		Libs.getQueueInstance().add(postRequest);
	}
	public void getReviewRatingCountChart(final long storeid) {

		final String url = App.getContext().getString(R.string.server_url) + "/review/getReviewRatingCountChart";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(App.getContext(), "Added", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: " + response);


				Intent broadcastIntent=new Intent(ReviewIntentService.this,MyStorePageActivity.class);
				broadcastIntent.setAction(GET_REVIEW_FOR_CHART);
				broadcastIntent.putExtra(GET_REVIEW_FOR_CHART,response);
				LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
				/*
				*
				*
				*
				*
				*       ADD YOUR CHARTING FUNCTION HERE
				*       Count is Y-Axis
				*       Rating is X-Axis
				*       Rating from 0-5 in 0.5 increments
				*       data is available in  reviewRatingCountCharts arraylist
				*
				*
				*
				 */
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
				Log.e("error", error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(storeid));
				return parameters;
			}
		};
		Libs.getQueueInstance().add(postRequest);
	}
}

