package com.cs442.group5.feedback.utils;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cs442.group5.feedback.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shivani on 11/9/2016.
 */
public class CustomSwipeAdapter extends PagerAdapter {
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
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }


    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.swipe_layout,null);
        ((ViewPager) collection).addView(view);
        final ImageView img = (ImageView) view.findViewById(R.id.image_view);
        Picasso.with(context)
                .load(IMAGES.get(position))
                .into(img);
        return view;
    }
}
