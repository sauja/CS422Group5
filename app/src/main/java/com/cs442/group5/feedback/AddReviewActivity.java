package com.cs442.group5.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs442.group5.feedback.model.Review;
import com.cs442.group5.feedback.model.question.FeedbackForm;
import com.cs442.group5.feedback.service.ReviewIntentService;
import com.cs442.group5.feedback.utils.Libs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AddReviewActivity extends AppCompatActivity {
	private static final String TAG="AddReviewActivity";
	Context context;
	String storeid;
	FeedbackForm feedbackForm;
	LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_review);
context=this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Add Review");
		setSupportActionBar(toolbar);
		// add back arrow to toolbar
	/*	if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
*/
		Intent i = getIntent();
		Gson gson=new Gson();
		storeid=getIntent().getStringExtra("storeid");
		Log.e(TAG, "onCreate: "+storeid );
		final String jsonItems = (String) i.getExtras().get("structure");
		Log.e(TAG, "onCreate: "+jsonItems );
		feedbackForm=gson.fromJson(jsonItems, new TypeToken<FeedbackForm>() {}.getType());
		TextView textView_formName=(TextView) findViewById(R.id.textView_formName);
		textView_formName.setText(feedbackForm.getFormName());
		layout=(LinearLayout) findViewById(R.id.ll_data);

		feedbackForm.addToLayout(layout,context,jsonItems );

	}
	public void onSave(View view)
	{
		Log.e(TAG, "onClick: "+layout.getChildCount());
		int childCount=layout.getChildCount();
		int i=0;
		int anscount=1;
		String reviewString="";
		while (i!=childCount)
		{
			String type=layout.getChildAt(i).getClass().getName();

			if(layout.getChildAt(i) instanceof EditText)
			{
				reviewString+="\n"+((TextView)layout.getChildAt(i-1)).getText();
				EditText e=(EditText)layout.getChildAt(i);
				Log.e(TAG, "onClick: AnswerEdit "+anscount+++" "+e.getText());
				reviewString+="\n"+e.getText();
			}
			if(layout.getChildAt(i) instanceof RadioGroup)
			{
				reviewString+="\n"+((TextView)layout.getChildAt(i-1)).getText();
				RadioGroup e=(RadioGroup)layout.getChildAt(i);
				int radioCount=e.getChildCount();
				for(int j=0;j<radioCount;j++)
				{
					RadioButton r=(RadioButton)e.getChildAt(j);
					if(r.isChecked()) {
						Log.e(TAG, "onClick: AnswerRadio " + anscount++ + " " + r.getText());
						reviewString+="\n"+r.getText();
					}
				}
				//Log.e(TAG, "onClick: Answer "+anscount+++" "+e.getText());
			}
			if(layout.getChildAt(i) instanceof CheckBox)
			{
				reviewString+="\n"+((TextView)layout.getChildAt(i-1)).getText();
				while (layout.getChildAt(i) instanceof CheckBox) {
					CheckBox c=(CheckBox)layout.getChildAt(i);
					if (c.isChecked())
					{	Log.e(TAG, "onClick: AnswerCheck " + anscount + " " + c.getText());
						reviewString+="\n"+c.getText();
					}
					i++;
				}
				anscount++;
				continue;

				//Log.e(TAG, "onClick: Answer "+anscount+++" "+e.getText());
			}
			if(layout.getChildAt(i) instanceof Spinner)
			{
				reviewString+="\n"+((TextView)layout.getChildAt(i-1)).getText();
				Spinner spinner=(Spinner)layout.getChildAt(i);
				reviewString+="\n"+spinner.getSelectedItem().toString();
				Log.e(TAG, "onClick: AnswerCheck " + anscount + " "+spinner.getSelectedItem().toString());
				anscount++;
			}
			if(layout.getChildAt(i) instanceof RatingBar)
			{
				reviewString+="\n"+((TextView)layout.getChildAt(i-1)).getText();
				RatingBar ratingBar=(RatingBar)layout.getChildAt(i) ;
				reviewString+="\nRating: "+ratingBar.getRating();
				Log.e(TAG, "onClick: AnswerCheck " + anscount + " "+ratingBar.getRating() );
				anscount++;
			}
			i++;
		}
		Review r=new Review();
		r.setUid(Libs.getUser().getUid());
		r.setComment(reviewString);

		Log.e(TAG, "onSave: "+reviewString );
		Intent intent=new Intent(AddReviewActivity.this,ReviewIntentService.class);
		intent.putExtra(ReviewIntentService.ADD_REVIEW,new Gson().toJson(r));
		intent.putExtra("activity",TAG);
		intent.setAction(ReviewIntentService.ADD_REVIEW);
		startService(intent);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {

					}
				}, new IntentFilter(ReviewIntentService.ADD_REVIEW)
		);
	}

}
