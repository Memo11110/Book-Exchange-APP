package com.android.app.bookexchange;

import java.net.SocketTimeoutException;
import java.util.Dictionary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class EditProfileActivity extends Activity {

	private Button btn_back,btn_update,btn_reset;
	private EditText et_userName,et_phone,et_aboutme,
					 et_password,et_confirmPass;
	private TextView tv_email;
	private String str_userName,str_phone,str_aboutMe,str_pass,str_confPass,
					str_email,str_userId;
	private Dictionary<String, String> msgResponse;
	private SharedPreferences preference;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		setDataUI();
		setupUI(findViewById(R.id.rl_parent));
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_email = preference.getString("email", "");
		str_userId = preference.getString("UserId", "");
		str_pass = preference.getString("password", "");
		
		System.out.println("str_email:- "+str_email);
		System.out.println("str_userId:- "+str_userId);
		System.out.println("str_pass:- "+str_pass);
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(EditProfileActivity.this, MyProfileActivity.class);
				EditProfileActivity.this.startActivity(i);
				EditProfileActivity.this.finish();
			}
		});
		btn_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editProfile();
			}
		});
		btn_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				setNewData();
			}
		});

	}
	
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_update = (Button)findViewById(R.id.btn_update);
		btn_reset = (Button)findViewById(R.id.btn_reset);
		et_userName = (EditText)findViewById(R.id.et_userName);
		et_phone = (EditText)findViewById(R.id.et_phone);
		et_aboutme = (EditText)findViewById(R.id.et_aboutme);
		et_password = (EditText)findViewById(R.id.et_password);
		et_confirmPass = (EditText)findViewById(R.id.et_confirmPass);
		tv_email = (TextView)findViewById(R.id.tv_email);
	}
	
	private void setNewData()
	{
		str_userName = getIntent().getStringExtra("name");
		str_phone = getIntent().getStringExtra("phone");
		str_aboutMe = getIntent().getStringExtra("about");
		
		et_userName.setText(str_userName);
		tv_email.setText(str_email);
		et_phone.setText(str_phone);
		et_aboutme.setText(str_aboutMe);
		et_password.setText("");
		et_confirmPass.setText("");
	}
	private void editProfile()
	{
		str_userName  = et_userName.getText().toString().trim();
		str_phone  = et_phone.getText().toString().trim();
		str_aboutMe  = et_aboutme.getText().toString().trim();
		str_pass  = et_password.getText().toString().trim();
		str_confPass  = et_confirmPass.getText().toString().trim();
		if(str_userName.equalsIgnoreCase("")||str_userName.length()==0)
		{
			et_userName.requestFocus();
			et_userName.setError(getResources().getString(R.string.message_Name));
		}
		else if(str_phone.equalsIgnoreCase("")||str_phone.length()==0)
		{
			et_phone.requestFocus();
			et_phone.setError(getResources().getString(R.string.message_phone));
		}
		else if(str_phone.length()>0 && str_phone.length()<8 )
		{
			et_phone.requestFocus();
			et_phone.setError(getResources().getString(R.string.message_validPhone));
		}
		else if(str_aboutMe.equalsIgnoreCase("")||str_aboutMe.length()==0 )
		{
			et_aboutme.requestFocus();
			et_aboutme.setError(getResources().getString(R.string.message_about));
		}
		else if(str_pass.length()>0 && str_pass.length()<6)
		{
			et_password.requestFocus();
			et_password.setError(getResources().getString(R.string.message_passwordLength));
		}
		else if(!str_pass.equalsIgnoreCase(str_confPass))
		{
			et_confirmPass.requestFocus();
			et_confirmPass.setError(getResources().getString(R.string.message_bothPassword));
		}
		
		else
		{
			if(CheckInternetConection.isInternetConnection(getApplicationContext()))
			{
				new EditProfileLoad().execute();
			}
			else
			{
				showToast(getResources().getString(R.string.app_noConnection));
			}
		}
	}
	
	private class EditProfileLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog logDialog=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			logDialog=new ProgressDialog(EditProfileActivity.this);
			logDialog.setTitle(getResources().getString(R.string.app_name));
			logDialog.setMessage(getResources().getString(R.string.app_pleaseWait));
			logDialog.setIndeterminate(true);
			logDialog.setCancelable(false);
			logDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try 
			{
				String editProfile_URL=getResources().getString(R.string.URL_app)+getResources().getString(R.string.URL_edit);
				QWebService.initialize(editProfile_URL);
				msgResponse=GeneralWebService.editUserProfile(editProfile_URL, getBundleData());
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
			}
			catch (NotFoundException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			logDialog.dismiss();
			try
			{
				getProfileJSON();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private  Bundle getBundleData()
	{
		Bundle bundle =new Bundle();
		bundle.putString("user_id", str_userId);
		bundle.putString("name", str_userName);
		bundle.putString("email", str_email);
		bundle.putString("phone", str_phone);
		bundle.putString("about", str_aboutMe);
		bundle.putString("password", str_pass);
		return bundle;
	}
	
	private void getProfileJSON(){
		
		try 
		{
			if(msgResponse!=null && !msgResponse.isEmpty())
			{
				if(msgResponse.get("message").equalsIgnoreCase("success"))
				{
					showToast(msgResponse.get("Description"));
					
					Intent i = new Intent(EditProfileActivity.this,MyProfileActivity.class);
					EditProfileActivity.this.startActivity(i);
					EditProfileActivity.this.finish();
				}
				else
				{
					showToast(msgResponse.get("Description"));
				}
			}
			else
			{
				showToast(getResources().getString(R.string.app_error));
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class DataSetLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog logDialog=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			logDialog=new ProgressDialog(EditProfileActivity.this);
			logDialog.setTitle(getResources().getString(R.string.app_name));
			logDialog.setMessage(getResources().getString(R.string.app_pleaseWait));
			logDialog.setIndeterminate(true);
			logDialog.setCancelable(false);
			logDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try 
			{
				str_userName = getIntent().getStringExtra("name");
				str_phone = getIntent().getStringExtra("phone");
				str_aboutMe = getIntent().getStringExtra("about");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			logDialog.dismiss();
			try
			{
				 setNewData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		try 
		{
			new DataSetLoad().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
private void setupUI(View view) {
		
		try 
		{
			 if(!(view instanceof EditText)) {
			        view.setOnTouchListener(new OnTouchListener() {
			            public boolean onTouch(View v, MotionEvent event) {
			                hideSoftKeyboard(EditProfileActivity.this);
			                return false;
			            }
			        });
			    }
			    if (view instanceof ViewGroup) {
			        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
			            View innerView = ((ViewGroup) view).getChildAt(i);
			            setupUI(innerView);
			        }
			    }
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	   
	}
	private void hideSoftKeyboard(Activity activity) {
		try 
		{
			InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	    
	}
	
	private void showToast(String str)
	{
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent(EditProfileActivity.this, MyProfileActivity.class);
		EditProfileActivity.this.startActivity(i);
		EditProfileActivity.this.finish();
	}

}
