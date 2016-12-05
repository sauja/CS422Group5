package com.cs442.group5.feedback;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.LocalBroadcastManager;
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
import com.cs442.group5.feedback.model.Review;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.model.question.FeedbackForm;
import com.cs442.group5.feedback.notification.MyFirebaseNotification;
import com.cs442.group5.feedback.service.FormIntentService;
import com.cs442.group5.feedback.service.ReviewIntentService;
import com.cs442.group5.feedback.service.StoreIntentService;
import com.cs442.group5.feedback.utils.CustomSwipeAdapter;
import com.cs442.group5.feedback.utils.Libs;
import com.cs442.group5.feedback.utils.RatingColor;
import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.PermissionsRefusedListener;
import com.github.jksiezni.permissive.Permissive;
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

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
	MenuItem bookmark;
	ProgressDialog nProg;
	RequestQueue queue;
	Store store;
	FeedbackForm feedbackForm;
	private ShareActionProvider miShareAction;
	String storeid=null;
	ArrayList<Review> reviewList;
	private static final int GALLERY_INTENT = 2;
	CollapsingToolbarLayout collapsingToolbar;

	ArrayList<String> imgList;

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

		if(getIntent().getExtras()!=null)
			storeid = getIntent().getExtras().getString("storeid");
		if(storeid==null)
			storeid=getIntent().getExtras().get("storeid").toString();

		Log.e(TAG, "onCreate: storeid "+storeid);
		mdatabse = FirebaseDatabase.getInstance().getReference();
		mStorage = FirebaseStorage.getInstance().getReference();
		nProg = new ProgressDialog(this);
		addAllBroadcastListners();
	}

	@Override
	protected void onResume() {
		Log.e(TAG, "onResume: "+storeid );
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(MyFirebaseNotification.REVIEW_NOTIFICATION_ID);
		SharedPreferences sf=getSharedPreferences("store",MODE_PRIVATE);
		if(sf.contains("storeData"+storeid))
		{
			Log.e(TAG, "onResume: Restore Store" );
			store=new Gson().fromJson(sf.getString("storeData"+storeid,null),new TypeToken<Store>(){}.getType());

			updateFields(store);
		}
		else    refreshStore();
		if(sf.contains("imglist"+storeid))
		{
			Log.e(TAG, "onResume: Restore Imagelist" );
			imgList=new Gson().fromJson(sf.getString("imglist"+storeid,null),new TypeToken<ArrayList<String>>(){}.getType());
			customSwipeAdapter = new CustomSwipeAdapter(context, imgList);
			viewPager.setAdapter(customSwipeAdapter);
			customSwipeAdapter.notifyDataSetChanged();
		}
		else refreshImageList();
		if(sf.contains("form"+storeid))
			feedbackForm=new Gson().fromJson(sf.getString("form"+storeid,null),new TypeToken<FeedbackForm>(){}.getType());
		else refreshForm();
		if(sf.contains("review"+storeid))
		{
			//Log.e(TAG, "onResume: Restore Review list "+store.getName() );
			reviewList=new Gson().fromJson(sf.getString("review"+storeid,null),new TypeToken<ArrayList<Review>>() {}.getType());
			updateReviewFields();
		}
		else refreshReviewList();
		super.onResume();
	}

	@Override
	protected void onPause() {
		SharedPreferences.Editor edit=getSharedPreferences("store",MODE_PRIVATE).edit();
		if(store!=null)
			edit.putString("storeData"+storeid,new Gson().toJson(store));
		if(imgList!=null&&imgList.size()>0)
			edit.putString("imglist"+storeid,new Gson().toJson(imgList));
		if(reviewList!=null&&reviewList.size()>0)
			edit.putString("review"+storeid,new Gson().toJson(reviewList));
		if(feedbackForm!=null)
			edit.putString("form"+storeid,new Gson().toJson(feedbackForm));
		edit.commit();
		super.onPause();
	}

	public  void refreshAll()
	{
		refreshStore();
		refreshImageList();
		refreshForm();
		refreshReviewList();
	}
	public void refreshForm()
	{
		Intent intent=new Intent(StoreActivity.this,FormIntentService.class);
		intent.putExtra(FormIntentService.GET_FORM,storeid);
		intent.setAction(FormIntentService.GET_FORM);
		intent.putExtra("activity",TAG);
		startService(intent);

	}
	public void refreshImageList()
	{
		DatabaseReference df;
		df = FirebaseDatabase.getInstance().getReference("StoreImages/" + storeid);
		df.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				imgList = new ArrayList<>();
				for (DataSnapshot dsp : dataSnapshot.getChildren())
					imgList.add(String.valueOf(dsp.getValue()));
				customSwipeAdapter = new CustomSwipeAdapter(context, imgList);
				viewPager.setAdapter(customSwipeAdapter);
				customSwipeAdapter.notifyDataSetChanged();
				Log.e("On Data changed", "onDataChange: " + imgList.size());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});

	}
	public void refreshReviewList()
	{
		Intent intent=new Intent(StoreActivity.this,ReviewIntentService.class);
		intent.putExtra(ReviewIntentService.GET_ALL_REVIEWS,storeid);
		intent.setAction(ReviewIntentService.GET_ALL_REVIEWS);
		intent.putExtra("activity",TAG);
		startService(intent);

	}
	public void addAllBroadcastListners()
	{

		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						store=new Gson().fromJson(intent.getStringExtra(StoreIntentService.GET_STORE),new TypeToken<Store>() {}.getType());
						Log.e(TAG, "onReceive: "+new Gson().toJson(store) );
						updateFields(store);

					}
				}, new IntentFilter(StoreIntentService.GET_STORE)
		);
		Log.e(TAG, "onCreate: 123456789 "+storeid);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						feedbackForm=new Gson().fromJson(intent.getStringExtra(FormIntentService.GET_FORM), new TypeToken<FeedbackForm>() {}.getType());
						Log.e(TAG, "onReceive: "+new Gson().toJson(feedbackForm) );
					}
				}, new IntentFilter(FormIntentService.GET_FORM)
		);

		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						reviewList=new Gson().fromJson(intent.getStringExtra(ReviewIntentService.GET_ALL_REVIEWS),new TypeToken<ArrayList<Review>>() {}.getType());
						updateReviewFields();
					}
				}, new IntentFilter(ReviewIntentService.GET_ALL_REVIEWS)
		);

		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String response=intent.getStringExtra(StoreIntentService.TOGGLE_BOOKMARK);
						//updateFields(store);
						if(response!=null)
						{
							if(response.contains("booktrue"))
							{	Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show();
								bookmark.setIcon(R.drawable.bookmark_check);}
							if(response.contains("bookfalse"))
							{Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
								bookmark.setIcon(R.drawable.bookmark);}
						}
						Log.e(TAG, "onReceive: TOGGLE_BOOKMARK "+response );

					}
				}, new IntentFilter(StoreIntentService.TOGGLE_BOOKMARK)
		);
	}
	public void toggleBookmark()
	{
		Intent intent=new Intent(StoreActivity.this,StoreIntentService.class);
		intent.putExtra(StoreIntentService.TOGGLE_BOOKMARK,storeid);
		intent.putExtra("activity",TAG);
		intent.setAction(StoreIntentService.TOGGLE_BOOKMARK);
		startService(intent);

	}
	public void refreshStore()
	{
		Intent intent=new Intent(StoreActivity.this,StoreIntentService.class);
		intent.putExtra(StoreIntentService.GET_STORE,storeid);
		intent.putExtra("activity",TAG);
		intent.setAction(StoreIntentService.GET_STORE);
		startService(intent);


	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.e(TAG, "onNewIntent: "+intent.getStringExtra("storeid") );
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_store, menu);
		bookmark = menu.findItem(R.id.action_bookmark);
		// Fetch reference to the share action provider

		updateFields(store);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle arrow click here

		switch (item.getItemId())
		{
			case R.id.action_refresh:
				Log.e(TAG, "onOptionsItemSelected: Refresh" );
				refreshAll();
				break;
			case android.R.id.home:
				finish();
				break;
			case R.id.action_bookmark:
				if(storeid!=null)
					toggleBookmark();
				break;
			case R.id.action_share:
				if(store!=null){
					final ImageView imageView=new ImageView(this);
					final String uri="http://"+getString(R.string.host)+getString(R.string.path)+"/"+store.getId();
					Bitmap myBitmap = QRCode.from(uri).bitmap();
					imageView.setImageBitmap(myBitmap);
					Uri bmpUri = getLocalBitmapUri(imageView);
					Intent shareIntent = new Intent();
					shareIntent.putExtra(Intent.EXTRA_TEXT, uri);
					shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
					shareIntent.setType("image/*");
					shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					startActivity(Intent.createChooser(shareIntent, "Send"));
					/*Glide.with(context).load(store.getImgurl()).asBitmap().thumbnail(0.1f).into(new SimpleTarget<Bitmap>(100,100) {
						@Override
						public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
							imageView.setImageBitmap(resource); // Possibly runOnUiThread()
							Intent shareIntent = new Intent();
							shareIntent.setAction(Intent.ACTION_SEND);

							shareIntent.putExtra(Intent.EXTRA_TEXT, uri);

							Uri bmpUri = getLocalBitmapUri(imageView);

							shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
							shareIntent.setType("image/*");
							shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							startActivity(Intent.createChooser(shareIntent, "Send"));
						}
					});*/
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
		//if(store!=null) {
		int j = 2;
		LinearLayout item = (LinearLayout) findViewById(R.id.ll_review);
		item.removeAllViews();
		Collections.sort(reviewList);
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
					intent.putExtra("storeid", storeid);
					startActivity(intent);
					Toast.makeText(context, "More Reviews", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//}
		//else
		//	Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
	}

	public void rateMe(View view) {
		//Toast.makeText(context, "rateMe", Toast.LENGTH_SHORT).show();
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
		if(store!=null) {
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
		else Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show();
	}
	public void onAddReview(View view)
	{
		if(feedbackForm==null)
		{
			rateMe(new View(context));
		}
		else {
			Intent intent=new Intent(StoreActivity.this,AddReviewActivity.class);
			intent.putExtra("storeid",storeid);
			Log.e(TAG, "onAddReview: "+new Gson().toJson(feedbackForm) );
			intent.putExtra("structure",new Gson().toJson(feedbackForm));
			startActivity(intent);
		}
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
				refreshReviewList();
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

			if(store.getIsBookmarked()!=null&&bookmark!=null)
			{
				Log.e(TAG, "updateFields: update Toggle bookmark" );
				if(store.getIsBookmarked().contains("true"))
					bookmark.setIcon(R.drawable.bookmark_check);
				else
					bookmark.setIcon(R.drawable.bookmark);
				collapsingToolbar.setTitle(store.getName());
			}
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

		final Intent intent=new Intent(StoreActivity.this,DirectionsActivity.class);

		if(store!=null&&Libs.haveNetworkConnection()){
			new Permissive.Request(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)
					.whenPermissionsGranted(new PermissionsGrantedListener() {
						@Override
						public void onPermissionsGranted(String[] permissions) throws SecurityException {
							intent.putExtra("address",store.getAddress()+","+store.getLocation());
							startActivity(intent);
						}
					})
					.whenPermissionsRefused(new PermissionsRefusedListener() {
						@Override
						public void onPermissionsRefused(String[] permissions) {
							Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
						}
					})
					.execute(this);
		}
		else
		{
			Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
		}
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
		if(store!=null&&store.getPhone_no().length()>7)
			new Permissive.Request(Manifest.permission.CALL_PHONE)
					.whenPermissionsGranted(new PermissionsGrantedListener() {
						@Override
						public void onPermissionsGranted(String[] permissions) throws SecurityException {
							String uri = "tel:" + store.getPhone_no();
							final Intent intent = new Intent(Intent.ACTION_CALL);
							intent.setData(Uri.parse(uri));
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
					})
					.whenPermissionsRefused(new PermissionsRefusedListener() {
						@Override
						public void onPermissionsRefused(String[] permissions) {
							Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
						}
					})
					.execute(this);
	}
}

