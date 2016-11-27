package com.cs442.group5.feedback;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements  GoogleApiClient.OnConnectionFailedListener {
	private final String TAG = "LoginActivity";
	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private Button signUp;
	Context context;
	private static final int CONTACT_PERMISSION_REQUEST_CODE = 1;
	private static final int RC_SIGN_IN = 9001;
	private boolean mPermissionDenied = false;

	private FirebaseAuth mAuth;
	// [END declare_auth]

	// [START declare_auth_listener]
	private FirebaseAuth.AuthStateListener mAuthListener;
	// [END declare_auth_listener]

	private GoogleApiClient mGoogleApiClient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context=this;
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		// [START initialize_auth]
		mAuth = FirebaseAuth.getInstance();
		getUser();
		SignInButton signInButton=(SignInButton) findViewById(R.id.btn_googleLogin);
		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(context, "Google login", Toast.LENGTH_SHORT).show();
				//enableContactsPermission();
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
				startActivityForResult(signInIntent, RC_SIGN_IN);
			}
		});

		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		signUp = (Button) findViewById(R.id.signup);

		signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});
		Button mEmailSignInButton = (Button) findViewById(R.id.btn_emailLogin);
		mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
			}
		});
	}

	private void getUser() {
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					SharedPreferences sf=getSharedPreferences("user",MODE_PRIVATE);
					SharedPreferences.Editor edit=sf.edit();
					edit.putString("name",user.getDisplayName());
					if (user.getPhotoUrl()!=null)
						edit.putString("image",user.getPhotoUrl().toString());
					else {
						int resId=R.mipmap.ic_launcher;
						Resources resources = context.getResources();
						edit.putString("image",Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) ).toString());
						//Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) );
					}
					edit.putString("uid",user.getUid());
					edit.commit();
					Intent intent=new Intent(LoginActivity.this,DashBoardActivity.class);
					startActivity(intent);
					// User is signed in
					//Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
				} else {
					// User is signed out
					//Log.d(TAG, "onAuthStateChanged:signed_out");
				}
				// [START_EXCLUDE]
				//updateUI(user);
				// [END_EXCLUDE]
			}
		};
	}

	public void signIn(final String email, final String password) {
		if(email!=null&&password!=null&&email.length()>0&&password.length()>0)
			mAuth.signInWithEmailAndPassword(email, password)
					.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

							if (!task.isSuccessful()) {
								Log.w(TAG, "signInWithEmail", task.getException());
								Toast.makeText(LoginActivity.this, "Authentication failed.",
										Toast.LENGTH_SHORT).show();
								mPasswordView.setError("Password is incorrect or user not found");
								return;
							}

							getUser();
						}
					});
		else
			Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show();


	}
	public void onResetPassword(View view) {
		if (mEmailView.getText().length() < 6)
			Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
		else {

			mAuth.sendPasswordResetEmail(mEmailView.getText().toString())
					.addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
							}


						}
					});
		}

	}



	public void onEmailLogin(View view)
	{

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}
	// [END on_start_add_listener]

	// [START on_stop_remove_listener]
	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if (result.isSuccess()) {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = result.getSignInAccount();
				firebaseAuthWithGoogle(account);
			} else {
				// Google Sign In failed, update UI appropriately
				// [START_EXCLUDE]
				Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
				// [END_EXCLUDE]
			}
		}
	}
	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		//Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
		// [START_EXCLUDE silent]
		showProgressDialog();
		// [END_EXCLUDE]

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						//Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

						// If sign in fails, display a message to the user. If sign in succeeds
						// the auth state listener will be notified and logic to handle the
						// signed in user can be handled in the listener.
						if (!task.isSuccessful()) {
							//Log.w(TAG, "signInWithCredential", task.getException());
							Toast.makeText(LoginActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
							return;
						}
						// [START_EXCLUDE]
						hideProgressDialog();
						// [END_EXCLUDE]
						getUser();
					}
				});
	}

	public void updateUser(final String uuid, final String tokenid)
	{
		RequestQueue queue= Volley.newRequestQueue(this);

		final String url=context.getString(R.string.server_url)+"/user/updateUser";
		Log.d(TAG, "updateUser: " );
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: " +response);


			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: " );
				if(error!=null)

					Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("uid", uuid);
				parameters.put("address", tokenid);
				return parameters;
			}
		};
		queue.add(postRequest);
	}


}

