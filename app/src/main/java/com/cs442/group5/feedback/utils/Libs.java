package com.cs442.group5.feedback.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.cs442.group5.feedback.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Created by sauja7 on 11/14/16.
 */

public  class Libs extends Application {
	static FirebaseAuth firebaseAuth;
	static FirebaseDatabase firebaseDatabase;
	static RequestQueue queue;
	private static Context mContext;
	static User user;
	public static FirebaseDatabase firebaseDatabaseInstance()
	{
		if(firebaseDatabase==null)
		{
			firebaseDatabase= FirebaseDatabase.getInstance();
			firebaseDatabase.setPersistenceEnabled(true);
		}
		return firebaseDatabase;
	}
	public static RequestQueue getQueueInstance()
	{
		if(queue==null) {
			queue = Volley.newRequestQueue(getContext());

		}
		return queue;
	}
	public static RetryPolicy getTimeoutPolicy(int timeout)
	{
		RetryPolicy policy = new DefaultRetryPolicy(timeout,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return policy;

	}
	public static FirebaseAuth getFirebaseAuth()
	{
		if(firebaseAuth==null)
			firebaseAuth= FirebaseAuth.getInstance();
		return firebaseAuth;
	}
	public static User getUser()
	{
		if(user==null) {
			user = new User();
		}
		else if(user.getUid()==null)
		{
			SharedPreferences sf=getContext().getSharedPreferences("user",MODE_PRIVATE);
			if(sf.contains("user"))
			{
				user=new Gson().fromJson(sf.getString("user",""),new TypeToken<User>(){}.getType());
			}
		}
		return user;
	}

	public static void setUser(User user) {
		Libs.user = user;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	public static Context getContext(){
		return mContext;
	}
	public static void saveUserToSharedPref(User u)
	{
		SharedPreferences sf=getContext().getSharedPreferences("user",MODE_PRIVATE);
		String tokenid=(sf.contains("tokenid")==true)?sf.getString("tokenid",""):null;
		u.setTokenid(tokenid);
		SharedPreferences.Editor edit=sf.edit();
		edit.putString("user",new Gson().toJson(u));
		//Date d=new Date();
		//Log.e(TAG, "saveToSharedPref: "+ d.getDate()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds());
		edit.commit();
	}
	public static boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
}
