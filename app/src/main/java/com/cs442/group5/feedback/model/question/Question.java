package com.cs442.group5.feedback.model.question;

import java.util.ArrayList;

/**
 * Created by sauja7 on 11/1/16.
 */

public class Question {
	String ques;
	String type;
	ArrayList<String> options;

	public String getQues() {
		return ques;
	}

	public String getType() {
		return type;
	}

	public void setQues(String q) {
		this.ques=q;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	public void addOptions(String text)
	{
		options.add(text);
	}
}
