package com.cs442.group5.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.cs442.group5.feedback.model.Store;
import com.cs442.group5.feedback.utils.RatingColor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class DashBoardActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	private static final String TAG = "DashBoardActivity";
	Context context;
	RequestQueue queue;
	ArrayList<Store> myStores;
	GridView gridview_myStores;
	MyStoreDashboardArrayAdapter arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_dash_board);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		context=this;

		gridview_myStores=(GridView)findViewById(R.id.gridview_dashboard);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		View headerView = navigationView.getHeaderView(0);
		queue = Volley.newRequestQueue(this);
		getAllStores();
		if(myStores!=null)
		{
			arrayAdapter=new MyStoreDashboardArrayAdapter(context,myStores);
			gridview_myStores.setAdapter(arrayAdapter);
			arrayAdapter.notifyDataSetChanged();
		}
		Log.e(TAG, "onCreate: " );
		gridview_myStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent=new Intent(context,StoreActivity.class);
				intent.putExtra("storeid",myStores.get(i).getId());
				startActivity(intent);

			}
		});








	}

	private class MyStoreDashboardArrayAdapter extends ArrayAdapter<Store>
	{
		private  class ViewHolder {
			ImageView imageView_img;
			TextView textView_rating;
			TextView textView_name;
			TextView textView_address;
			TextView textView_tags;
		}
		public MyStoreDashboardArrayAdapter(Context context, ArrayList<Store> store) {
			super(context, R.layout.store_list_item, store);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			Store store = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			ViewHolder viewHolder; // view lookup cache stored in tag
			if (convertView == null) {

				// If there's no view to re-use, inflate a brand new view for row
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.store_list_item, parent, false);
				viewHolder.textView_rating = (TextView) convertView.findViewById(R.id.textView_rating);
				viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
				viewHolder.textView_address = (TextView) convertView.findViewById(R.id.textView_address);
				viewHolder.textView_tags = (TextView) convertView.findViewById(R.id.textView_tags);
				viewHolder.imageView_img = (ImageView) convertView.findViewById(R.id.imageView_img);

				// Cache the viewHolder object inside the fresh view
				convertView.setTag(viewHolder);
			} else {
				// View is being recycled, retrieve the viewHolder object from tag
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// Populate the data from the data object via the viewHolder object
			// into the template view.
			viewHolder.textView_rating.setText((String.valueOf(store.getRating())).substring(0,3));
			int color= RatingColor.getRatingColor(store.getRating(),context);
			((GradientDrawable)viewHolder.textView_rating.getBackground()).setStroke(10, color);
			((GradientDrawable)viewHolder.textView_rating.getBackground()).setColor( color);
			viewHolder.textView_address.setText(store.getAddress()+"\n"+store.getLocation());
			viewHolder.textView_tags.setText(store.getTags());
			viewHolder.textView_name.setText(store.getName());
			// Return the completed view to render on screen
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent=new Intent(context,StoreActivity.class);
					Log.e(TAG, "onClick: "+myStores.get(position).getId() );
					intent.putExtra("storeid",myStores.get(position).getId());

					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	public void getAllStores()
	{
		final String url=context.getString(R.string.server_url)+"/store/getAllStores";
		Log.e(TAG, "getMyStores: " );
		StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "onResponse: " );
				if(response!=null&&response.length()>0) {
					Gson gson=new Gson();
					myStores=gson.fromJson(response,new TypeToken<ArrayList<Store>>() {}.getType());
					arrayAdapter=new MyStoreDashboardArrayAdapter(context,myStores);
					gridview_myStores.setAdapter(arrayAdapter);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: " );
				if(error!=null)
					Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
				Log.e("error",error.toString());
			}
		});

		queue.add(postRequest);

	}

	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		} else
		{
			super.onBackPressed();
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dash_board, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}*/

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		Intent intent;
		switch (id)
		{
			case R.id.myprofile:
				intent = new Intent(this, MyProfileActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				//finish();
				break;
			case R.id.dashboard:
				break;
			case R.id.new_form:
				intent = new Intent(this, NewFormActivity.class);
				startActivity(intent);
				break;
			case R.id.my_stores:
				intent = new Intent(this, MyStoreActivity.class);
				startActivity(intent);
				break;
			case R.id.logout:
				FirebaseAuth mAuth=FirebaseAuth.getInstance();
				mAuth.signOut();
				intent = new Intent(this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
				break;
			case R.id.TandC:
				intent = new Intent(this, TermsNConditions.class);
				intent.putExtra("webviewName", "TermsNConditions");
				startActivity(intent);
				break;
			case R.id.aboutUs:
				intent = new Intent(this, TermsNConditions.class);
				intent.putExtra("webviewName", "AboutUs");
				startActivity(intent);
				break;
		}


		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

}
