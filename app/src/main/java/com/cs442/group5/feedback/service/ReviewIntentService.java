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
import com.cs442.group5.feedback.AddReviewActivity;
import com.cs442.group5.feedback.DashBoardActivity;
import com.cs442.group5.feedback.MyStorePageActivity;
import com.cs442.group5.feedback.NewStoreActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.StoreActivity;
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
	String broadcastClass;
	public ReviewIntentService() {
		super("ReviewIntentService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		context=this;
		if (intent != null) {
			final String action = intent.getAction();
			broadcastClass=intent.getStringExtra("activity");
			switch(action)
			{

				case GET_ALL_REVIEWS:
					broadcastClass=intent.getStringExtra("activity");
					Log.e(TAG, "onHandleIntent: GET_ALL_REVIEWS storeid "+intent.getStringExtra(GET_ALL_REVIEWS) );
					getAllReviews(intent.getStringExtra(GET_ALL_REVIEWS));
					break;
				case GET_REVIEW_FOR_CHART:
					broadcastClass=intent.getStringExtra("activity");
					/*final String param1 = intent.getStringExtra(EXTRA_PARAM1);
					final String param2 = intent.getStringExtra(EXTRA_PARAM2);
					handleActionBaz(param1, param2);*/
					Log.e(TAG, "onHandleIntent: GET_REVIEW_FOR_CHART storeid "+intent.getLongExtra(GET_REVIEW_FOR_CHART,-1) );
					getReviewRatingCountChart(intent.getStringExtra(GET_REVIEW_FOR_CHART));
					break;
				case ADD_REVIEW:
					Review r=new Gson().fromJson(intent.getStringExtra(ADD_REVIEW),new TypeToken<Review>() {}.getType());
					Log.e(TAG, "onHandleIntent: "+new Gson().toJson(r) );
					addReview(r);
					break;
			}

		}
	}

	public void getAllReviews(final String storeid) {
		final String url = context.getString(R.string.server_url) + "/review/getAllReviews";

		Log.e(TAG, "getAllReviews: "+storeid );
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: getAllReviews "+storeid+" "+response );
				sendStoreBroadcast(response,GET_ALL_REVIEWS);
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
				parameters.put("storeid", storeid);
				return parameters;
			}
		};postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);
	}
	public void addReview(final Review r) {
		Log.e(TAG, "addReview: " + r.getStoreid() + "\n"
				+ r.getUid() + "\n" + r.getComment());
		final String url = Libs.getContext().getString(R.string.server_url) + "/review/addReview";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(Libs.getContext(), "Review Added", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: " + response);
				sendStoreBroadcast(response,ADD_REVIEW);
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
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);
	}
	public void getReviewRatingCountChart(final String storeid) {

		final String url = Libs.getContext().getString(R.string.server_url) + "/review/getReviewRatingCountChart";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(Libs.getContext(), "Added", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: " + response);
				sendStoreBroadcast(response,GET_REVIEW_FOR_CHART);
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
				parameters.put("storeid", storeid);
				return parameters;
			}
		};
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);
	}
	private void sendStoreBroadcast(String response,String action)
	{
		Intent broadcastIntent=null;
		switch (broadcastClass)
		{
			case "AddReviewActivity":
				broadcastIntent=new Intent(ReviewIntentService.this,AddReviewActivity.class);
				break;
			case "MyStorePageActivity":
				broadcastIntent=new Intent(ReviewIntentService.this,MyStorePageActivity.class);
				break;
			case "NewStoreActivity":
				broadcastIntent=new Intent(ReviewIntentService.this,NewStoreActivity.class);
				break;
			case "DashBoardActivity":
				broadcastIntent=new Intent(ReviewIntentService.this,DashBoardActivity.class);
				break;
			case "StoreActivity":
				broadcastIntent=new Intent(ReviewIntentService.this,StoreActivity.class);
				break;
		}
		Log.e(TAG, "sendStoreBroadcast: "+broadcastClass );
		if(broadcastIntent!=null){
			broadcastIntent.setAction(action);
			broadcastIntent.putExtra(action,response);
			LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
		}
	}
}


