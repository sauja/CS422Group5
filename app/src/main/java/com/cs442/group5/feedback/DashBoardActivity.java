package com.cs442.group5.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class DashBoardActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	GridView gridView;
	TextView textView_userName;
	TextView textView_email;
	ArrayList<String> itemList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_dash_board);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		/*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});*/

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		View headerView = navigationView.getHeaderView(0);
		textView_userName = (TextView) headerView.findViewById(R.id.textView_userName);
		textView_email = (TextView) headerView.findViewById(R.id.textView_email);
		ImageView imageView=(ImageView)headerView.findViewById(R.id.imageView_profile);
		final Context context=this;
		imageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(context, MyProfileActivity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				//finish();
			}
		});
		ImageView imageView4=(ImageView) findViewById(R.id.imageView4);
		imageView4.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent=new Intent(context,StoreActivity.class);
				startActivity(intent);
			}
		});



		/*if (getIntent().getExtras().containsKey("user"))
		{
			User user = User.getInstance();
			if (user != null)
			{
				textView_userName.setText(user.getUserName());
				textView_email.setText(user.getEmail());
			}
		}*/

		/*GridView gridView = (GridView) findViewById(R.id.gridView_dasboard_list);
		gridView.setClickable(true);
		for (int i = 1; i < 26; i++)
			itemList.add("Form " + i);
		MenuItemListAdapter arrayAdapter = new MenuItemListAdapter(this, itemList);
		gridView.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();
*/

	}

	/*private class MenuItemListAdapter extends ArrayAdapter<String>
	{
		Context context;
		private final ArrayList<String> menuList;

		public MenuItemListAdapter(Context context, ArrayList<String> items)
		{
			super(context, 0, items);
			this.context = context;
			this.menuList = items;
		}

		@Override
		public int getCount()
		{
			return menuList.size();
		}

		@Override
		public String getItem(int pos)
		{
			return menuList.get(pos);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View v = convertView;
			if (v == null)
			{
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.form_list_item, null);
			}
			String item = getItem(position);
			View itemView = v;
			TextView textView_id = (TextView) v.findViewById(R.id.textView_formName);
			textView_id.setText(item);
			return itemView;
		}
	}*/

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
			case R.id.new_store:
				intent = new Intent(this, NewStoreActivity.class);
				startActivity(intent);
				break;
			case R.id.logout:
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
