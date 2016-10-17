package com.cs442.group5.feedback;

import android.content.Intent;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs442.group5.feedback.database.User.User;
import com.cs442.group5.feedback.database.User.UserDBHelper;

public class LoginActivity extends AppCompatActivity
{
	EditText editText_userName;
	EditText editText_password;
	Button btn_login;
	TextView textView_signUp;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		editText_userName=(EditText) findViewById(R.id.editText_userName);
		editText_password=(EditText) findViewById(R.id.editText_password);
		btn_login=(Button) findViewById(R.id.btn_login);
		textView_signUp=(TextView) findViewById(R.id.textView_signUp);

	}
	public void onLogin(View view)
	{
		if(editText_userName.getText()!=null && editText_password.getText()!=null)
		{
			User user=new User(editText_userName.getText().toString(),editText_password.getText().toString());
			UserDBHelper userDBHelper=new UserDBHelper(this);
			if(userDBHelper.checkCredential(user)!=null)
			{
				Intent intent=new Intent(this,DashBoardActivity.class);
				intent.putExtra("user",user);
				startActivity(intent);
				finish();
			}
			else
				Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
		}
	}
	public void onSignUp(View view)
	{
		Intent intent=new Intent(this,SignUpActivity.class);
		startActivity(intent);
	}
}
