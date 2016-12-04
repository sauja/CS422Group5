package com.cs442.group5.feedback;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.PermissionsRefusedListener;
import com.github.jksiezni.permissive.Permissive;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.CameraController;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class QRReader extends AppCompatActivity {
	private ScannerLiveView camera;
	private CameraController controller;
	private boolean flashStatus;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrreader);
		context=this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		camera = (ScannerLiveView) findViewById(R.id.camview);
		camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener()
		{
			@Override
			public void onScannerStarted(ScannerLiveView scanner)
			{
				//Toast.makeText(QRReader.this,"Scanner Started",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onScannerStopped(ScannerLiveView scanner)
			{
				//Toast.makeText(QRReader.this,"Scanner Stopped",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onScannerError(Throwable err)
			{
				//Toast.makeText(QRReader.this,"Scanner Error: " + err.getMessage(),Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCodeScanned(String data)
			{
				if(data.contains(getString(R.string.host)))
				{Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( data ) );
				startActivity( browse );}
				else
				Toast.makeText(QRReader.this, "Invalid QR", Toast.LENGTH_SHORT).show();
			}
		});
		new Permissive.Request(Manifest.permission.CAMERA)
				.whenPermissionsGranted(new PermissionsGrantedListener() {
					@Override
					public void onPermissionsGranted(String[] permissions) throws SecurityException {
						findViewById(R.id.btnFlash).setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								toggleFlash();
							}
						});
					}
				})
				.whenPermissionsRefused(new PermissionsRefusedListener() {
					@Override
					public void onPermissionsRefused(String[] permissions) {
						findViewById(R.id.btnFlash).setVisibility(View.GONE);
					}
				})
				.execute(this);

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		ZXDecoder decoder = new ZXDecoder();
		decoder.setScanAreaPercent(0.5);
		camera.setDecoder(decoder);
		camera.startScanner();
	}

	@Override
	protected void onPause()
	{
		camera.stopScanner();
		super.onPause();
	}

	public void toggleFlash()
	{
		flashStatus = !flashStatus;
		camera.getCamera().getController().switchFlashlight(flashStatus);
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle arrow click here
		if (item.getItemId() == android.R.id.home) {
			finish(); // close this activity and return to preview activity (if there is any)
		}
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}


}