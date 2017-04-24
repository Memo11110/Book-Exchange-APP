package com.android.app.bookexchange;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.ak.app.staticmethod.DialogClasses;

public class ProfileActivity extends Activity {

	private Button btn_back,btn_myBook,btn_purchaseBook,btn_addBook,
				   btn_editProfile,btn_logout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		setDataUI();
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
				ProfileActivity.this.startActivity(i);
				ProfileActivity.this.finish();
			}
		});
		btn_myBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(ProfileActivity.this, MyBooksActivity.class);
				ProfileActivity.this.startActivity(i);
				ProfileActivity.this.finish();
			}
		});
		btn_purchaseBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(ProfileActivity.this, MyPurchaseBooksActivity.class);
				ProfileActivity.this.startActivity(i);
				ProfileActivity.this.finish();
			}
		});
		btn_addBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(ProfileActivity.this, AddBookActivity.class);
				ProfileActivity.this.startActivity(i);
				ProfileActivity.this.finish();
			}
		});
		btn_editProfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ProfileActivity.this, MyProfileActivity.class);
				ProfileActivity.this.startActivity(i);
				ProfileActivity.this.finish();
				
			}
		});
		btn_logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DialogClasses.showDialog2(getResources().getString(R.string.app_logoutMessage),
						getResources().getString(R.string.app_name),
						getResources().getString(R.string.btn_yes) , 
						getResources().getString(R.string.btn_no), 
						ProfileActivity.this);
			}
		});
	}
	
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_myBook = (Button)findViewById(R.id.btn_myBook);
		btn_purchaseBook = (Button)findViewById(R.id.btn_purchaseBook);
		btn_addBook = (Button)findViewById(R.id.btn_addBook);
		btn_editProfile = (Button)findViewById(R.id.btn_editProfile);
		btn_logout = (Button)findViewById(R.id.btn_logout);
	}
	
		
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
		ProfileActivity.this.startActivity(i);
		ProfileActivity.this.finish();
	}

}
