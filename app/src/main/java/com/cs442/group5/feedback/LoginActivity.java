package com.cs442.group5.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

	}
	public void onLogin(View view)
	{
		Intent intent=new Intent(this,DashBoardActivity.class);
		startActivity(intent);


	}
	public void onSignUp(View view)
	{
		Intent intent=new Intent(this,SignUpActivity.class);
		startActivity(intent);
	}
}
