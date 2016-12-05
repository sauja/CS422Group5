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
import com.cs442.group5.feedback.BookMarkActivity;
import com.cs442.group5.feedback.DashBoardActivity;
import com.cs442.group5.feedback.MyStorePageActivity;
import com.cs442.group5.feedback.NewStoreActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.StoreActivity;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.utils.Libs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sauja7 on 11/26/16.
 */

public class StoreIntentService extends IntentService {
	private static final String TAG = "StoreIntentService";
	Context context;
	String broadcastClass=null;
	public static final String ADD_STORE= "addStore";
	public static final String GET_STORE = "getStore";
	public static final String UPDATE_STORE = "updateStore";
	public static final String GET_ALL_STORES = "getAllStores";
	public static final String GET_BOOKMARKED_STORES = "getBookmarkedStores";
	public static final String TOGGLE_BOOKMARK="toggleBookmark";
	public StoreIntentService(String name) {
		super(name);
	}
	public StoreIntentService() {
		super("StoreIntentService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		context=this;
		if (intent != null) {
			Log.e(TAG, "onHandleIntent: Action:" +intent.getAction());
			final String action = intent.getAction();
			broadcastClass=intent.getStringExtra("activity");
			switch(action)
			{
				case ADD_STORE:
					Log.e(TAG, "onHandleIntent: ADD_STORE:" +intent.getStringExtra(ADD_STORE));
					addStore(intent.getStringExtra(ADD_STORE));
					break;
				case GET_STORE:
					Log.e(TAG, "onHandleIntent: GET_STORE:" +intent.getStringExtra(GET_STORE));
					getStore(intent.getStringExtra(GET_STORE));
					break;
				case TOGGLE_BOOKMARK:
					Log.e(TAG, "onHandleIntent: TOGGLE_BOOKMARK:" +intent.getStringExtra(TOGGLE_BOOKMARK));
					toggleBookmark(intent.getStringExtra(TOGGLE_BOOKMARK));
					break;
				case UPDATE_STORE:
					Log.e(TAG, "onHandleIntent: UPDATE_STORE:" +intent.getStringExtra(UPDATE_STORE));
					Store store=new Gson().fromJson(intent.getStringExtra(UPDATE_STORE),new TypeToken<Store>() {}.getType());
					updateStore(store);
					break;
				case GET_ALL_STORES:
					getAllStores();
					break;
				case GET_BOOKMARKED_STORES:
					getBookmarkedStores();
					break;
			}
		}
	}
	public void getAllStores()
	{
		final String url=context.getString(R.string.server_url)+"/store/getAllStores";
		Log.e(TAG, "getMyStores: " );
		StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: " );
				sendStoreBroadcast(response,GET_ALL_STORES);

			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: " );
				if(error!=null)
					Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
				Log.e("error",error.toString());
			}
		});
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);

	}
	public void getBookmarkedStores()
	{
		final String url=context.getString(R.string.server_url)+"/store/getBookmarkedStores";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				sendStoreBroadcast(response,GET_BOOKMARKED_STORES);

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
				parameters.put("uid",Libs.getUser().getUid() );
				return parameters;
			}
		};
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);

	}
	public void updateStore(final Store store) {
		final String url = context.getString(R.string.server_url) + "/store/updateStore";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				sendStoreBroadcast(response,UPDATE_STORE);

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
				parameters.put("store", new Gson().toJson(store));
				return parameters;
			}
		};
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);
	}

	public void toggleBookmark(final String storeid) {
		final String url = context.getString(R.string.server_url) + "/store/toggleBookmark";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				sendStoreBroadcast(response,TOGGLE_BOOKMARK);
				Log.e(TAG, "onResponse: "+response );
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
				parameters.put("storeid", storeid);
				parameters.put("uid", Libs.getUser().getUid());
				return parameters;
			}
		};
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);
	}

	public void getStore(final String id) {
		final String url = context.getString(R.string.server_url) + "/store/getStore";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				sendStoreBroadcast(response,GET_STORE);
				Log.e(TAG, "onResponse: "+response );
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
				parameters.put("id", id);
				parameters.put("uid",Libs.getUser().getUid());
				return parameters;
			}
		};
		postRequest.setRetryPolicy(Libs.getTimeoutPolicy(30000));
		Libs.getQueueInstance().add(postRequest);
	}
	public void addStore(final String store)
	{
		final String url=context.getString(R.string.server_url)+"/store/addStore";
		Log.e(TAG, "addStore: " );
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: " );
				if(response!=null&&response.length()>0)
				{
					Toast.makeText(context, "Store added successfully", Toast.LENGTH_LONG).show();
				}
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: " );
				if(error!=null)
					Toast.makeText(context, "Store not added!", Toast.LENGTH_LONG).show();
				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("store", store);
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
			case "BookMarkActivity":
				broadcastIntent=new Intent(StoreIntentService.this,BookMarkActivity.class);
				break;
			case "NewStoreActivity":
				broadcastIntent=new Intent(StoreIntentService.this,NewStoreActivity.class);
				break;
			case "DashBoardActivity":
				broadcastIntent=new Intent(StoreIntentService.this,DashBoardActivity.class);
				break;
			case "MyStorePageActivity":
				broadcastIntent=new Intent(StoreIntentService.this,MyStorePageActivity.class);
				break;
			case "StoreActivity":
				broadcastIntent=new Intent(StoreIntentService.this,StoreActivity.class);
				break;
		}

		if(broadcastIntent!=null){
			broadcastIntent.setAction(action);
			broadcastIntent.putExtra(action,response);
			LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
		}
	}
}
