package com.cs442.group5.feedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class TermsNConditions extends AppCompatActivity
{
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_nconditions);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		if (getIntent().getExtras().containsKey("webviewName"))
		{
			String value = getIntent().getExtras().getString("webviewName");
			WebView wv;
			wv = (WebView) findViewById(R.id.webView1);
			switch (value)
			{
				case "TermsNConditions":
					toolbar.setTitle("Terms and Conditions");
					value = "tnc.html";
					break;
				case "AboutUs":
					toolbar.setTitle("About Us");
					value = "aboutUs.html";
					break;
				case "license":
					toolbar.setTitle("3rd Party License");
					value = "license.html";
					break;
			}

			wv.loadUrl("file:///android_asset/" + value);

			Button btn_close = (Button) findViewById(R.id.btn_close);
			btn_close.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					finish();
				}
			});
		}
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
