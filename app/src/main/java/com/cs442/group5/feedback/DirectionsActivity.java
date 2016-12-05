package com.cs442.group5.feedback;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cs442.group5.feedback.utils.DirectionsJSONParser;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {



	GoogleMap mgoogleMap;
	double lat, lng;
	LatLng user_ll, dest_ll;
	GoogleApiClient mGoogleApiClient = null;
	// Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
	//       mGoogleApiClient);
	Geocoder geocoder = null;
	String userLocation,locality;

	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);
		Intent intent = getIntent();
		Bundle extras = getIntent().getExtras();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		String location = intent.getStringExtra("address");

		//String location =  extras.getString("address");
		Geocoder gc = new Geocoder(this);
		List<Address> list = null;
		try {
			list = gc.getFromLocationName(location, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(list==null||list.size()==0)
		{
			Toast.makeText(this, "Incorrect Location/ Network Error", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		Address address = list.get(0);
		locality = address.getLocality();

		Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

		// destination latitude, longitude
		lat = address.getLatitude();
		lng = address.getLongitude();
		dest_ll = new LatLng(lat, lng);


		// user latitude, longitude
		// Enable MyLocation Button in the Map
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
		// map.setMyLocationEnabled(true);

		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
					.addApi(LocationServices.API)
					.build();
		}
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}



		if (googleServciesAvailable()) {
			Toast.makeText(this, "Google maps connected", Toast.LENGTH_SHORT).show();

			initMap();


		} else {
			// no google maps supported
		}



		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	private void initMap() {
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
		mapFragment.getMapAsync(this);
	}

	private String getDirectionsUrl(LatLng user_ll, LatLng dest_ll) {
       /* // Origin of route
        String str_origin = "origin=" + String.valueOf(user_ll.latitude) + "," + String.valueOf(user_ll.longitude);

        // Destination of route
        String str_dest = "destination=" + String.valueOf(dest_ll.latitude) + "," + String.valueOf(dest_ll.longitude);
        */

		// Origin of route
		String str_origin = "origin=" + user_ll.latitude + "," + user_ll.longitude;

		// Destination of route
		String str_dest = "destination=" + dest_ll.latitude + "," + dest_ll.longitude;



		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


		return url;
	}


	public boolean googleServciesAvailable() {
		GoogleApiAvailability api = GoogleApiAvailability.getInstance();
		int isAvailable = api.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (api.isUserResolvableError(isAvailable)) {
			Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
			dialog.show();
		} else {
			Toast.makeText(this, "Can't connect to play services", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mgoogleMap = googleMap;


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
		mgoogleMap.setMyLocationEnabled(true);

	}

	private void gotoLocation(double lat, double lng) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
		mgoogleMap.moveCamera(update);
	}


	private void gotoLocationzoom(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		//CameraUpdate upd = CameraUpdateFactory.
		mgoogleMap.moveCamera(update);
	}



	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/**
	 * A method to download json data from url
	 */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/**
	 * A class to parse the Google Places in JSON format
	 */
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";


			if (result.size() < 1) {
				Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
				return;
			}


			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) {    // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(5);
				lineOptions.color(Color.BLACK);

			}

			//tvDistance.setText("Distance:" + distance + ", Duration:" + duration);
			TextView tvDistance = (TextView) findViewById(R.id.tvDistance);
			TextView tvDuration = (TextView) findViewById(R.id.tvDuration);
			tvDistance.setText(distance);
			tvDuration.setText(duration);
			// Drawing polyline in the Google Map for the i-th route
			mgoogleMap.addPolyline(lineOptions);


			// zoom the path
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(user_ll);
			builder.include(dest_ll);
			LatLngBounds bounds = builder.build();

			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 70);
			mgoogleMap.animateCamera(cu);

			// add markers to source and destination
			Marker marker1 = mgoogleMap.addMarker(new MarkerOptions()
					.position(user_ll)
					.title(userLocation));


			Marker marker2 = mgoogleMap.addMarker(new MarkerOptions()
					.position(dest_ll)
					.title(locality));

		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mapTypeNormal:
				mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
			case R.id.mapTypeSattelite:
				mgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break;
			case R.id.mapTypeTerrain:
				mgoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
			case R.id.mapTypeHybrid:
				mgoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				break;
			case android.R.id.home:
				finish();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {



		// get user location
		double mylat, mylng;
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
		Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		geocoder = new Geocoder(this, Locale.getDefault());
		if (mLastLocation != null) {
			mylat = mLastLocation.getLatitude();
			mylng = mLastLocation.getLongitude();

			user_ll = new LatLng(mylat, mylng);

			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(mylat, mylng, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			userLocation = addresses.get(0).getAddressLine(0);

		}



		// draw the line
		// Getting URL to the Google Directions API
		String url = getDirectionsUrl(user_ll, dest_ll);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(url);


	}

	@Override
	public void onConnectionSuspended(int i) {

	}


	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("GoogleMaps Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();


		client.connect();
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	@Override
	public void onStop() {
		super.onStop();


		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}

}
