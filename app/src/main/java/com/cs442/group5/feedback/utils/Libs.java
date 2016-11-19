package com.cs442.group5.feedback.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by sauja7 on 11/14/16.
 */

public  class Libs {
	static FirebaseAuth firebaseAuth;
	static FirebaseDatabase firebaseDatabase;
	static RequestQueue queue;
	 Context context;
	public static FirebaseDatabase firebaseDatabaseInstance()
	{
		if(firebaseDatabase==null)
		{
			firebaseDatabase= FirebaseDatabase.getInstance();
			firebaseDatabase.setPersistenceEnabled(true);
		}
		return firebaseDatabase;
	}
	public static RequestQueue getQueueInstance(Context context)
	{
		if(queue==null)
			queue= Volley.newRequestQueue(context);
		return queue;
	}
	public static FirebaseAuth getFirebaseAuth()
	{
		if(firebaseAuth==null)
			firebaseAuth= FirebaseAuth.getInstance();
		return firebaseAuth;
	}

}
