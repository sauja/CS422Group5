package com.cs442.group5.feedback;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.cs442.group5.feedback.utils.RatingColor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mypopsy.maps.StaticMap;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener
{
	public static final String EXTRA_NAME = "cheese_name";
	private static final String TAG="StoreActivity";
	private Context context;
	RequestQueue queue;
	//private FABToolbarLayout layout;
	TextView textView_name;
	TextView textView_address;
	TextView textView_Location;
	TextView textView_zipcode;
	TextView textView_phone_no;
	TextView textView_emailid;
	TextView textView_website;
	TextView textView_rating;
	String storeid="";
	ArrayList<Review> reviewList;
	Store store;

	static int count;
	static String string_download_url;
	private StorageReference mStorage;
	private DatabaseReference mdatabse;
	DatabaseReference df;
	ProgressDialog nProg;
	private static final int GALLERY_INTENT = 2;
	CollapsingToolbarLayout collapsingToolbar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		mdatabse = FirebaseDatabase.getInstance().getReference();
		mStorage = FirebaseStorage.getInstance().getReference();
		nProg = new ProgressDialog(this);
		df = FirebaseDatabase.getInstance().getReference("users").child("url");
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		context=this;
		Intent intent = getIntent();
		//layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
		//fab = (FloatingActionButton)findViewById(R.id.fabtoolbar_fab);
		//final String cheeseName = intent.getStringExtra(EXTRA_NAME);
		queue = Volley.newRequestQueue(this);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ImageView backdrop=(ImageView)findViewById(R.id.backdrop);

		collapsingToolbar =
				(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


		textView_name = (TextView) findViewById(R.id.textView_name);
		textView_address = (TextView) findViewById(R.id.textView_address);
		textView_Location = (TextView) findViewById(R.id.textView_location);
		textView_rating=(TextView) findViewById(R.id.textView_rating);
		if(getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("storeid"))
		{
			storeid=getIntent().getExtras().get("storeid").toString();

			getStore(storeid);
			getAllReviews(Integer.parseInt(storeid));

		}
		/*fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layout.show();
			}
		});*/
		Button btn_getDirections=(Button) findViewById(R.id.btn_getDirections);
		btn_getDirections.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(StoreActivity.this, DirectionsActivity.class);


				String sendMessage = store.getGpsLat()+","+store.getGpsLng();
				//put the text inside the intent and send it to another Activity
				intent.putExtra("address", sendMessage);
				//start the activity
				startActivity(intent);
			}
		});
	}
	public void onCall(View view)
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setMessage("Do you want to call "+store.getName())
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						dialoginterface.cancel();
					}})
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"+store.getPhone_no()));
						startActivity(callIntent);
					}
				}).show();

	}

	@Override
	public void onBackPressed() {
		//if(layout.isToolbar())
		//	layout.hide();
		//else
		finish();

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case android.R.id.home:
				finish();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		Toast.makeText(this, "Element clicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.sample_actions, menu);
		return true;
	}

	public void getStore(final String id)
	{
		final String url=context.getString(R.string.server_url)+"/store/getStore";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Gson gson=new Gson();
				Store store=gson.fromJson(response,new TypeToken<Store>() {}.getType());
				updateFields(store);
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("id", id);
				return parameters;
			}
		};
		queue.add(postRequest);
	}
	public void getAllReviews(final int id)
	{
		final String url=context.getString(R.string.server_url)+"/review/getAllReviews";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Gson gson=new Gson();

				reviewList=gson.fromJson(response,new TypeToken<ArrayList<Review>>() {}.getType());
				updateReviewFields();

			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(id));
				return parameters;
			}
		};
		queue.add(postRequest);
	}
	public void addReview(final Review r)
	{
		Log.e(TAG, "addReview: "+r.getStoreid()+"\n"
				+r.getUid()+"\n"+r.getComment());
		final String url=context.getString(R.string.server_url)+"/review/addReview";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: "+response );
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("storeid", String.valueOf(r.getStoreid()));
				parameters.put("uid", r.getUid());
				parameters.put("comment", r.getComment());
				parameters.put("rating",String.valueOf( r.getRating()));
				return parameters;
			}
		};
		queue.add(postRequest);
	}
	public void updateFields(Store store){
		if(store!=null&&store.getName().length()>0)
		{
			this.store=store;
			textView_name.setText(store.getName());
			textView_address.setText(store.getAddress()
					+"\n"+store.getLocation()
					+"\n"+store.getPhone_no()
					+"\n"+store.getWebsite());
			textView_Location.setText(store.getLocation());
			collapsingToolbar.setTitle(store.getName());
			textView_rating.setText(String.valueOf(store.getRating()).substring(0,3));
			int color= RatingColor.getRatingColor(store.getRating(),context);
			((GradientDrawable)textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable)textView_rating.getBackground()).setColor( color);
			if(store.getAddress()!=null&&store.getAddress().length()>0)
			{
				ImageView imageView_staticMap=(ImageView)findViewById(R.id.imageView_staticMap);
				StaticMap map = new StaticMap()
						.center(store.getAddress()+" "+store.getLocation())
						.marker(StaticMap.Marker.Style.RED, new StaticMap.GeoPoint(store.getAddress()+" "+store.getLocation()))
						.size(320, 240);
				try {
					Glide.with(StoreActivity.this).load(map.toURL())
							.into(imageView_staticMap);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void addPhotos(View view)
	{
		Toast.makeText(context, "Add Photos", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/n");
		startActivityForResult(intent,GALLERY_INTENT);
	}
	public void addReview(View view)
	{
		Toast.makeText(context, "addReview", Toast.LENGTH_SHORT).show();
	}
	public void rateMe(View view)
	{
		Toast.makeText(context, "rateMe", Toast.LENGTH_SHORT).show();
		final Dialog openDialog = new Dialog(context);
		openDialog.setContentView(R.layout.content_store_add_review);
		openDialog.setTitle("Custom Dialog Box");

		Button btn_close = (Button)openDialog.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openDialog.cancel();
			}
		});
		Button btn_add = (Button)openDialog.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				SharedPreferences sharedpreferences=getSharedPreferences("user", Context.MODE_PRIVATE);
				if(sharedpreferences.contains("uid")) {
					RatingBar ratingBar = (RatingBar) openDialog.findViewById(R.id.ratingBar);
					EditText editText_comment = (EditText) openDialog.findViewById(R.id.editText_comment);
					Review r = new Review();
					r.setComment(editText_comment.getText().toString());
					r.setStoreid(Integer.parseInt(storeid));
					r.setRating(ratingBar.getRating());

					r.setUid(sharedpreferences.getString("uid","")) ;
					addReview(r);
				}
				else
					Toast.makeText(context, "Please login again", Toast.LENGTH_SHORT).show();
				openDialog.dismiss();
			}
		});
		openDialog.show();
	}



	private void updateReviewFields() {

		LinearLayout item = (LinearLayout) findViewById(R.id.ll_store);
		for(Review r:reviewList) {

			CardView child =(CardView) getLayoutInflater().inflate(R.layout.content_store_review, null);
			TextView textView_name = (TextView) child.findViewById(R.id.textView_name);
			textView_name.setText(r.getUid());
			TextView textView_date = (TextView) child.findViewById(R.id.textView_date);
			;
			textView_date.setText(new SimpleDateFormat("MMM dd, yyyy").format(r.getTimestamp()));
			TextView textView_rating = (TextView) child.findViewById(R.id.textView_rating);
			textView_rating.setText((String.valueOf(r.getRating())).substring(0,3));
			int color= RatingColor.getRatingColor(r.getRating(),context);
			((GradientDrawable)textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable)textView_rating.getBackground()).setColor( color);
			TextView textView_comment = (TextView) child.findViewById(R.id.textView_comment);
			textView_comment.setText(r.getComment());
			item.addView(child);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==GALLERY_INTENT && resultCode == RESULT_OK){
			nProg.setMessage("Uploading....");
			nProg.show();
			System.out.println("shivani");
			Uri uri = data.getData();
			count = count + 1;
			System.out.println(uri);
			StorageReference filepath = mStorage.child("StoreImages/"+store.getId()).child(uri.getLastPathSegment());
			filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
					string_download_url = taskSnapshot.getDownloadUrl().toString();
					mdatabse.child("StoreImages").child(String.valueOf(store.getId())).child(String.valueOf(count)).setValue(string_download_url);
					System.out.println("Success");
					Toast.makeText(StoreActivity.this, "Upload Done", Toast.LENGTH_SHORT).show();
					nProg.dismiss();
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					System.out.println("Failure "+e);

				}
			}).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
					double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
					System.out.println("Upload is " + progress + "% done");
				}
			}).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
					System.out.println("Upload is paused");
				}
			});

		}
	}


}
