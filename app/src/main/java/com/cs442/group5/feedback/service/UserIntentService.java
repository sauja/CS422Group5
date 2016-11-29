package com.cs442.group5.feedback.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sauja7 on 11/26/16.
 */

public class UserIntentService extends IntentService {
	private static final String TAG = "UserIntentService";
	Context context;
	public UserIntentService(String name) {
		super(name);
	}
	public static final String UPDATE_USER = "updateUser";
	@Override
	protected void onHandleIntent(Intent intent) {
		context=this;
		if (intent != null) {
			final String action = intent.getAction();
			switch(action)
			{
			}
		}
	}
}
