package com.cs442.group5.feedback.model.question;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.model.controls.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sauja7 on 11/1/16.
 */

public class FeedbackForm {
	long id;
	String name;
	String structure;
	String formid;
	long storeid;
	String iddefault;
	String formName="";
	ArrayList<Question> questions;
	private static final String TAG=FeedbackForm.class.getSimpleName();
	public void addToLayout(final LinearLayout layout, final Context context,final String jsonItems)
	{
		int quesCount=1;

		for(Question q :questions)
		{
			TextView t1=new TextView(context);
			t1.setText(quesCount+++" "+q.getQues());
			layout.addView(t1);
			ArrayList<String> control;
			switch (q.getType())
			{
				case Control.DROPDOWN:
					Spinner spinner=new Spinner(context);
					String []op=q.getOptions().toArray(new String[q.getOptions().size()]);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
							android.R.layout.simple_spinner_item, op);
					spinner.setAdapter(adapter);
					layout.addView(spinner);
					break;
				case Control.RADIO:
					RadioGroup group=new RadioGroup(context);
					control=q.getOptions();
					for(String option:control)
					{
						RadioButton r1=new RadioButton(context);
						r1.setText(option);
						group.addView(r1);
					}
					layout.addView(group);
					break;
				case Control.CHECKBOX:
					control=q.getOptions();
					for(String option:control)
					{
						CheckBox r1=new CheckBox(context);
						r1.setText(option);
						layout.addView(r1);
					}
					break;
				case Control.TEXT:
					EditText e1=new EditText(context);
					layout.addView(e1);
					break;
				case Control.RATING:
					RatingBar ratingBar=new RatingBar(context);
					ratingBar.setNumStars(5);
					ratingBar.setRating(3f);
					ratingBar.setStepSize(0.5f);
					ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					layout.addView(ratingBar);
					break;
			}
		}

		/*btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createForm((long)1,context,jsonItems);
				Log.e(TAG, "onClick: "+layout.getChildCount());
				int childCount=layout.getChildCount();
				int i=0;
				int anscount=1;
				while (i!=childCount)
				{
					String type=layout.getChildAt(i).getClass().getName();
					if(layout.getChildAt(i) instanceof EditText)
					{
						EditText e=(EditText)layout.getChildAt(i);
						Log.e(TAG, "onClick: AnswerEdit "+anscount+++" "+e.getText());
					}
					if(layout.getChildAt(i) instanceof RadioGroup)
					{
						RadioGroup e=(RadioGroup)layout.getChildAt(i);
						int radioCount=e.getChildCount();
						for(int j=0;j<radioCount;j++)
						{
							RadioButton r=(RadioButton)e.getChildAt(j);
							if(r.isChecked())
								Log.e(TAG, "onClick: AnswerRadio "+anscount+++" "+r.getText());
						}
						//Log.e(TAG, "onClick: Answer "+anscount+++" "+e.getText());
					}
					if(layout.getChildAt(i) instanceof CheckBox)
					{
						while (layout.getChildAt(i) instanceof CheckBox) {
							CheckBox c=(CheckBox)layout.getChildAt(i);
							if (c.isChecked())
								Log.e(TAG, "onClick: AnswerCheck " + anscount + " " + c.getText());
							i++;
						}
						anscount++;
						continue;

						//Log.e(TAG, "onClick: Answer "+anscount+++" "+e.getText());
					}
					if(layout.getChildAt(i) instanceof Spinner)
					{
						Spinner spinner=(Spinner)layout.getChildAt(i);
						Log.e(TAG, "onClick: AnswerCheck " + anscount + " "+spinner.getSelectedItem().toString());
						anscount++;
					}
					if(layout.getChildAt(i) instanceof RatingBar)
					{
						RatingBar ratingBar=(RatingBar)layout.getChildAt(i) ;

						Log.e(TAG, "onClick: AnswerCheck " + anscount + " "+ratingBar.getRating() );
						anscount++;
					}


					i++;
				}
				for(i=0;i<childCount;i++)
				{
					Log.e(TAG, "onClick: "+i+" "+layout.getChildAt(i).getClass().getName() );
					switch (layout.getChildAt(i).getClass().getName())
					{

						case "android.widget.Checkbox":
							break;
						case "android.widget.EditText":
							break;
						case "android.widget.RadioGroup":
							break;

					}


				}

			}
		});*/

	}

	public FeedbackForm() {
		this.questions =new ArrayList<>();
	}
	public void addQuestion(Question q)
	{
		questions.add(q);
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormName() {
		return formName;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}

	public void createForm(final long storeid,final Context context, final String jsonItems)
	{
		RequestQueue queue;
		queue = Volley.newRequestQueue(context);
		final String url=context.getString(R.string.server_url)+"/form/addForm";
		StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) {

				if(response.equals("1062"))
					Toast.makeText(context, "Form already exists.", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "onResponse: "+response );
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
				parameters.put("name", formName);
				parameters.put("structure", jsonItems);
				parameters.put("storeid", String.valueOf(storeid));
				Log.e(TAG, "getParams: "+jsonItems );
				return parameters;
			}
		};
		queue.add(postRequest);
	}
}
