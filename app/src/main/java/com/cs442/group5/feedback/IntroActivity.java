package com.cs442.group5.feedback;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends MaterialIntroActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableLastSlideAlphaExitTransition(true);
		getBackButtonTranslationWrapper()
				.setEnterTranslation(new IViewTranslation() {
					@Override
					public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
						view.setAlpha(percentage);
					}
				});
		addSlide(new SlideFragmentBuilder()
				.backgroundColor(R.color.colorPrimaryDark)
				.buttonsColor(R.color.colorAccent)
				.title("FeedBack")
				.description("Please grant permissions on the next slide")
				.build());
		addSlide(new SlideFragmentBuilder()
						.backgroundColor(R.color.colorPrimaryDark)
						.buttonsColor(R.color.colorAccent)
						.image(agency.tango.materialintroscreen.R.drawable.ic_next)
						.neededPermissions(new String[]{android.Manifest.permission.CAMERA})
						.title("Camera")
						.description("To add images of stores")
						.build(),
				new MessageButtonBehaviour(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMessage("We provide solutions to make you love your work");
					}
				}, "Work with love"));
		addSlide(new SlideFragmentBuilder()
						.backgroundColor(R.color.colorPrimaryDark)
						.buttonsColor(R.color.colorAccent)
						.image(agency.tango.materialintroscreen.R.drawable.ic_next)
						.neededPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
						.title("Location")
						.description("Needed for showing Map")
						.build(),
				new MessageButtonBehaviour(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMessage("We provide solutions to make you love your work");
					}
				}, "Work with love"));
		addSlide(new SlideFragmentBuilder()
						.backgroundColor(R.color.colorPrimaryDark)
						.buttonsColor(R.color.colorAccent)
						.image(agency.tango.materialintroscreen.R.drawable.ic_next)
						.neededPermissions(new String[]{Manifest.permission.CALL_PHONE})
						.title("Call")
						.description("Needed for calling stores")
						.build(),
				new MessageButtonBehaviour(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMessage("We provide solutions to make you love your work");
					}
				}, "Work with love"));

	}
	@Override
	public void onFinish() {
		super.onFinish();
		Intent intent=new Intent(IntroActivity.this,LoginActivity.class);
		startActivity(intent);
	}
}