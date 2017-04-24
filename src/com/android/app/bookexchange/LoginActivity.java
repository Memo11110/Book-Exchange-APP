package com.android.app.bookexchange;

import java.net.SocketTimeoutException;
import java.util.Dictionary;
import java.util.concurrent.TimeoutException;

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
import android.widget.Toast;

import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.staticmethod.DialogClasses;
import com.ak.app.staticmethod.ValidationMethod;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class LoginActivity extends Activity {

	private Button btn_login,btn_forgotPass,btn_register;
	private EditText et_email,et_pass;
	private String str_email,str_pass,str_userId="";
	private SharedPreferences preference;
	private Dictionary<String, String> msgResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		setDataUI();
		setupUI(findViewById(R.id.rl_parent));
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				loginUser();
			}
		});
		btn_forgotPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(LoginActivity.this, ForgotPassActivity.class);
				LoginActivity.this.startActivity(i);
				LoginActivity.this.finish();
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
				LoginActivity.this.startActivity(i);
				LoginActivity.this.finish();
			}
		});

	}
	
	private void setDataUI()
	{
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_forgotPass = (Button)findViewById(R.id.btn_forgotPass);
		btn_register = (Button)findViewById(R.id.btn_register);
		et_email = (EditText)findViewById(R.id.et_email);
		et_pass = (EditText)findViewById(R.id.et_pass);
	}
	
	private void loginUser()
	{
		str_email  = et_email.getText().toString().trim();
		str_pass  = et_pass.getText().toString().trim();
		System.out.println("str_userId:- "+str_userId);
		if(str_email.equalsIgnoreCase("")||str_email.length()==0)
		{
			et_email.requestFocus();
			et_email.setError(getResources().getString(R.string.message_email));
		}
		else if(!ValidationMethod.emailValidation(str_email))
		{
			et_email.requestFocus();
			et_email.setError(getResources().getString(R.string.message_validEmail));
		}
		else if(str_pass.equalsIgnoreCase("")||str_pass.length()==0)
		{
			et_pass.requestFocus();
			et_pass.setError(getResources().getString(R.string.message_password));
		}
		else
		{
			if(CheckInternetConection.isInternetConnection(getApplicationContext()))
			{
				new LoginClassLoad().execute();
			}
			else
			{
				DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
						getResources().getString(R.string.app_name), 
						getResources().getString(R.string.btn_ok), 
						LoginActivity.this);
			}
		}
		
	}
	private class LoginClassLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog logDialog=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			logDialog=new ProgressDialog(LoginActivity.this);
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
				String login_URL=getResources().getString(R.string.URL_app)+getResources().getString(R.string.URL_login);
				QWebService.initialize(login_URL);
				msgResponse=GeneralWebService.loginUser(getApplicationContext(), str_email, str_pass);
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
			logDialog.dismiss();
			getProfileJSON();
		}
	}
	
	private void getProfileJSON(){
		
		if(msgResponse!=null && !msgResponse.isEmpty())
		{
			if(msgResponse.get("message").equalsIgnoreCase("success"))
			{
				showToast(msgResponse.get("Description"));
				str_userId = msgResponse.get("UserId");
				System.out.println("str_userId:- "+str_userId);
				
				SharedPreferences.Editor editor=preference.edit();
				editor.putString("email", str_email);
				editor.putString("password", str_pass);
				editor.putString("UserId", str_userId);
			    editor.commit();
			    
				Intent i = new Intent(LoginActivity.this,HomeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				LoginActivity.this.startActivity(i);
				LoginActivity.this.finish();
			}
			else
			{
				showToast(msgResponse.get("description"));
			}
		}
		else
		{
			showToast(getResources().getString(R.string.app_error));
		}
	}
	
	private void setupUI(View view) {
		
		try 
		{
			 if(!(view instanceof EditText)) {
			        view.setOnTouchListener(new OnTouchListener() {
			            public boolean onTouch(View v, MotionEvent event) {
			                hideSoftKeyboard(LoginActivity.this);
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
		LoginActivity.this.finish();
	}

}
