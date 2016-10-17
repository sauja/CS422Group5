package com.cs442.group5.feedback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class AboutUs extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		WebView wv;
		wv = (WebView) findViewById(R.id.webView1);
		wv.loadUrl("file:///android_asset/aboutUs.html");
		Button btn_close=(Button)findViewById(R.id.btn_close);
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
