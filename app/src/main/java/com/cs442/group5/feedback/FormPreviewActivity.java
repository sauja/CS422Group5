package com.cs442.group5.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs442.group5.feedback.model.question.FeedbackForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FormPreviewActivity extends AppCompatActivity {
	private static final String TAG=FormPreviewActivity.class.getSimpleName();
Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_preview);
		context=this;
		FeedbackForm feedbackForm;

		Intent i = getIntent();
		Gson gson=new Gson();
		String jsonItems = (String) i.getExtras().get("FORMDATA");
		Log.e(TAG, "onCreate: "+jsonItems );
		feedbackForm=gson.fromJson(jsonItems, new TypeToken<FeedbackForm>() {}.getType());
		TextView textView_formName=(TextView) findViewById(R.id.textView_formName);
		textView_formName.setText(feedbackForm.getFormName());
		LinearLayout linearLayout=(LinearLayout) findViewById(R.id.ll_data);

		feedbackForm.addToLayout(linearLayout,context,jsonItems );
	}
}
