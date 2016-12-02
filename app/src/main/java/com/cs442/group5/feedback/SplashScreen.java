package com.cs442.group5.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.cs442.group5.feedback.notification.MyFirebaseInstanceIDService;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
		startService(intent);
		Intent intent2 = new Intent(this, LoginActivity.class);
		startActivity(intent2);
		onNewIntent(getIntent());
	}
	@Override
	protected void onNewIntent(Intent intent) {
		String action = intent.getAction();
		String data = intent.getDataString();
		if (Intent.ACTION_VIEW.equals(action) && data != null) {
			String recipeId = data.substring(data.lastIndexOf("/") + 1);
			Toast.makeText(this, recipeId, Toast.LENGTH_SHORT).show();
			Intent storeIntent=new Intent(SplashScreen.this,StoreActivity.class);
			storeIntent.putExtra("storeid",recipeId);
			startActivity(storeIntent);
		}
	}
}
