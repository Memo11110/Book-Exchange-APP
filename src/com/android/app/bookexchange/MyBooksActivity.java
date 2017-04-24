package com.android.app.bookexchange;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.concurrent.TimeoutException;

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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.app.imageloader.ImageLoader;
import com.ak.app.servicedata.InfoBookData;
import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.staticmethod.DialogClasses;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class MyBooksActivity extends Activity {

	private Button btn_back;
	private ListView listview;
	private TextView tv_noBooks;
	private ArrayList<InfoBookData> arrMyBook;
	private ProgressDialog viewDialog=null;
	private SharedPreferences preference;
	private String str_userId,str_email;
	private String str_book_image,str_book_id,str_bookName,str_bookIssueDate,str_bookPrice,
					str_bookStatus,str_bookDes,str_bookURL,str_bookISBN;
	private Dictionary<String, String> msgResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybook);
		
		setDataUI();
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_userId = preference.getString("UserId", "");
		str_email = preference.getString("email", "");
		
		System.out.println("Email:- "+str_email);
		System.out.println("User Id:- "+str_userId);
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(MyBooksActivity.this, ProfileActivity.class);
				MyBooksActivity.this.startActivity(i);
				MyBooksActivity.this.finish();
			}
		});
		
	}
	
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		listview = (ListView)findViewById(R.id.listview);
		tv_noBooks = (TextView)findViewById(R.id.tv_noBooks);
	}
	
	private void showToast(String str)
	{
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		if (CheckInternetConection.isInternetConnection(getApplicationContext()))
		{
			 new MyBookListLoad().execute();
		} 
		else 
		{
			listview.setVisibility(View.INVISIBLE);
			tv_noBooks.setVisibility(View.VISIBLE);
			tv_noBooks.setText(getResources().getString(R.string.tv_noBook));
			DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
				getResources().getString(R.string.app_name), 
				getResources().getString(R.string.btn_ok), 
				MyBooksActivity.this);
		}
	}
	public class MyBookListLoad extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			viewDialog=new ProgressDialog(MyBooksActivity.this);
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
				str_userId = preference.getString("UserId", "");
				String mybook_URL=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_mybooks);
				QWebService.initialize(mybook_URL);
				arrMyBook=GeneralWebService.allMyBooksData(getApplicationContext(),str_userId);
				System.out.println("arrMyBook:- "+arrMyBook);
				
			}
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch (TimeoutException e)
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
//				listview.setAdapter(new MyBooksAdapter());
				if(arrMyBook!=null && arrMyBook.size()>0)
				{
					String msg=arrMyBook.get(0).json_message;
					if(msg.equalsIgnoreCase("success"))
					{
						listview.setVisibility(View.VISIBLE);
						tv_noBooks.setVisibility(View.INVISIBLE);
						listview.setAdapter(new MyBooksAdapter());
					}
					else
					{
						listview.setVisibility(View.INVISIBLE);
						tv_noBooks.setVisibility(View.VISIBLE);
						tv_noBooks.setText(arrMyBook.get(0).json_description);
					}
				}
				else
				{
					try
					{
						listview.setVisibility(View.INVISIBLE);
						tv_noBooks.setVisibility(View.VISIBLE);
						tv_noBooks.setText(getResources().getString(R.string.tv_noBook));
						showToast(getResources().getString(R.string.app_error));
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	private class MyBooksAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return arrMyBook.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView; 
			if(row==null)
			{
				row = View.inflate(MyBooksActivity.this, R.layout.row_my_books, null);
			}
			
			ImageView img_book = (ImageView)row.findViewById(R.id.img_book);
			ProgressBar pb= (ProgressBar)row.findViewById(R.id.pb);
			TextView tv_bookName = (TextView)row.findViewById(R.id.tv_bookName);
			TextView tv_bookPrice = (TextView)row.findViewById(R.id.tv_bookPrice);
			TextView tv_bookCondition = (TextView)row.findViewById(R.id.tv_bookCondition);
			TextView tv_bookISBN = (TextView)row.findViewById(R.id.tv_bookISBN);
			Button btn_edit = (Button)row.findViewById(R.id.btn_edit);
			Button btn_delete = (Button)row.findViewById(R.id.btn_delete);
			

			str_book_image = arrMyBook.get(position).book_image;
			str_book_id = arrMyBook.get(position).book_id;
			str_bookName = arrMyBook.get(position).book_name;
			str_bookPrice = arrMyBook.get(position).book_price;
			str_bookStatus = arrMyBook.get(position).book_status;
			str_bookURL = arrMyBook.get(position).book_url;
			str_bookDes = arrMyBook.get(position).book_description;
			str_bookISBN = arrMyBook.get(position).book_isbn;
			str_bookIssueDate= arrMyBook.get(position).book_issue_date;
			
			tv_bookName.setText(str_bookName);
			tv_bookPrice.setText(str_bookPrice);
			tv_bookCondition.setText(str_bookStatus);
			tv_bookISBN.setText(str_bookISBN);
			
			if(str_book_image!=null && !str_book_image.equals(""))
			{
//				String url=getResources().getString(R.string.URL_requirImage)+str_image1;
				img_book.setTag(str_book_image);
				System.out.println("img url:-  "+str_book_image);
				ImageLoader.DisplayImage(str_book_image, img_book, pb);
			}
			else
			{
				img_book.setImageResource(R.drawable.no_image);
				pb.setVisibility(View.GONE);
			}
		
			btn_edit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					str_book_id = arrMyBook.get(position).book_id;
					str_book_image = arrMyBook.get(position).book_image;
					str_bookName = arrMyBook.get(position).book_name;
					str_bookPrice = arrMyBook.get(position).book_price;
					str_bookStatus = arrMyBook.get(position).book_status;
					str_bookURL = arrMyBook.get(position).book_url;
					str_bookDes = arrMyBook.get(position).book_description;
					str_bookISBN = arrMyBook.get(position).book_isbn;
					str_bookIssueDate= arrMyBook.get(position).book_issue_date;
					Intent i = new Intent(MyBooksActivity.this, EditBookDetailActivity.class);
					i.putExtra("str_book_id", str_book_id);
					i.putExtra("str_book_image", str_book_image);
					i.putExtra("str_bookName", str_bookName);
					i.putExtra("str_bookPrice", str_bookPrice);
					i.putExtra("str_bookStatus", str_bookStatus);
					i.putExtra("str_bookURL", str_bookURL);
					i.putExtra("str_bookDes", str_bookDes);
					i.putExtra("str_bookISBN", str_bookISBN);
					i.putExtra("str_bookIssueDate", str_bookIssueDate);
					MyBooksActivity.this.startActivity(i);
					
				}
			});
			btn_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					str_book_id = arrMyBook.get(position).book_id;
					showorderDialog(getResources().getString(R.string.app_deleteBook),
							getResources().getString(R.string.app_name),
							getResources().getString(R.string.btn_yes) , 
							getResources().getString(R.string.btn_no), 
							MyBooksActivity.this);
				}
			});
				
			return row;
		}
		
	}
	
	public  void showorderDialog(String msg, String ttl, String pos, String neg, final Activity ctx) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(msg).setCancelable(false).setPositiveButton(pos,new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				if(CheckInternetConection.isInternetConnection(getApplicationContext()))
				{
					new DeleteBookLoad().execute();
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
	
	private class DeleteBookLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog logDialog=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			logDialog=new ProgressDialog(MyBooksActivity.this);
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
				String deleteBook_Url=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_deletebook);
				QWebService.initialize(deleteBook_Url);
				msgResponse=GeneralWebService.orderBook(deleteBook_Url, getBundleData());
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
						Intent mainIntent = new Intent(MyBooksActivity.this,MyBooksActivity.class);
						MyBooksActivity.this.startActivity(mainIntent);
						MyBooksActivity.this.finish();
						
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
		bundle.putString("book_id", str_book_id);
		return bundle;
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent(MyBooksActivity.this, ProfileActivity.class);
		MyBooksActivity.this.startActivity(i);
		MyBooksActivity.this.finish();
	}


}
