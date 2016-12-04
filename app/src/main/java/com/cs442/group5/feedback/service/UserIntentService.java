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
import com.cs442.group5.feedback.LoginActivity;
import com.cs442.group5.feedback.MyProfileActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.utils.Libs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sauja7 on 11/26/16.
 */

public class UserIntentService extends IntentService {
	private static final String TAG = "UserIntentService";
	public static final String UPDATE_USER = "updateUser";
	Context context;
	String broadcastClass=null;

	public UserIntentService(String name) {
		super(name);
	}
	public UserIntentService() {
		super("UserIntentService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		context = this;
		if (intent != null) {
			Log.e(TAG, "onHandleIntent: Action:" + intent.getAction());
			final String action = intent.getAction();
			switch (action) {
				case UPDATE_USER:
					broadcastClass = intent.getStringExtra("activity");
					Log.e(TAG, "onHandleIntent: UPDATE_USER:" + intent.getStringExtra(UPDATE_USER));
					updateUser(intent.getStringExtra(UPDATE_USER));
					break;
			}
		}
	}
	public void updateUser(final String user)
	{
		final String url=context.getString(R.string.server_url)+"/user/updateUser";
		Log.e(TAG, "updateUser: " );
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: " );
				if(response!=null&&response.length()>0)
				{
					Toast.makeText(context, "User updated", Toast.LENGTH_LONG).show();
					sendStoreBroadcast(response,UPDATE_USER);
				}
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: " );
				if(error!=null)
					Toast.makeText(context, "User update failed", Toast.LENGTH_LONG).show();
				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("user", user);
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
			case "LoginActivity":
				broadcastIntent=new Intent(UserIntentService.this,LoginActivity.class);
				break;
			case "MyProfileActivity":
				broadcastIntent=new Intent(UserIntentService.this,MyProfileActivity.class);
				break;
		}

		if(broadcastIntent!=null){
			broadcastIntent.setAction(action);
			broadcastIntent.putExtra(action,response);
			LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
		}
	}
}
