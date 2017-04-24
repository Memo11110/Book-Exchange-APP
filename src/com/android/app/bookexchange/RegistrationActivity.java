package com.android.app.bookexchange;

import java.io.IOException;
import java.util.Dictionary;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegistrationActivity extends Activity {

	private Button btn_back,btn_submit,btn_reset;
	private EditText et_userName,et_email,et_password,et_confirmPass;
	private String str_userName,str_email,str_pass,str_confPass;
	private Dictionary<String, String> msgResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		setDataUI();
		setupUI(findViewById(R.id.rl_parent));
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
				RegistrationActivity.this.startActivity(i);
				RegistrationActivity.this.finish();
			}
		});
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				registration();
			}
		});
		btn_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				resetData();
			}
		});

	}
	
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		btn_reset = (Button)findViewById(R.id.btn_reset);
		et_userName = (EditText)findViewById(R.id.et_userName);
		et_email = (EditText)findViewById(R.id.et_email);
		et_password = (EditText)findViewById(R.id.et_password);
		et_confirmPass = (EditText)findViewById(R.id.et_confirmPass);
	}
	
	private void resetData()
	{
		et_userName.setText("");
		et_email.setText("");
		et_password.setText("");
		et_confirmPass.setText("");
	}
	private void registration()
	{
		str_userName  = et_userName.getText().toString().trim();
		str_email  = et_email.getText().toString().trim();
		str_pass  = et_password.getText().toString().trim();
		str_confPass  = et_confirmPass.getText().toString().trim();
		if(str_userName.equalsIgnoreCase("")||str_userName.length()==0)
		{
			et_userName.requestFocus();
			et_userName.setError(getResources().getString(R.string.message_Name));
		}
		else if(str_email.equalsIgnoreCase("")||str_email.length()==0)
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
			et_password.requestFocus();
			et_password.setError(getResources().getString(R.string.message_password));
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
				new RegisterLoad().execute();
			}
			else
			{
				DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
						getResources().getString(R.string.app_name), 
						getResources().getString(R.string.btn_ok), 
						RegistrationActivity.this);
			}
		}
		
	}
	private class RegisterLoad extends AsyncTask<Void,Void,Void>
	{
		private ProgressDialog regDialog=null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			regDialog=new ProgressDialog(RegistrationActivity.this);
			regDialog.setTitle(getResources().getString(R.string.app_name));
			regDialog.setMessage("Please wait");
			regDialog.setIndeterminate(true);
			regDialog.setCancelable(false);
			regDialog.show();
		}
				
		@Override
		protected Void doInBackground(Void... params) {
			try 
			{
				String reg_URL=getResources().getString(R.string.URL_app)+getResources().getString(R.string.URL_register);
				msgResponse=GeneralWebService.registerUserPost(reg_URL, getBundleData());
				System.out.println(msgResponse);
			} 
			catch (TimeoutException e) {
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			regDialog.dismiss();
			if(msgResponse!=null && !msgResponse.isEmpty())
			{
				if(msgResponse.get("message").equalsIgnoreCase("success"))
				{
					showToast(msgResponse.get("description"));
					Intent i=new Intent(RegistrationActivity.this, LoginActivity.class);
					RegistrationActivity.this.startActivity(i);
					RegistrationActivity.this.finish();
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
	}
	
	private Bundle getBundleData() {
		
		Bundle bundle = new Bundle();
		bundle.putString("name", str_userName);
		bundle.putString("email",str_email);
		bundle.putString("password",str_pass);
		return bundle;
	}
	
	private void setupUI(View view) {
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(RegistrationActivity.this);
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
	
	private void showToast(String str)
	{
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
		RegistrationActivity.this.startActivity(i);
		RegistrationActivity.this.finish();
	}

}
