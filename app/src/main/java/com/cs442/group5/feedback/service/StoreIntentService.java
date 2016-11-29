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
import com.cs442.group5.feedback.NewStoreActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.utils.Libs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sauja7 on 11/26/16.
 */

public class StoreIntentService extends IntentService {
	private static final String TAG = "StoreIntentService";
	Context context;
	String broadcastClass=null;
	public static final String GET_STORE = "getStore";
	public static final String UPDATE_STORE = "updateStore";
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
			switch(action)
			{
				case GET_STORE:
					broadcastClass=intent.getStringExtra("activity");
					Log.e(TAG, "onHandleIntent: GET_STORE:" +intent.getStringExtra(GET_STORE));
					getStore(intent.getStringExtra(GET_STORE));
					break;
				case UPDATE_STORE:
					broadcastClass=intent.getStringExtra("activity");
					Log.e(TAG, "onHandleIntent: UPDATE_STORE:" +intent.getStringExtra(UPDATE_STORE));
					Store store=new Gson().fromJson(intent.getStringExtra(UPDATE_STORE),new TypeToken<Store>() {}.getType());
					updateStore(store);
					break;
			}
		}
	}

	public void updateStore(final Store store) {
		final String url = context.getString(R.string.server_url) + "/store/updateStore";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Intent broadcastIntent=null;
				switch (broadcastClass)
				{
					case "NewStoreActivity":
						broadcastIntent=new Intent(StoreIntentService.this,NewStoreActivity.class);
						broadcastIntent.setAction(UPDATE_STORE);
						broadcastIntent.putExtra(UPDATE_STORE,response);
						break;
				}

				if(broadcastIntent!=null)
					LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);

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
		Libs.getQueueInstance().add(postRequest);
	}

	public void getStore(final String id) {
		final String url = context.getString(R.string.server_url) + "/store/getStore";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Intent broadcastIntent=null;
				switch (broadcastClass)
				{
					case "NewStoreActivity":
						broadcastIntent=new Intent(StoreIntentService.this,NewStoreActivity.class);
						broadcastIntent.setAction(GET_STORE);
						broadcastIntent.putExtra(GET_STORE,response);
						break;
				}

				if(broadcastIntent!=null)
					LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);

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
				return parameters;
			}
		};
		Libs.getQueueInstance().add(postRequest);
	}
	public void addStore(final Store store)
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
				parameters.put("store", new Gson().toJson(store));
				return parameters;
			}
		};
			Libs.getQueueInstance().add(postRequest);

	}
}
