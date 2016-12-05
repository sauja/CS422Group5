package com.cs442.group5.feedback;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.cs442.group5.feedback.model.ReviewRatingCountChart;
import com.cs442.group5.feedback.notification.MyFirebaseNotification;
import com.cs442.group5.feedback.service.ReviewIntentService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MyStorePageActivity extends AppCompatActivity {
	private static String TAG="MyStorePageActivity";
	private ShareActionProvider mShareActionProvider;
	Context context;
	BarChart chart ;
	ArrayList<BarEntry> BARENTRY ;
	ArrayList<String> BarEntryLabels ;
	BarDataSet Bardataset ;
	BarData BARDATA ;
	String storeid;
	String storename=null;
	TextView textView_name;
	int count =0;
	float rating = 0.0f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_store_page);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		context=this;
		textView_name = (TextView) findViewById(R.id.textView_name);
		Bundle b=getIntent().getExtras();
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}

		if(getIntent().getExtras()!=null)
			storename = getIntent().getExtras().getString("storename");
		if(storename==null)
			storename=getIntent().getExtras().get("storename").toString();
		Log.e(TAG, "onCreate: ");
		if(getIntent().getExtras()!=null)
			storeid = getIntent().getExtras().getString("storeid");
		if(storeid==null)
			storeid=getIntent().getExtras().get("storeid").toString();
		textView_name.setText(storename);
		Log.e(TAG, "onCreate: After get "+storeid+" "+storename  );
		Intent intent=new Intent(MyStorePageActivity.this,ReviewIntentService.class);
		intent.putExtra(ReviewIntentService.GET_REVIEW_FOR_CHART,storeid);
		intent.setAction(ReviewIntentService.GET_REVIEW_FOR_CHART);
		intent.putExtra("activity",TAG);
		startService(intent);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						buildChart(intent.getStringExtra(ReviewIntentService.GET_REVIEW_FOR_CHART));
					}
				}, new IntentFilter(ReviewIntentService.GET_REVIEW_FOR_CHART)
		);
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(MyFirebaseNotification.REVIEW_NOTIFICATION_ID);
	}
	public void buildChart(String response)
	{
		chart = (BarChart) findViewById(R.id.chart1);
		int i=0, ratingI=0;
		Log.e(TAG, "buildChart: "+response );
		ArrayList<BarDataSet> dataSets = null;
		ArrayList<BarEntry> valueSet1 = new ArrayList<>();
		BarEntry v1e1;
		ArrayList<ReviewRatingCountChart> reviewRatingCountCharts=new Gson().fromJson(response,new TypeToken<ArrayList<ReviewRatingCountChart>>() {}.getType());
		Log.e(TAG, "buildChart: "+reviewRatingCountCharts.size() );
		for (ReviewRatingCountChart r:reviewRatingCountCharts)
		{
			count = r.getCount();
			rating = r.getRating();
			switch (String.valueOf(rating))
			{
				case "0.0":
					ratingI = 0;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "0.5":
					ratingI = 1;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "1.0":
					ratingI = 2;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "1.5":
					ratingI = 3;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "2.0":
					ratingI = 4;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "2.5":
					ratingI = 5;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "3.0":
					ratingI = 6;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "3.5":
					ratingI = 7;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "4.0":
					ratingI = 8;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "4.5":
					ratingI = 9;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
				case "5.0":
					ratingI = 10;
					v1e1 = new BarEntry(count,ratingI);
					valueSet1.add(v1e1);
					break;
			}


		}

		BarDataSet barDataSet1 = new BarDataSet(valueSet1, "X-axis: Rating Y-axis: Review count");

		barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
		dataSets = new ArrayList<>();
		dataSets.add(barDataSet1);

		BarData data = new BarData(getXAxisValues(), barDataSet1);
		chart.setData(data);
		chart.setDescription(storename);

		chart.animateXY(2000, 2000);

		chart.invalidate();


	}
	private ArrayList<String> getXAxisValues() {
		ArrayList<String> xAxis = new ArrayList<>();
		xAxis.add("0.0");
		xAxis.add("0.5");
		xAxis.add("1.0");
		xAxis.add("1.5");
		xAxis.add("2.0");
		xAxis.add("2.5");
		xAxis.add("3.0");
		xAxis.add("3.5");
		xAxis.add("4.0");
		xAxis.add("4.5");
		xAxis.add("5.0");
		return xAxis;
	}
	public void onEditStore(View view)
	{
		//chart.saveToGallery(".feedback/Chart",50);


		Intent intent=new Intent(context,NewStoreActivity.class);

		intent.putExtra("storeid",storeid);
		intent.putExtra("mode","EDIT");
		startActivityForResult(intent,1);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.menu_my_store_page, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.action_share);

		// Fetch and store ShareActionProvider
		//mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider();

		// Return true to display menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.action_share:

				chart.saveToGallery("Chart",".feedback",null, Bitmap.CompressFormat.PNG,0);

				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");
				share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/.feedback/Chart.png"));
				startActivity(Intent.createChooser(share, "Share Image"));
				//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

				break;
		}
		return super.onOptionsItemSelected(item);
	}
	public void onNewForm(View view)
	{
		Intent intent = new Intent(this, NewFormActivity.class);
		intent.putExtra("storeid",storeid);
		startActivity(intent);
	}
	public void onEditForm(View view)
	{
		Intent intent = new Intent(this, NewFormActivity.class);
		intent.putExtra("storeid",storeid);
		startActivity(intent);
	}
}
