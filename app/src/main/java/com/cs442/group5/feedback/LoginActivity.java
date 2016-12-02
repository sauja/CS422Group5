/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cs442.group5.feedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs442.group5.feedback.model.User;
import com.cs442.group5.feedback.service.UserIntentService;
import com.cs442.group5.feedback.utils.Const;
import com.cs442.group5.feedback.utils.Libs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private Button signUp;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]
int userType;
    private GoogleApiClient mGoogleApiClient;
    //private TextView mStatusTextView;
    //private TextView mDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        //mStatusTextView = (TextView) findViewById(R.id.status);
        //mDetailTextView = (TextView) findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
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
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
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
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]


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
							userType= Const.USER_EMAIL;
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

    // [START on_start_add_listener]
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
    // [END on_stop_remove_listener]

    // [START onactivityresult]
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
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]

                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
	    userType=Const.USER_GMAIL;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
          //  mStatusTextView.setText( user.getEmail());
           // mDetailTextView.setText( user.getUid());


            User localUser= Libs.getUser();
            localUser.setUid(user.getUid());
            localUser.setEmail(user.getEmail());
            localUser.setProfileImageURL(null==user.getPhotoUrl()? "https://firebasestorage.googleapis.com/v0/b/feedback-87897.appspot.com/o/StoreImages%2Fdefault.png?alt=media&token=5bdb457e-f082-4839-b116-055af718213c":  user.getPhotoUrl().toString());
            localUser.setDisplayName(user.getDisplayName());
localUser.setUserType(userType);
            saveToSharedPref(localUser);
            startActivity(new Intent(this,DashBoardActivity.class));
            finish();

            //findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
           // mStatusTextView.setText("signedout");
           // mDetailTextView.setText(null);

            //findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }
    public void saveToSharedPref(User u)
    {
        SharedPreferences sf=getSharedPreferences("user",MODE_PRIVATE);
        String tokenid=(sf.contains("tokenid")==true)?sf.getString("tokenid",""):null;
        u.setTokenid(tokenid);
        SharedPreferences.Editor edit=sf.edit();
        edit.putString("user",new Gson().toJson(u));
	    Date d=new Date();
	    Log.e(TAG, "saveToSharedPref: "+ d.getDate()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds());
	    edit.commit();
	    Intent intent=new Intent(LoginActivity.this,UserIntentService.class);
	    intent.setAction(UserIntentService.UPDATE_USER);
	    intent.putExtra(UserIntentService.UPDATE_USER,new Gson().toJson(Libs.getUser()));
	    intent.putExtra("activity",TAG);
	    startService(intent);

    }
}
