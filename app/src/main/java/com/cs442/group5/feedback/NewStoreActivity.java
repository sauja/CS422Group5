package com.cs442.group5.feedback;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs442.group5.feedback.model.Store;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;




public class NewStoreActivity extends AppCompatActivity implements OnMapReadyCallback{

	private static final String TAG=NewStoreActivity.class.getSimpleName();
	Location mLastLocation;
	LocationManager locationManager;
	private GoogleMap mMap;
	RequestQueue queue;
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
	LatLng gps = new LatLng(0.0, 0.0);
	String provider;
	Location location;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queue = Volley.newRequestQueue(this);
		setContentView(R.layout.activity_new_store);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		SmoothScrollMapFragment mapFragment = (SmoothScrollMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.content_new_store2);
		editText_name = (EditText) findViewById(R.id.editText_name);
		editText_address = (EditText) findViewById(R.id.editText_address);
		editText_Location = (EditText) findViewById(R.id.editText_Location);
		editText_zipcode = (EditText) findViewById(R.id.editText_zipcode);
		editText_phone_no = (EditText) findViewById(R.id.editText_phone_no);
		editText_emailid = (EditText) findViewById(R.id.editText_emailid);
		editText_website = (EditText) findViewById(R.id.editText_website);
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		provider = locationManager.getBestProvider(criteria, true);

		mapFragment.setListener(new SmoothScrollMapFragment.OnTouchListener() {
			@Override
			public void onTouch() {
				scrollView.requestDisallowInterceptTouchEvent(true);
			}
		});
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(validateFields()) {
					if(isEditable)
					{}
					else addStore();
				}
				else Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show();
			}
		});

		if(getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("storeid"))
		{
			getStore(getIntent().getExtras().get("storeid").toString());
			toolbar.setTitle("Edit Store");

		}
		if(getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("mode"))
			isEditable=true;


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
	public void addStore()
	{
		final String url=context.getString(R.string.server_url)+"/store/addStore";
		Log.e(TAG, "addStore: " );
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: " );
				if(response!=null&&response.length()>0)
					Toast.makeText(context, "Store added successfully", Toast.LENGTH_LONG).show();
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: " );
				if(error!=null)
					Toast.makeText(context, "Store not added!", Toast.LENGTH_LONG).show();
				Log.e("error",error.toString());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("name", editText_name.getText().toString());
				parameters.put("address", editText_address.getText().toString());
				parameters.put("location", editText_Location.getText().toString());
				parameters.put("zipcode", editText_zipcode.getText().toString());
				parameters.put("phone_no", editText_phone_no.getText().toString());
				parameters.put("emailid", editText_emailid.getText().toString());
				parameters.put("website", editText_website.getText().toString());
				parameters.put("gpsLat", String.valueOf(gps.latitude));
				parameters.put("gpsLng", String.valueOf(gps.longitude));
				parameters.put("ownerid", "1");
				return parameters;
			}
		};
		queue.add(postRequest);

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
		}
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

	private boolean validateFields()
	{
		if(editText_emailid.length()<1||editText_address.length()<1||editText_Location.length()<1
				||editText_name.length()<1||editText_website.length()<1||editText_zipcode.length()<1)
		return false;
		return true;
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
