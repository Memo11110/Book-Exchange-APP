package com.android.app.bookexchange;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.app.servicedata.InfoProfileData;
import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.staticmethod.DialogClasses;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class MyProfileActivity extends Activity {

	private Button btn_back,btn_edit;
	private TextView tv_name,tv_ttlUpload,tv_ttlePurchase,tv_phoneNumber,tv_email,tv_about;
	private String str_userId,str_userName,str_email,str_phone,str_aboutMe,
					str_ttlUploadBook,str_ttlPurchBook;
	private SharedPreferences preference;
	private ProgressDialog viewDialog=null;
	private ArrayList<InfoProfileData> arrProfileData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);
		
		setDataUI();
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_userId = preference.getString("UserId", "");
		str_email = preference.getString("email", "");
		
		System.out.println("Email:- "+str_email);
		System.out.println("User Id:- "+str_userId);
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MyProfileActivity.this, ProfileActivity.class);
				MyProfileActivity.this.startActivity(i);
				MyProfileActivity.this.finish();
			}
		});
		btn_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MyProfileActivity.this, EditProfileActivity.class);
				i.putExtra("name", str_userName);
				i.putExtra("phone", str_phone);
				i.putExtra("about", str_aboutMe);
				MyProfileActivity.this.startActivity(i);
				MyProfileActivity.this.finish();
			}
		});
		if(CheckInternetConection.isInternetConnection(getApplicationContext()))
		{
			new ViewProfileLoad().execute();
		}
		else
		{
			DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
					getResources().getString(R.string.app_name), 
					getResources().getString(R.string.btn_ok), 
					MyProfileActivity.this);
		}
		
	}
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_edit = (Button)findViewById(R.id.btn_edit);
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_ttlePurchase = (TextView)findViewById(R.id.tv_ttlePurchase);
		tv_ttlUpload = (TextView)findViewById(R.id.tv_ttlUpload);
		tv_phoneNumber = (TextView)findViewById(R.id.tv_phoneNumber);
		tv_email = (TextView)findViewById(R.id.tv_email);
		tv_about = (TextView)findViewById(R.id.tv_about);
	}
	
	private void setNewData()
	{
		System.out.println("str_aboutMe:- "+str_aboutMe);
		tv_name.setText(str_userName);
		tv_email.setText(str_email);
		tv_about.setText(str_aboutMe);
		tv_ttlePurchase.setText(str_ttlPurchBook);
		tv_ttlUpload.setText(str_ttlUploadBook);
		tv_phoneNumber.setText(str_phone);
	}
	public class ViewProfileLoad extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			viewDialog=new ProgressDialog(MyProfileActivity.this);
			viewDialog.setTitle(getResources().getString(R.string.app_name));
			viewDialog.setMessage(getResources().getString(R.string.app_pleaseWait));
			viewDialog.setIndeterminate(true);
			viewDialog.setCancelable(false);
			viewDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try 
			{
				String viewProfile_URL=getResources().getString(R.string.URL_app)+getResources().getString(R.string.URL_profile);
				QWebService.initialize(viewProfile_URL);
				arrProfileData=GeneralWebService.viewUserProfile(viewProfile_URL, getBundleData());
				
			}
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(viewDialog!=null)
				viewDialog.dismiss();
			try 
			{
				if(arrProfileData!=null && arrProfileData.size()>0)
				{
					if(arrProfileData.get(0).json_message.equalsIgnoreCase("success"))
					{
						str_userName = arrProfileData.get(0).user_name;
						str_email = arrProfileData.get(0).user_email;
						str_phone = arrProfileData.get(0).user_phone;
						str_aboutMe = arrProfileData.get(0).user_about_me;
						str_ttlPurchBook = arrProfileData.get(0).user_total_purchased_books;
						str_ttlUploadBook = arrProfileData.get(0).user_total_uploaded_books;
						setNewData();
						
					}
					else
					{
						showToast(arrProfileData.get(0).json_description);
					}
				}
				else
				{
					showToast(getResources().getString(R.string.app_error));
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	private Bundle getBundleData() {
		
		Bundle bundle = new Bundle();
		bundle.putString("user_id", str_userId);
		return bundle;
	}
	private void showToast(String str)
	{
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent(MyProfileActivity.this, ProfileActivity.class);
		MyProfileActivity.this.startActivity(i);
		MyProfileActivity.this.finish();
	}

}
