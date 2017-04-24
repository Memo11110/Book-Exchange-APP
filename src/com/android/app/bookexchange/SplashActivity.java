package com.android.app.bookexchange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity{
	
	private final int DISPLAY_LENGTH = 4000;
	private String str_email="";
//	private ImageView img_logo;
	private SharedPreferences preferences;
//	Animation anim, anim1, anim2=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		
//		img_logo = (ImageView)findViewById(R.id.img_logo);
//		anim1=AnimationUtils.loadAnimation(this, R.anim.mytrans);
//		img_logo.startAnimation(anim1);
		
		preferences =this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		str_email = preferences.getString("email", "");
//		anim=AnimationUtils.loadAnimation(this, R.anim.mycombo);
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() 
			{
				if(str_email==null || str_email.length()==0)
				{	
					Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
					mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
				}
				else
				{
					Intent mainIntent = new Intent(SplashActivity.this,HomeActivity.class);
					mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
				}
				
			}
		}, DISPLAY_LENGTH);
	}
	
	@Override
	public void onBackPressed() {
	}
}
