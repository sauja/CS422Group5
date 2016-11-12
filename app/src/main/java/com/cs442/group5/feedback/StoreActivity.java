package com.cs442.group5.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.cs442.group5.feedback.model.Store;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mypopsy.maps.StaticMap;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener
{
	public static final String EXTRA_NAME = "cheese_name";
	private static final String TAG="StoreActivity";
	private Context context;
	RequestQueue queue;
	private FABToolbarLayout layout;
	TextView textView_name;
	TextView textView_address;
	TextView textView_Location;
	TextView textView_zipcode;
	TextView textView_phone_no;
	TextView textView_emailid;
	TextView textView_website;
	private FloatingActionButton fab;
	Store store;
	CollapsingToolbarLayout collapsingToolbar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		context=this;
		Intent intent = getIntent();
		layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
		fab = (FloatingActionButton)findViewById(R.id.fabtoolbar_fab);
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
		if(getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("storeid"))
		{
			getStore(getIntent().getExtras().get("storeid").toString());

		}
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layout.show();
			}
		});
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

	@Override
	public void onBackPressed() {
		if(layout.isToolbar())
			layout.hide();
		else
			finish();

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
}
