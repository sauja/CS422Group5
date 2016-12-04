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
import com.cs442.group5.feedback.DashBoardActivity;
import com.cs442.group5.feedback.FormPreviewActivity;
import com.cs442.group5.feedback.MyStorePageActivity;
import com.cs442.group5.feedback.NewFormActivity;
import com.cs442.group5.feedback.NewStoreActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.StoreActivity;
import com.cs442.group5.feedback.utils.Libs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sauja7 on 12/2/16.
 */

public class FormIntentService extends IntentService {
	private static final String TAG = "FormIntentService";
	public static final String CREATE_FORM = "createForm";
	public static final String GET_FORM = "getForm";
	Context context;
	String broadcastClass;
	public FormIntentService() {
		super("FormIntentService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		context = this;
		if (intent != null) {
			final String action = intent.getAction();
			broadcastClass = intent.getStringExtra("activity");
			switch (action) {

				case CREATE_FORM:
					broadcastClass = intent.getStringExtra("activity");
					Log.e(TAG, "onHandleIntent: CREATE_FORM storeid " + intent.getStringExtra(CREATE_FORM));
					String storeid=intent.getStringExtra("storeid");
					String formname=intent.getStringExtra("formname");
					String data=intent.getStringExtra("structure");
					createForm(storeid,formname,data);
					break;
				case GET_FORM:
					broadcastClass = intent.getStringExtra("activity");
					String storeid2=intent.getStringExtra(GET_FORM);
					getForm(storeid2);
					break;
			}
		}
	}
	public void createForm(final String storeid,final String formName, final String jsonItems)
	{


		final String url=context.getString(R.string.server_url)+"/form/addForm";
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {

				if(response.equals("1062"))
					Toast.makeText(context, "Form already exists.", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: "+response );
				sendStoreBroadcast(response,CREATE_FORM);
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("name", formName);
				parameters.put("structure", jsonItems);
				parameters.put("storeid", storeid);
				Log.e(TAG, "getParams: "+jsonItems );
				return parameters;
			}
		};
		Libs.getQueueInstance().add(postRequest);
	}
	public void getForm(final String storeid)
	{
		Log.e(TAG, "getForm: "+storeid );
		final String url=context.getString(R.string.server_url)+"/form/getForm";
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				sendStoreBroadcast(response,GET_FORM);
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", storeid);
				return parameters;
			}
		};

		Libs.getQueueInstance().add(postRequest);
	}
	private void sendStoreBroadcast(String response,String action)
	{
		Intent broadcastIntent=null;
		switch (broadcastClass)
		{
			case "FormPreviewActivity":
				broadcastIntent=new Intent(FormIntentService.this,FormPreviewActivity.class);
				break;
			case "MyStorePageActivity":
				broadcastIntent=new Intent(FormIntentService.this,MyStorePageActivity.class);
				break;
			case "NewStoreActivity":
				broadcastIntent=new Intent(FormIntentService.this,NewStoreActivity.class);
				break;
			case "DashBoardActivity":
				broadcastIntent=new Intent(FormIntentService.this,DashBoardActivity.class);
				break;
			case "StoreActivity":
				broadcastIntent=new Intent(FormIntentService.this,StoreActivity.class);
				break;
			case "NewFormActivity":
				broadcastIntent=new Intent(FormIntentService.this,NewFormActivity.class);
				break;
		}
		Log.e(TAG, "sendStoreBroadcast: "+broadcastClass +" "+response);
		if(broadcastIntent!=null){
			broadcastIntent.setAction(action);
			broadcastIntent.putExtra(action,response);
			LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
		}
	}
}
