package com.cs442.group5.feedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity
{
	private static final String TAG = "LoginActivity";
	EditText editText_userName;
	EditText editText_password;
	Button btn_login;
	TextView textView_signUp;
	public ProgressDialog mProgressDialog;
	// [START declare_auth]
	private FirebaseAuth mAuth;
	// [END declare_auth]

	// [START declare_auth_listener]
	private FirebaseAuth.AuthStateListener mAuthListener;
	// [END declare_auth_listener]

	public void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(true);
		}

		mProgressDialog.show();
	}

	public void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_login);
		editText_userName = (EditText) findViewById(R.id.editText_userName);
		editText_password = (EditText) findViewById(R.id.editText_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		textView_signUp = (TextView) findViewById(R.id.textView_signUp);
		mAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					// User is signed in


					Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
				} else {
					// User is signed out
					Log.d(TAG, "onAuthStateChanged:signed_out");
				}
				// [START_EXCLUDE]
				hideProgressDialog();

				// [END_EXCLUDE]
			}
		};
	}

	public void onLogin(View view)
	{
		if (editText_userName.getText() != null && editText_password.getText() != null)
		{
			signIn(editText_userName.getText().toString(), editText_password.getText().toString());
			/*User user = new User(editText_userName.getText().toString(), editText_password.getText().toString());
			UserDBHelper userDBHelper = new UserDBHelper(this);
			if (userDBHelper.checkCredential(user) != null)
			{
				Intent intent = new Intent(this, DashBoardActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
				finish();
			} else
				Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();*/
		}
	}

	public void onSignUp(View view)
	{
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
		hideProgressDialog();
	}
	private boolean validateForm() {
		boolean valid = true;

		String email = editText_userName.getText().toString();
		if (TextUtils.isEmpty(email)) {
			editText_userName.setError("Required.");
			valid = false;
		} else {
			editText_userName.setError(null);
		}

		String password = editText_password.getText().toString();
		if (TextUtils.isEmpty(password)) {
			editText_password.setError("Required.");
			valid = false;
		} else {
			editText_password.setError(null);
		}

		return valid;
	}
	private void signIn(String email, String password) {
		Log.d(TAG, "signIn:" + email);
		if (!validateForm()) {
			return;
		}
		showProgressDialog();

		// [START sign_in_with_email]
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

						// If sign in fails, display a message to the user. If sign in succeeds
						// the auth state listener will be notified and logic to handle the
						// signed in user can be handled in the listener.
						if (!task.isSuccessful()) {
							Log.w(TAG, "signInWithEmail:failed", task.getException());
							Toast.makeText(LoginActivity.this, "Authentification Failed",
									Toast.LENGTH_SHORT).show();
						}


						hideProgressDialog();
						Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
						//intent.putExtra("user", new User(editText_userName.getText().toString(),editText_password.getText().toString()));
						startActivity(intent);
						finish();
						// [END_EXCLUDE]
					}
				});
		// [END sign_in_with_email]
	}
}
