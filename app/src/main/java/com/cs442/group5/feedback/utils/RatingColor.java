package com.cs442.group5.feedback.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.cs442.group5.feedback.R;

/**
 * Created by sauja7 on 11/13/16.
 */

public class RatingColor {

	public static int getRatingColor(float rating, Context context)
	{
		int id=android.R.color.transparent;
		if(rating==(float)0.0)
			id= R.color.rating_0;
		else if(rating<=(float)0.5)
			id= R.color.rating_0_5;
		else if(rating<=(float)1.0)
			id= R.color.rating_1;
		else if(rating<=(float)1.5)
			id= R.color.rating_1_5;
		else if(rating<=(float)2.0)
			id= R.color.rating_2;
		else if(rating<=(float)2.5)
			id= R.color.rating_2_5;
		else if(rating<=(float)3.0)
			id= R.color.rating_3;
		else if(rating<=(float)3.5)
			id= R.color.rating_3_5;
		else if(rating<=(float)4.0)
			id= R.color.rating_4;
		else if(rating<=(float)4.5)
			id= R.color.rating_4_5;
		else if(rating<=(float)5.0)
			id= R.color.rating_5;


		return ContextCompat.getColor(context.getApplicationContext(),id);
	}
}
