package com.cs442.group5.feedback;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cs442.group5.feedback.model.controls.Control;
import com.cs442.group5.feedback.model.question.FeedbackForm;
import com.cs442.group5.feedback.model.question.Question;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class NewFormActivity extends AppCompatActivity
{
	private static final String TAG="NewFormActivity";
	Context context=this;
	TextView textView_formName;
	Spinner spinner_questionType;
	ListView listView_options;
	Button btn_addOption;
	EditText editText_option;
	ArrayList<String> option;
	OptionListAdapter optionListAdapter;
	FeedbackForm feedbackForm=new FeedbackForm();
	EditText editText_question;
	SharedPreferences sf;
	SharedPreferences.Editor editor;
	Gson gson=new Gson();
	String storeid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_form);
		textView_formName=(TextView)findViewById(R.id.textView_formName);
		editText_question=(EditText) findViewById(R.id.editText_question);
		spinner_questionType = (Spinner) findViewById(R.id.spinner_questionType);
		sf=context.getSharedPreferences("NEWFORM",MODE_PRIVATE);
		editor = sf.edit();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// add back arrow to toolbar
		if (getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		btn_addOption=(Button)findViewById(R.id.btn_addOption);
		editText_option=(EditText)findViewById(R.id.editText_option);
		listView_options=(ListView) findViewById(R.id.listView_options);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		option=new ArrayList<>();

		optionListAdapter=new OptionListAdapter(this,option);
		listView_options.setAdapter(optionListAdapter);
		optionListAdapter.notifyDataSetChanged();

		spinner_questionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(spinner_questionType.getSelectedItem().toString().equals("Radio Button")
						||spinner_questionType.getSelectedItem().toString().equals("Drop Down")
						||spinner_questionType.getSelectedItem().toString().equals("CheckBox"))
				{
					btn_addOption.setVisibility(View.VISIBLE);
					editText_option.setVisibility(View.VISIBLE);
				}
				else
				{
					btn_addOption.setVisibility(View.GONE);
					editText_option.setVisibility(View.GONE);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}
		});

		listView_options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
				final EditText editText = new EditText(context);

				TextView tv=(TextView)view.findViewById(R.id.textView_optionText);
				editText.setText(tv.getText());
				new AlertDialog.Builder(context)
						.setTitle("Enter Form Name")
						.setView(editText)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								option.set(i,editText.getText().toString());
								optionListAdapter.notifyDataSetChanged();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						})
						.show();
			}
		});


	}
	public void onClickFormName(View v)
	{
		changeFormName();

	}
	public void onClickPreview(View v)
	{
		Log.e(TAG, "onClickPreview: " );

		Intent i = new Intent(this,FormPreviewActivity.class);

		i.putExtra("structure",gson.toJson(feedbackForm) );
		i.putExtra("storeid",storeid);
		Log.e("JASAON", "onClickPreview: "+gson.toJson(feedbackForm)  );
		startActivity(i);
	}
	public void onClickAddQuestion(View v)
	{
		Log.e(TAG, "onClickAddQuestion: " );
		if(editText_question.getText().toString().length()>0){
			Question question=new Question();
			question.setQues(editText_question.getText().toString());
			switch (spinner_questionType.getSelectedItem().toString())
			{
				case "Text":
					question.setType(Control.TEXT);
					feedbackForm.addQuestion(question);
					break;
				case "CheckBox":
					question.setType(Control.CHECKBOX);
					question.setOptions(option);
					feedbackForm.addQuestion(question);
					break;
				case "Radio Button":
					question.setType(Control.RADIO);
					question.setOptions(option);
					feedbackForm.addQuestion(question);
					break;
				case "Drop Down":
					question.setType(Control.DROPDOWN);
					question.setOptions(option);
					feedbackForm.addQuestion(question);
					break;
				case "Rating":
					question.setType(Control.RATING);
					feedbackForm.addQuestion(question);
					break;

			}
			option=new ArrayList<String>();
			optionListAdapter=new OptionListAdapter(this,option);
			listView_options.setAdapter(optionListAdapter);
			optionListAdapter.notifyDataSetChanged();
			editText_question.setText("");

		}
		else
			Toast.makeText(context, "Please enter the question", Toast.LENGTH_SHORT).show();
	}
	public void onClickAddOption(View v)
	{

		if(editText_option.getText().length()>0)
		{
			option.add(editText_option.getText().toString());
			editText_option.setText("");
			optionListAdapter.notifyDataSetChanged();
		}
	}
	public void changeFormName()
	{
		final EditText editText = new EditText(this);


		editText.setText(textView_formName.getText());

		new AlertDialog.Builder(this)
				.setTitle("Enter Form Name")
				.setView(editText)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(editText.getText().toString().length()==0) {
							Toast.makeText(context, "Form name cannot be empty", Toast.LENGTH_SHORT).show();
							changeFormName();
						}
						else
						{textView_formName.setText(editText.getText().toString());
							feedbackForm.setFormName(editText.getText().toString());}
					}
				})
				.show();
	}
	private class OptionListAdapter extends ArrayAdapter<String>
	{
		List<String> objects;
		public OptionListAdapter(Context context, List<String> objects) {
			super(context,0, objects);
			this.objects=objects;

		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			String user = getItem(position);
			// Check if an existing view is being reused, otherwise inflate the view
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.option_item, parent, false);
			}
			// Lookup view for data population
			TextView optionValue = (TextView) convertView.findViewById(R.id.textView_optionText);
			ImageView imgbtn_delete = (ImageView) convertView.findViewById(R.id.delete);
			// Populate the data into the template view using the data object
			optionValue.setText(user);
			imgbtn_delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					objects.remove(position);
					notifyDataSetChanged();
				}
			});
			// Return the completed view to render on screen
			return convertView;
		}


	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "onPause: " );
		editor.putString("FORM",gson.toJson(feedbackForm));
		editor.putString("currentquestion",editText_question.getText().toString());
		editor.putString("currentoptions",gson.toJson(option));
		editor.putString("currentformname",textView_formName.getText().toString());
		editor.commit();
	}

	@Override
	protected void onResume() {
		Log.e(TAG, "onResume: " );
		super.onResume();
		storeid=getIntent().getExtras().get("storeid").toString();
		Log.e(TAG, "onResume: "+storeid );
		if(sf.contains("FORM"))
		{
			FeedbackForm f=gson.fromJson(sf.getString("FORM",null),new TypeToken<FeedbackForm>() {}.getType());
			Log.e(TAG, "onResume: gson data "+f );
			if(f!=null)
			{
				feedbackForm=f;

			}
			else
				feedbackForm=new FeedbackForm();

		}
		if(sf.contains("currentquestion"))
		{
			Log.e(TAG, "onResume: currentquestion"+ sf.getString("currentquestion",""));
			editText_question.setText(sf.getString("currentquestion",""));
		}
		if(sf.contains("currentformname"))
		{
			Log.e(TAG, "onResume: currentformname"+ sf.getString("currentformname",""));
			textView_formName.setText(sf.getString("currentformname",""));
		}
		if(sf.contains("currentoptions"))
		{
			Log.e(TAG, "onResume: currentoptions"+ sf.getString("currentoptions",""));
			option=(gson.fromJson(sf.getString("currentoptions",null),new TypeToken<ArrayList<String>>() {}.getType()));
			if(option==null)
			{
				Log.e(TAG, "onResume: Options are null" );
				option=new ArrayList<String>();
			}
		}
		if(feedbackForm.getFormName().length()==0)
		{
			Log.e(TAG, "onResume: formName "+feedbackForm.getFormName() );
			changeFormName();}
		optionListAdapter=new OptionListAdapter(this,option);
		listView_options.setAdapter(optionListAdapter);
		optionListAdapter.notifyDataSetChanged();
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