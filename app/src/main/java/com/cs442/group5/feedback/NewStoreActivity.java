package com.cs442.group5.feedback;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.service.StoreIntentService;
import com.cs442.group5.feedback.utils.Libs;
import com.cs442.group5.feedback.utils.SmoothScrollMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;




public class NewStoreActivity extends AppCompatActivity implements OnMapReadyCallback{

	private static final String TAG="NewStoreActivity";
	Location mLastLocation;
	LocationManager locationManager;
	private GoogleMap mMap;
	private static final int GALLERY_INTENT = 2;
	ProgressDialog nProg;
	String id="";
	private Context context = this;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
	Store store;
	Boolean isEditable=false;
	EditText editText_name;
	EditText editText_address;
	EditText editText_Location;
	EditText editText_zipcode;
	EditText editText_phone_no;
	EditText editText_emailid;
	EditText editText_website;
	ImageView imageView_imgurl;
	LatLng gps = new LatLng(0.0, 0.0);
	String provider;
	Location location;

	private StorageReference mStorage;
	FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_store);
		store=new Store();
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		nProg = new ProgressDialog(this);
		mAuth = FirebaseAuth.getInstance();
		mStorage = FirebaseStorage.getInstance().getReference();
		if(getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("storeid"))
		{
			Intent intent=new Intent(NewStoreActivity.this,StoreIntentService.class);
			intent.putExtra(StoreIntentService.GET_STORE,getIntent().getExtras().get("storeid").toString());
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
			LocalBroadcastManager.getInstance(this).registerReceiver(
					new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							//Store store=new Gson().fromJson(intent.getStringExtra(StoreIntentService.UPDATE_STORE),new TypeToken<Store>() {}.getType());
							//updateFields(store);

							Intent intent1=new Intent(NewStoreActivity.this,MyStorePageActivity.class);
								Log.e(TAG, "go back: "+store.getId()+" "+ store.getName());
							intent.putExtra("storeid",store.getId());
							intent.putExtra("storename",store.getName());
							intent.putExtra("mode","EDIT");
							//startActivity(intent1);
							finish();
						}
					}, new IntentFilter(StoreIntentService.UPDATE_STORE)
			);
			toolbar.setTitle("Edit Store");
			isEditable = true;
		}




		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		SmoothScrollMapFragment mapFragment = (SmoothScrollMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.content_new_store2);
		editText_name = (EditText) findViewById(R.id.editText_name);
		editText_address = (EditText) findViewById(R.id.editText_address);
		editText_Location = (EditText) findViewById(R.id.editText_Location);
		editText_zipcode = (EditText) findViewById(R.id.editText_zipcode);
		editText_phone_no = (EditText) findViewById(R.id.editText_phone_no);
		editText_emailid = (EditText) findViewById(R.id.editText_emailid);
		editText_website = (EditText) findViewById(R.id.editText_website);
		imageView_imgurl=(ImageView)findViewById(R.id.imageView_imgurl);
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		provider = locationManager.getBestProvider(criteria, true);

		mapFragment.setListener(new SmoothScrollMapFragment.OnTouchListener() {
			@Override
			public void onTouch() {
				scrollView.requestDisallowInterceptTouchEvent(true);
			}
		});

	}
	public void onSaveButton(View view)
	{
		Toast.makeText(context, "on fab click", Toast.LENGTH_SHORT).show();
		Log.e(TAG, "onSaveButton: " );
		Log.e(TAG, "onClick: edit?:"+isEditable );
		if(validateFields()) {
			if(isEditable)
			{
				Log.e(TAG, "onClick: Update Store" );
				store.setName(editText_name.getText().toString());
				store.setAddress(editText_address.getText().toString());
				store.setLocation(editText_Location.getText().toString());
				store.setZipcode(editText_zipcode.getText().toString());
				store.setPhone_no(editText_phone_no.getText().toString());
				store.setEmailid(editText_emailid.getText().toString());
				store.setWebsite(editText_website.getText().toString());
				store.setGpsLat(String.valueOf(gps.latitude));
				store.setGpsLng(String.valueOf(gps.longitude));
				Log.e(TAG, "onClick: Update: "+new Gson().toJson(store));
				Intent intent=new Intent(NewStoreActivity.this,StoreIntentService.class);
				intent.putExtra(StoreIntentService.UPDATE_STORE,new Gson().toJson(store));
				intent.putExtra("activity",TAG);
				intent.setAction(StoreIntentService.UPDATE_STORE);
				startService(intent);
			}
			else {
				Log.e(TAG, "onClick: Add Store" );
				store.setName(editText_name.getText().toString());
				store.setAddress(editText_address.getText().toString());
				store.setLocation(editText_Location.getText().toString());
				store.setZipcode(editText_zipcode.getText().toString());
				store.setPhone_no(editText_phone_no.getText().toString());
				store.setEmailid(editText_emailid.getText().toString());
				store.setWebsite(editText_website.getText().toString());
				store.setGpsLat(String.valueOf(gps.latitude));
				store.setGpsLng(String.valueOf(gps.longitude));
				store.setOwnerID(Libs.getUser().getUid());
				Intent intent=new Intent(NewStoreActivity.this,StoreIntentService.class);
				intent.setAction(StoreIntentService.ADD_STORE);
				intent.putExtra(StoreIntentService.ADD_STORE,new Gson().toJson(store));
				intent.putExtra("activity",TAG);
				startService(intent);



			}
		}
		else Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.

			return;
		}


		location = locationManager.getLastKnownLocation(provider);
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setCompassEnabled(true);

		//mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 13f));
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled( true );
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				return false;
			}
		});

		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				mMap.clear();
				mMap.addMarker(new MarkerOptions()
						.position(arg0).title(arg0.toString()));
				gps=arg0;
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arg0, mMap.getCameraPosition().zoom));
				Log.e("arg0", arg0.toString());
			}
		});
		if (location != null) {
			LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
			Log.e(TAG, "onMapReady: "+latLng );
			googleMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
		}
	}

	public void updateFields(Store store){
		if(store!=null&&store.getName().length()>0)
		{
			this.store=store;
			editText_name.setText(store.getName());
			editText_address.setText(store.getAddress());
			editText_Location.setText(store.getLocation());
			editText_zipcode.setText(store.getZipcode());
			editText_phone_no.setText(store.getPhone_no());
			editText_emailid.setText(store.getEmailid());
			editText_website.setText(store.getWebsite());
			try{Glide.with(context).load(store.getImgurl()).fitCenter().into(imageView_imgurl);}catch(Exception e){}
		}
	}


	private boolean validateFields()
	{
		if(editText_emailid.length()<1||editText_address.length()<1||editText_Location.length()<1
				||editText_name.length()<1||editText_website.length()<1||editText_zipcode.length()<1)
			return false;
		return true;
	}
	public void onChangeImage(View view)
	{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/n");
		startActivityForResult(intent, GALLERY_INTENT);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
			nProg.setMessage("Uploading....");
			nProg.show();

			Uri uri = data.getData();
			StorageReference filepath = mStorage.child("StoreTemp/" + mAuth.getCurrentUser().getUid()+"_"+ SystemClock.currentThreadTimeMillis());
			filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					Log.e(TAG, "onSuccess: "+taskSnapshot.getDownloadUrl().toString() );
					store.setImgurl(taskSnapshot.getDownloadUrl().toString());
					Glide.with(context).load(taskSnapshot.getDownloadUrl()).into(imageView_imgurl);
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
