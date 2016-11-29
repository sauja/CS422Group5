package com.cs442.group5.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
	private static final int GALLERY_INTENT = 2;
	CircleImageView profilepic;
	TextView name;
	TextView email;
	Button signout;
	FirebaseUser user;
	static String string_download_url;
	DatabaseReference db = FirebaseDatabase.getInstance().getReference();
	User u;
	Context context;


	ProgressDialog nProg;
	public final String TAG = "MyProfileActivity";
	private StorageReference mStorage;
	FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
context=this;
		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		profilepic = (CircleImageView) findViewById(R.id.profilepic);
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
		mStorage = FirebaseStorage.getInstance().getReference();
		nProg = new ProgressDialog(this);
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
	public void onChangeImage(View view)
	{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/n");
		startActivityForResult(intent, GALLERY_INTENT);
		nProg = new ProgressDialog(this);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
			nProg.setMessage("Uploading....");
			nProg.show();

			Uri uri = data.getData();
			StorageReference filepath = mStorage.child("UserProfilePic/" + mAuth.getCurrentUser().getUid());
			filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

					FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

					UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
							.setPhotoUri(taskSnapshot.getDownloadUrl())
							.build();
					Glide.with(context).load(taskSnapshot.getDownloadUrl()).into(profilepic);
					user.updateProfile(profileUpdates)
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										Log.d(TAG, "User profile updated.");

									}
								}
							});

					SharedPreferences sf=getSharedPreferences("user",MODE_PRIVATE);
					SharedPreferences.Editor edit=sf.edit();
					edit.putString("name",mAuth.getCurrentUser().getDisplayName());
					edit.putString("image",taskSnapshot.getDownloadUrl().toString());

					edit.putString("uid",mAuth.getCurrentUser().getUid());
					edit.commit();
					nProg.dismiss();
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {


				}
			}).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
					double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

				}
			}).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

				}
			});

		}
	}
}
