package com.cs442.group5.feedback;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cs442.group5.feedback.model.Review;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.service.ReviewIntentService;
import com.cs442.group5.feedback.service.StoreIntentService;
import com.cs442.group5.feedback.utils.CustomSwipeAdapter;
import com.cs442.group5.feedback.utils.Libs;
import com.cs442.group5.feedback.utils.RatingColor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mypopsy.maps.StaticMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreActivity extends AppCompatActivity {
	private static final String TAG = "StoreActivity";
	ViewPager viewPager;
	CustomSwipeAdapter customSwipeAdapter;
	Context context;
	static String string_download_url;
	private StorageReference mStorage;
	private DatabaseReference mdatabse;
	ProgressDialog nProg;
	RequestQueue queue;
	Store store;
	private ShareActionProvider miShareAction;

	ArrayList<Review> reviewList;
	private static final int GALLERY_INTENT = 2;
	CollapsingToolbarLayout collapsingToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		context = this;
		queue = Volley.newRequestQueue(this);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// add back arrow to toolbar
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

		viewPager = (ViewPager) findViewById(R.id.viewPager_storeImages);
		DatabaseReference df;
		final String storeid =getIntent().getExtras().get("storeid").toString();
		df = FirebaseDatabase.getInstance().getReference("StoreImages/" + storeid);
		df.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				ArrayList<String> Userlist = new ArrayList<>();
				for (DataSnapshot dsp : dataSnapshot.getChildren())
					Userlist.add(String.valueOf(dsp.getValue()));
				customSwipeAdapter = new CustomSwipeAdapter(context, Userlist);
				viewPager.setAdapter(customSwipeAdapter);
				customSwipeAdapter.notifyDataSetChanged();
				Log.e("On Data changed", "onDataChange: " + Userlist.size());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});


		Intent intent=new Intent(StoreActivity.this,StoreIntentService.class);
		intent.putExtra(StoreIntentService.GET_STORE,storeid);
		intent.putExtra("activity",TAG);
		intent.setAction(StoreIntentService.GET_STORE);
		startService(intent);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						store=new Gson().fromJson(intent.getStringExtra(StoreIntentService.GET_STORE),new TypeToken<Store>() {}.getType());
						updateFields(store);

					}
				}, new IntentFilter(StoreIntentService.GET_STORE)
		);
		Log.e(TAG, "onCreate: 123456789 "+storeid);


		intent=new Intent(StoreActivity.this,ReviewIntentService.class);
						intent.putExtra(ReviewIntentService.GET_ALL_REVIEWS,storeid);
						intent.setAction(ReviewIntentService.GET_ALL_REVIEWS);
						intent.putExtra("activity",TAG);
						startService(intent);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						reviewList=new Gson().fromJson(intent.getStringExtra(ReviewIntentService.GET_ALL_REVIEWS),new TypeToken<ArrayList<Review>>() {}.getType());
						updateReviewFields();
					}
				}, new IntentFilter(ReviewIntentService.GET_ALL_REVIEWS)
		);


		mdatabse = FirebaseDatabase.getInstance().getReference();
		mStorage = FirebaseStorage.getInstance().getReference();
		nProg = new ProgressDialog(this);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_store, menu);
		MenuItem item = menu.findItem(R.id.action_share);
		// Fetch reference to the share action provider
		miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

		return true;
	}

	public void getAllReviews(final long storeid) {
		final String url = context.getString(R.string.server_url) + "/review/getAllReviews";

		Log.e(TAG, "getAllReviews: "+storeid );
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: getAllReviews "+storeid+" "+response );
				reviewList=new Gson().fromJson(response,new TypeToken<ArrayList<Review>>() {}.getType());
				updateReviewFields();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error", error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(storeid));
				return parameters;
			}
		};
		Libs.getQueueInstance().add(postRequest);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle arrow click here
		if (item.getItemId() == android.R.id.home) {
			finish(); // close this activity and return to preview activity (if there is any)
		}
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				break;
			case R.id.action_bookmark:
				break;
			case R.id.action_share:
				if(store!=null){
					final ImageView imageView=new ImageView(this);
					Glide.with(context).load(store.getImgurl()).asBitmap().thumbnail(0.1f).into(new SimpleTarget<Bitmap>(100,100) {
						@Override
						public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
							imageView.setImageBitmap(resource); // Possibly runOnUiThread()
							Intent shareIntent = new Intent();
							shareIntent.setAction(Intent.ACTION_SEND);
							String uri="http://"+getString(R.string.host)+getString(R.string.path)+"/"+store.getId();
							shareIntent.putExtra(Intent.EXTRA_TEXT, uri);

							Uri bmpUri = getLocalBitmapUri(imageView);
							shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
							shareIntent.setType("image/*");
							shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							startActivity(Intent.createChooser(shareIntent, "Send"));
						}
					});
				}
		break;
	}

	return super.onOptionsItemSelected(item);
}

	public Uri getLocalBitmapUri(ImageView imageView) {
		// Extract Bitmap from ImageView drawable
		Drawable drawable = imageView.getDrawable();
		Bitmap bmp = null;
		if (drawable instanceof BitmapDrawable){
			bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		} else {
			return null;
		}
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			// Use methods on Context to access package-specific directories on external storage.
			// This way, you don't need to request external read/write permission.
			// See https://youtu.be/5xVh-7ywKpE?t=25m25s
			File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			// **Warning:** This will fail for API > 24, use a FileProvider as shown below instead.
			bmpUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmpUri;
	}
	private void updateReviewFields() {
		Log.e(TAG, "updateReviewFields: " );
		int j = 2;
		LinearLayout item = (LinearLayout) findViewById(R.id.ll_store);
		if (reviewList.size() < 2)
			j = reviewList.size();
		for (int i = 0; i < j; i++) {
			Review r = reviewList.get(i);
			CardView child = (CardView) getLayoutInflater().inflate(R.layout.content_store_review, null);
			TextView textView_name = (TextView) child.findViewById(R.id.textView_name);
			TextView textView_date = (TextView) child.findViewById(R.id.textView_date);
			TextView textView_rating = (TextView) child.findViewById(R.id.textView_rating);
			TextView textView_comment = (TextView) child.findViewById(R.id.textView_comment);
			CircleImageView profile_image = (CircleImageView) child.findViewById(R.id.profile_image);
			textView_name.setText(r.getFullname());
			textView_date.setText(new SimpleDateFormat("MMM dd, yyyy").format(r.getTimestamp()));
			textView_rating.setText((String.valueOf(r.getRating())).substring(0, 3));
			int color = RatingColor.getRatingColor(r.getRating(), context);
			((GradientDrawable) textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable) textView_rating.getBackground()).setColor(color);
			textView_comment.setText(r.getComment());
			Glide.with(Libs.getContext()).load(r.getImgurl()).into(profile_image);

			item.addView(child);
		}
		if (reviewList.size() > 2) {
			Button button = new Button(this, null, R.style.BorderlessButton);
			button.setText("More Reviews");
			button.setHeight((int) getResources().getDimension(R.dimen.button_height));
			button.setGravity(Gravity.RIGHT | Gravity.CENTER);
			item.addView(button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(StoreActivity.this, MoreReviewsActivity.class);
					intent.putExtra("reviewList", new Gson().toJson(reviewList));
					intent.putExtra("storeid", store.getId());
					startActivity(intent);
					Toast.makeText(context, "More Reviews", Toast.LENGTH_SHORT).show();
				}
			});
		}


	}

	public void rateMe(View view) {
		Toast.makeText(context, "rateMe", Toast.LENGTH_SHORT).show();
		final Dialog openDialog = new Dialog(context);
		openDialog.setContentView(R.layout.content_store_add_review);
		openDialog.setTitle("Custom Dialog Box");

		Button btn_close = (Button) openDialog.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openDialog.cancel();
			}
		});
		Button btn_add = (Button) openDialog.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				RatingBar ratingBar = (RatingBar) openDialog.findViewById(R.id.ratingBar);
				EditText editText_comment = (EditText) openDialog.findViewById(R.id.editText_comment);
				Review r = new Review();
				r.setComment(editText_comment.getText().toString());
				r.setStoreid(store.getId());
				r.setRating(ratingBar.getRating());

				r.setUid(Libs.getUser().getUid());
				addReview(r);

				openDialog.dismiss();
			}
		});
		openDialog.show();
	}

	public void addReview(final Review r) {
		Log.e(TAG, "addReview: " + r.getStoreid() + "\n"
				+ r.getUid() + "\n" + r.getComment());
		final String url = context.getString(R.string.server_url) + "/review/addReview";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: " + response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error", error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(r.getStoreid()));
				parameters.put("uid", r.getUid());
				parameters.put("comment", r.getComment());
				parameters.put("rating", String.valueOf(r.getRating()));
				return parameters;
			}
		};
		queue.add(postRequest);
	}

	public void addPhotos(View view) {
		Toast.makeText(context, "Add Photos", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/n");
		startActivityForResult(intent, GALLERY_INTENT);
	}



	public void updateFields(Store store) {
		if (store != null && store.getName().length() > 0) {
			TextView textView_name = (TextView) findViewById(R.id.textView_name);
			TextView textView_address = (TextView) findViewById(R.id.textView_address);
			TextView textView_Location = (TextView) findViewById(R.id.textView_location);
			TextView textView_rating = (TextView) findViewById(R.id.textView_rating);
			this.store = store;
			textView_name.setText(store.getName());
			textView_address.setText(store.getAddress()
					+ "\n" + store.getLocation()
					+ "\n" + store.getPhone_no()
					+ "\n" + store.getWebsite());
			textView_Location.setText(store.getLocation());
			collapsingToolbar.setTitle(store.getName());
			collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
			textView_rating.setText(String.valueOf(store.getRating()).substring(0, 3));
			int color = RatingColor.getRatingColor(store.getRating(), context);
			((GradientDrawable) textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable) textView_rating.getBackground()).setColor(color);
			if (store.getAddress() != null && store.getAddress().length() > 0) {
				ImageView imageView_staticMap = (ImageView) findViewById(R.id.imageView_staticMap);
				StaticMap map = new StaticMap()
						.center(store.getAddress() + " " + store.getLocation())
						.marker(StaticMap.Marker.Style.RED, new StaticMap.GeoPoint(store.getAddress() + " " + store.getLocation()))
						.size(320, 240);
				try {
					Glide.with(this).load(map.toURL())
							.into(imageView_staticMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getDirections(View view) {
		Toast.makeText(context, "get directions", Toast.LENGTH_SHORT).show();
		Intent intent=new Intent(StoreActivity.this,DirectionsActivity.class);
		intent.putExtra("address",store.getAddress()+","+store.getLocation());
		startActivity(intent);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
			nProg.setMessage("Uploading....");
			nProg.show();

			Uri uri = data.getData();
			StorageReference filepath = mStorage.child("StoreImages/" + store.getId()).child(uri.getLastPathSegment());
			filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					string_download_url = taskSnapshot.getDownloadUrl().toString();
					mdatabse.child("StoreImages/" + store.getId()).child(String.valueOf(UUID.randomUUID())).setValue(string_download_url);

					Toast.makeText(StoreActivity.this, "Upload Done", Toast.LENGTH_SHORT).show();
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

	public void onCallButton(View view) {

		String uri = "tel:" + store.getPhone_no();
		final Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse(uri));
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		new AlertDialog.Builder(context)
				.setTitle("Call "+store.getName())
				.setMessage("Are you sure you want to call this store?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(intent);
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();

	}
}

