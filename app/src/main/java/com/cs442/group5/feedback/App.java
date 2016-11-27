package com.cs442.group5.feedback;

import android.app.Application;
import android.content.Context;

/**
 * Created by sauja7 on 11/21/16.
 */

public class App extends Application {

	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	public static Context getContext(){
		return mContext;
	}
}