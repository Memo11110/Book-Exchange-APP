package com.android.app.bookexchange;


import java.net.SocketTimeoutException;
import java.util.Dictionary;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.staticmethod.DialogClasses;
import com.ak.app.staticmethod.ValidationMethod;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class ForgotPassActivity extends Activity{
	
	private Button btn_submit,btn_login;
	private EditText et_email;
	private String str_email;
	private Dictionary<String, String> msgResponse;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_pass);
		
		setDataUI();
		setupUI(findViewById(R.id.rl_parent));
		
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(ForgotPassActivity.this,LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ForgotPassActivity.this.startActivity(i);
				ForgotPassActivity.this.finish();
			}
		});
		
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				str_email=et_email.getText().toString().trim();
				if(str_email.length()==0)
				{
					et_email.requestFocus();
					et_email.setError(getResources().getString(R.string.message_email));
				}
				else if(!ValidationMethod.emailValidation(str_email))
				{
					et_email.requestFocus();
					et_email.setError(getResources().getString(R.string.message_validEmail));
				}
				else
				{
					if(CheckInternetConection.isInternetConnection(getApplicationContext()))
					{
						new ForgotLoad().execute();
						
					}
					else
					{
						DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
								getResources().getString(R.string.app_name), 
								getResources().getString(R.string.btn_ok), 
								ForgotPassActivity.this);
					}
				}
			}
		});
	}

	private void setDataUI() {
		btn_login=(Button) findViewById(R.id.btn_login);
		btn_submit=(Button) findViewById(R.id.btn_submit);
		et_email=(EditText) findViewById(R.id.et_email);
		
	}
	
	public class ForgotLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog forgotDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			forgotDialog=new ProgressDialog(ForgotPassActivity.this);
			forgotDialog.setTitle(getResources().getString(R.string.app_name));
			forgotDialog.setMessage(getResources().getString(R.string.app_pleaseWait));
			forgotDialog.setIndeterminate(true);
			forgotDialog.setCancelable(false);
			forgotDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try 
			{
				String forgot_URL=getResources().getString(R.string.URL_app)+getResources().getString(R.string.URL_forgetPassword);
				QWebService.initialize(forgot_URL);
				msgResponse=GeneralWebService.forgotUserPassword(getApplicationContext(), str_email);
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
			}
			catch (NotFoundException e) {
				e.printStackTrace();
			}
			catch (TimeoutException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			forgotDialog.dismiss();
			
			if(msgResponse!=null && !msgResponse.isEmpty())
			{
				if(msgResponse.get("message").equalsIgnoreCase("success"))
				{
					showToast(msgResponse.get("Description"));
					Intent mainIntent = new Intent(ForgotPassActivity.this,LoginActivity.class);
					mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ForgotPassActivity.this.startActivity(mainIntent);
					ForgotPassActivity.this.finish();
					
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
		}
	}
	
	private void setupUI(View view) {
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(ForgotPassActivity.this);
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
	
	private void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	private void showToast(String msg)	{
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onBackPressed() {
		Intent mainIntent = new Intent(ForgotPassActivity.this,LoginActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ForgotPassActivity.this.startActivity(mainIntent);
		ForgotPassActivity.this.finish();
	}
}
