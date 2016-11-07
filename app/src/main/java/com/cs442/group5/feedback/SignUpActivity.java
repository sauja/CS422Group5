package com.cs442.group5.feedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.cs442.group5.feedback.database.User.User;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sauja7 on 10/16/16.
 */

public class SignUpActivity extends AppCompatActivity
{
	EditText editText_fname;
	EditText editText_lname;
	EditText editText_email;
	EditText editText_userName;
	EditText editText_password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_signup);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		editText_fname = (EditText) findViewById(R.id.editText_fname);
		editText_lname = (EditText) findViewById(R.id.editText_lname);
		editText_email = (EditText) findViewById(R.id.editText_emailid);
		editText_userName = (EditText) findViewById(R.id.editText_userName);
		editText_password = (EditText) findViewById(R.id.editText_password);

	}

	public void onSignUp(View view)
	{
		User user;

		if (editText_email.getText() != null && editText_fname.getText() != null && editText_lname != null && editText_userName != null && editText_password.getText() != null)
		{
			user = new User(editText_userName.getText().toString(), editText_password.getText().toString());

			user.setEmail(editText_email.getText().toString());

			/*switch (i)
			{
				case Constants.USER_ALREADY_REGISTERED:
					Toast.makeText(this, "User is already registered", Toast.LENGTH_SHORT).show();
					break;
				case Constants.USER_ADDED:
					Intent intent = new Intent(this, DashBoardActivity.class);
					intent.putExtra("user", user);
					startActivity(intent);
					finish();
					break;
			}*/

		}
	}
}
