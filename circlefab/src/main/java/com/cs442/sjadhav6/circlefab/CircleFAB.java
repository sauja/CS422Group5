package com.cs442.sjadhav6.circlefab;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by sauja7 on 10/17/16.
 */

public class CircleFAB extends ConstraintLayout
{
	Context context;
	boolean isFABOpen=true;
	Animation fab_open,fab_close,fab_rotate_frwd,fab_rotate_rev,fab_bottom_open,fab_bottom_close,
			fab_top_open,fab_top_close,fab_middle_open,fab_middle_close;
	FloatingActionButton fab_bottom,fab_top,fab_middle,fab_main;
	public CircleFAB(Context context) {
		super(context);
		this.context=context;
		loadViews();
	}

	public CircleFAB(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.circle_fab, this);

		loadViews();
	}

	public CircleFAB(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	private void loadViews() {
		fab_main=(FloatingActionButton)findViewById(R.id.fab_main);
		fab_top=(FloatingActionButton)findViewById(R.id.fab_top);
		fab_middle=(FloatingActionButton)findViewById(R.id.fab_middle);
		fab_bottom=(FloatingActionButton)findViewById(R.id.fab_bottom);
		fab_main.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				animateFAB();
			}
		});


		fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open);
		fab_close = AnimationUtils.loadAnimation(context,R.anim.fab_close);
		fab_rotate_frwd = AnimationUtils.loadAnimation(context,R.anim.fab_rotate_frwd);
		fab_rotate_rev = AnimationUtils.loadAnimation(context,R.anim.fab_rotate_rev);
		fab_bottom_open = AnimationUtils.loadAnimation(context,R.anim.fab_bottom_open);
		fab_bottom_close = AnimationUtils.loadAnimation(context,R.anim.fab_bottom_close);
		fab_top_open= AnimationUtils.loadAnimation(context,R.anim.fab_top_open);
		fab_top_close= AnimationUtils.loadAnimation(context,R.anim.fab_top_close);
		fab_middle_open = AnimationUtils.loadAnimation(context,R.anim.fab_middle_open);
		fab_middle_close= AnimationUtils.loadAnimation(context,R.anim.fab_middle_close);
		animateFAB();
	}
	public void animateFAB(){

		if(isFABOpen){

			fab_main.startAnimation(fab_rotate_rev);
			fab_bottom.startAnimation(fab_bottom_close);
			fab_middle.startAnimation(fab_middle_close);
			fab_top.startAnimation(fab_top_close);
			fab_bottom.setClickable(false);
			fab_middle.setClickable(false);
			fab_top.setClickable(false);
			isFABOpen = false;


		} else {
			fab_main.startAnimation(fab_rotate_frwd);
			fab_bottom.startAnimation(fab_bottom_open);
			fab_middle.startAnimation(fab_middle_open);
			fab_top.startAnimation(fab_top_open);
			fab_bottom.setClickable(true);
			fab_middle.setClickable(true);
			fab_top.setClickable(true);
			isFABOpen = true;

		}
	}
}
