package com.cs442.group5.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs442.group5.feedback.model.question.FeedbackForm;
import com.cs442.group5.feedback.service.FormIntentService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FormPreviewActivity extends AppCompatActivity {
	private static final String TAG="FormPreviewActivity";
	Context context;
	String storeid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_preview);
		context=this;
		final FeedbackForm feedbackForm;

		Intent i = getIntent();
		Gson gson=new Gson();
		storeid=getIntent().getStringExtra("storeid");
		Log.e(TAG, "onCreate: "+storeid );
		final String jsonItems = i.getStringExtra("structure");
		Log.e(TAG, "onCreate: "+jsonItems );
		feedbackForm=gson.fromJson(jsonItems, new TypeToken<FeedbackForm>() {}.getType());
		TextView textView_formName=(TextView) findViewById(R.id.textView_formName);
		textView_formName.setText(feedbackForm.getFormName());
		LinearLayout linearLayout=(LinearLayout) findViewById(R.id.ll_data);

		feedbackForm.addToLayout(linearLayout,context,jsonItems );
		final Button btn_save=new Button(context);
		btn_save.setText("Save Form");
		linearLayout.addView(btn_save);
		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(FormPreviewActivity.this,FormIntentService.class);
				intent.putExtra(FormIntentService.CREATE_FORM,storeid);
				intent.putExtra("storeid",storeid);
				intent.putExtra("formname",feedbackForm.getFormName());
				intent.putExtra("structure",jsonItems);
				intent.setAction(FormIntentService.CREATE_FORM);
				intent.putExtra("activity",TAG);
				startService(intent);
				LocalBroadcastManager.getInstance(context).registerReceiver(
						new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								String response=intent.getStringExtra(FormIntentService.CREATE_FORM);
								Log.e(TAG, "onReceive: "+response );
								if(response!=null  && response.contains("true"))
								{
									Toast.makeText(context, "Form Added to store", Toast.LENGTH_SHORT).show();
									Intent intent1=new Intent(FormPreviewActivity.this,DashBoardActivity.class);
									startActivity(intent1);
									SharedPreferences.Editor pref = context.getSharedPreferences("NEWFORM", MODE_PRIVATE).edit();
									pref.clear();
									pref.commit();
									finish();
								}
								else
								{
									Toast.makeText(context, "Error: "+response, Toast.LENGTH_SHORT).show();
								}

							}
						}, new IntentFilter(FormIntentService.CREATE_FORM)
				);
			}
		});
	}
}