package com.cs442.group5.feedback.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.cs442.group5.feedback.R;

import java.util.ArrayList;

/**
 * Created by shivani on 11/9/2016.
 */
public class CustomSwipeAdapter extends PagerAdapter {
	private static final String TAG="CustomSwipeAdapter";
    private  Context context;
    private LayoutInflater layoutInflater;
    ImageView img;
    static ArrayList<String> IMAGES = new ArrayList<>();


    public CustomSwipeAdapter(Context context, ArrayList<String> IMAGES) {
        this.IMAGES = IMAGES;
        this.context = context;
        System.out.println("AAAAAAAAA"+IMAGES);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
		Log.e(TAG, "destroyItem: " );
	}

	@Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }


	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.swipe_layout,null);
		((ViewPager) container).addView(view);
		final ImageView img = (ImageView) view.findViewById(R.id.image_view);
		Glide.with(context).load(IMAGES.get(position)).asBitmap().into(img);
		//Picasso.with(context).load().into(img);
		return view;
	}

}
