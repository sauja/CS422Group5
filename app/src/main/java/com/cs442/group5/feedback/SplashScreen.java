package com.cs442.group5.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cs442.group5.feedback.database.User.User;
import com.cs442.group5.feedback.database.User.UserDBHelper;

public class SplashScreen extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		UserDBHelper userDBHelper=new UserDBHelper(this);
		if(userDBHelper.getCount()==0)
		{
			User user=new User("Admin","admin");
			user.setEmail("admin@admin.com");
			user.setContactNo("123456789");
			userDBHelper.addUser(user);
		}
		Intent intent=new Intent(this,LoginActivity.class);
		startActivity(intent);
	}
}
