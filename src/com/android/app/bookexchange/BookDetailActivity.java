package com.android.app.bookexchange;

import java.net.SocketTimeoutException;
import java.util.Dictionary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.app.imageloader.ImageLoader;
import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class BookDetailActivity extends Activity {

	private Button btn_back,btn_order,btn_cancel;
	private TextView tv_bookName,tv_bookISBN,tv_bookPrice,tv_bookCondition,
					 tv_bookIssueDate,tv_bookUrl,tv_description,tv_uploadedBY;
	private String str_bookName,str_bookISBN,str_bookPrice,str_bookStatus,str_image,
					str_bookIssueDate,str_bookUrl,str_bookDesc,str_uploadedBy="",
					str_bookID,str_userId,str_email;
	private SharedPreferences preference;
	private ImageView img_book;
	private ProgressBar pb;
	private Dictionary<String, String> msgResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);
		
		setDataUI();
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_userId = preference.getString("UserId", "");
		str_email = preference.getString("email", "");
		
		System.out.println("Email:- "+str_email);
		System.out.println("User Id:- "+str_userId);
		
		str_image = getIntent().getStringExtra("KEY_IMAGE");
		str_bookID = getIntent().getStringExtra("KEY_ID");
		str_bookName = getIntent().getStringExtra("KEY_NAME");
		str_bookPrice = getIntent().getStringExtra("KEY_PRICE");
		str_bookStatus = getIntent().getStringExtra("KEY_STATUS");
		str_bookUrl = getIntent().getStringExtra("KEY_URL");
		str_bookDesc = getIntent().getStringExtra("KEY_DES");
		str_bookISBN = getIntent().getStringExtra("KEY_ISBN");
		str_bookIssueDate = getIntent().getStringExtra("KEY_DATE");
		
		System.out.println("str_uploadedBy:- "+str_uploadedBy);
		
		setBookDetail();
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BookDetailActivity.this, HomeActivity.class);
				BookDetailActivity.this.startActivity(i);
				BookDetailActivity.this.finish();
			}
		});
		btn_order.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showorderDialog(getResources().getString(R.string.app_orderBook),
						getResources().getString(R.string.app_name),
						getResources().getString(R.string.btn_yes) , 
						getResources().getString(R.string.btn_no), 
						BookDetailActivity.this);
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(BookDetailActivity.this, HomeActivity.class);
				BookDetailActivity.this.startActivity(i);
				BookDetailActivity.this.finish();
			}
		});

	}
	
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_order = (Button)findViewById(R.id.btn_order);
		btn_cancel = (Button)findViewById(R.id.btn_cancel);
		tv_bookName = (TextView)findViewById(R.id.tv_bookName);
		tv_bookISBN = (TextView)findViewById(R.id.tv_bookISBN);
		tv_bookPrice = (TextView)findViewById(R.id.tv_bookPrice);
		tv_bookCondition = (TextView)findViewById(R.id.tv_bookCondition);
		tv_bookIssueDate = (TextView)findViewById(R.id.tv_bookIssueDate);
		tv_bookUrl = (TextView)findViewById(R.id.tv_bookUrl);
		tv_description = (TextView)findViewById(R.id.tv_description);
		tv_uploadedBY = (TextView)findViewById(R.id.tv_uploadedBY);
		img_book = (ImageView)findViewById(R.id.img_book);
		pb = (ProgressBar)findViewById(R.id.pb);
		
		
	}
	private void setBookDetail()
	{
		tv_bookName.setText(str_bookName);
		tv_bookISBN.setText(str_bookISBN);
		tv_bookPrice.setText(str_bookPrice);
		tv_bookCondition.setText(str_bookStatus);
		tv_bookIssueDate.setText(str_bookIssueDate);
		tv_bookUrl.setText(str_bookUrl);
		tv_description.setText(str_bookDesc);
		tv_uploadedBY.setText("");
		
		if(str_image!=null && !str_image.equals(""))
		{
//			String url=getResources().getString(R.string.URL_requirImage)+str_image1;
			img_book.setTag(str_image);
			System.out.println("img url:-  "+str_image);
			ImageLoader.DisplayImage(str_image, img_book, pb);
		}
		else
		{
			img_book.setImageResource(R.drawable.ic_launcher);
			pb.setVisibility(View.GONE);
		}
	}
	public  void showorderDialog(String msg, String ttl, String pos, String neg, final Activity ctx) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setIcon(R.drawable.cart_book);
		builder.setMessage(msg).setCancelable(false).setPositiveButton(pos,new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				if(CheckInternetConection.isInternetConnection(getApplicationContext()))
				{
					new OrderBookLoad().execute();
				}
				else
				{
					showToast(getResources().getString(R.string.app_noConnection));
				}
				dialog.cancel();
			}
		}).setNegativeButton(neg,new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				dialog.cancel();
			}
		});
		if (ttl != null && !ttl.equals(""))
			builder.setTitle(ttl);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private class OrderBookLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog logDialog=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			logDialog=new ProgressDialog(BookDetailActivity.this);
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
				String orderBook_Url=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_buy);
				QWebService.initialize(orderBook_Url);
				msgResponse=GeneralWebService.orderBook(orderBook_Url, getBundleData());
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
				if(msgResponse!=null && !msgResponse.isEmpty())
				{
					if(msgResponse.get("message").equalsIgnoreCase("success"))
					{
						showToast(msgResponse.get("Description"));
						Intent mainIntent = new Intent(BookDetailActivity.this,HomeActivity.class);
						BookDetailActivity.this.startActivity(mainIntent);
						BookDetailActivity.this.finish();
						
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
	}
	
	private  Bundle getBundleData()
	{
		Bundle bundle =new Bundle();
		bundle.putString("user_id", str_userId);
		bundle.putString("book_id", str_bookID);
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
		Intent i = new Intent(BookDetailActivity.this, HomeActivity.class);
		BookDetailActivity.this.startActivity(i);
		BookDetailActivity.this.finish();
	}

}
