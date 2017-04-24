package com.android.app.bookexchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MyPurchaseBooksActivity extends Activity {

	private Button btn_back;
	private ListView listview;
	private TextView tv_noBooks;
	private ProgressDialog viewDialog=null;
	private ArrayList<InfoBookData> arrMyPurchaseBook;
	private SharedPreferences preference;
	private String str_userId,str_email;
	private String str_book_image,str_book_id,str_bookName,str_bookIssueDate,str_bookPrice,
					str_bookStatus,str_bookDes,str_bookURL,str_bookISBN;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_book);
		
		setDataUI();
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_userId = preference.getString("UserId", "");
		str_email = preference.getString("email", "");
		
		System.out.println("Email:- "+str_email);
		System.out.println("User Id:- "+str_userId);
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(MyPurchaseBooksActivity.this, ProfileActivity.class);
				MyPurchaseBooksActivity.this.startActivity(i);
				MyPurchaseBooksActivity.this.finish();
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
				MyPurchaseBooksActivity.this);
		}
	}
	public class MyBookListLoad extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			viewDialog=new ProgressDialog(MyPurchaseBooksActivity.this);
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
				String mybook_URL=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_myPurchasedBooks);
				QWebService.initialize(mybook_URL);
				arrMyPurchaseBook=GeneralWebService.allMyBooksData(getApplicationContext(),str_userId);
				System.out.println("arrMyPurchaseBook:- "+arrMyPurchaseBook);
				
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
				if(arrMyPurchaseBook!=null && arrMyPurchaseBook.size()>0)
				{
					String msg=arrMyPurchaseBook.get(0).json_message;
					if(msg.equalsIgnoreCase("success"))
					{
						listview.setVisibility(View.VISIBLE);
						tv_noBooks.setVisibility(View.INVISIBLE);
						listview.setAdapter(new MyPurBooksAdapter());
					}
					else
					{
						listview.setVisibility(View.INVISIBLE);
						tv_noBooks.setVisibility(View.VISIBLE);
						tv_noBooks.setText(arrMyPurchaseBook.get(0).json_description);
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
	private class MyPurBooksAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return arrMyPurchaseBook.size();
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
				row = View.inflate(MyPurchaseBooksActivity.this, R.layout.row_my_books, null);
			}
			
			ImageView img_book = (ImageView)row.findViewById(R.id.img_book);
			ProgressBar pb= (ProgressBar)row.findViewById(R.id.pb);
			TextView tv_bookName = (TextView)row.findViewById(R.id.tv_bookName);
			TextView tv_bookPrice = (TextView)row.findViewById(R.id.tv_bookPrice);
			TextView tv_bookCondition = (TextView)row.findViewById(R.id.tv_bookCondition);
			TextView tv_bookISBN = (TextView)row.findViewById(R.id.tv_bookISBN);
			Button btn_edit = (Button)row.findViewById(R.id.btn_edit);
			Button btn_delete = (Button)row.findViewById(R.id.btn_delete);
			btn_edit.setVisibility(View.GONE);
			btn_delete.setVisibility(View.GONE);
			
			str_book_image = arrMyPurchaseBook.get(position).book_image;
			str_book_id = arrMyPurchaseBook.get(position).book_id;
			str_bookName = arrMyPurchaseBook.get(position).book_name;
			str_bookPrice = arrMyPurchaseBook.get(position).book_price;
			str_bookStatus = arrMyPurchaseBook.get(position).book_status;
			str_bookURL = arrMyPurchaseBook.get(position).book_url;
			str_bookDes = arrMyPurchaseBook.get(position).book_description;
			str_bookISBN = arrMyPurchaseBook.get(position).book_isbn;
			
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
		
			return row;
		}
		
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent(MyPurchaseBooksActivity.this, ProfileActivity.class);
		MyPurchaseBooksActivity.this.startActivity(i);
		MyPurchaseBooksActivity.this.finish();
	}


}
