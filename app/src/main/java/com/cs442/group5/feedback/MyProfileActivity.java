package com.cs442.group5.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs442.group5.feedback.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyProfileActivity extends AppCompatActivity {

	ImageView profilepic;
	TextView name;
	TextView email;
	Button signout;
	FirebaseUser user;
	DatabaseReference db = FirebaseDatabase.getInstance().getReference();
	User u;
	public final String TAG = "Signout: ";

	FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		profilepic = (ImageView) findViewById(R.id.profilepic);
		name = (TextView) findViewById(R.id.nameData);
		email = (TextView) findViewById(R.id.emailData);
		signout = (Button) findViewById(R.id.signout);

		mAuth = FirebaseAuth.getInstance();

		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				Log.d(TAG, "onAuthStateChanged");
				user = firebaseAuth.getCurrentUser();
				if (user != null) {

				} else {
					Log.d(TAG, "onAuthStateChanged:signed_out");
					Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
		};

		db.child("user").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						// Get user value
						User user = dataSnapshot.getValue(User.class);
						u = user;
						name.setText(user.getfName() + user.getlName());
						email.setText(user.getEmail());
						Picasso.with(MyProfileActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(profilepic);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Log.d(TAG, "User Database did not work");
					}
				});

		signout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mAuth.signOut();
			}
		});

	}

	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// handle arrow click here
		if (item.getItemId() == android.R.id.home)
		{
			finish(); // close this activity and return to preview activity (if there is any)
		}

		return super.onOptionsItemSelected(item);
	}
}
