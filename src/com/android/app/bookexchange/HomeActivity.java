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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.app.imageloader.ImageLoader;
import com.ak.app.servicedata.InfoBookData;
import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.staticmethod.DialogClasses;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class HomeActivity extends Activity {

	public static HomeActivity homeActivity;
	private Button btn_profile,btn_search;
	private TextView tv_noBook;
	private EditText et_search;
	private GridView gridview;
	private String str_searchKey="",str_userId,str_email;
	private String str_book_image,str_book_id,str_bookName,str_bookIssueDate,str_bookPrice,
					str_bookStatus,str_bookDes,str_bookURL,str_bookISBN;
	private ProgressDialog viewDialog=null;
	private SharedPreferences preference;
	private ArrayList<InfoBookData> arrAllBook,arrSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		setDataUI();
		setupUI(findViewById(R.id.rl_parent));
		homeActivity = HomeActivity.this;
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_userId = preference.getString("UserId", "");
		str_email = preference.getString("email", "");
		
		System.out.println("Email:- "+str_email);
		System.out.println("User Id:- "+str_userId);
		btn_profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
				HomeActivity.this.startActivity(i);
				HomeActivity.this.finish();
			}
		});

		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchBook();
			}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				str_searchKey = et_search.getText().toString().trim();
				if(!str_searchKey.equalsIgnoreCase("")||str_searchKey.length()>0)
				{
					str_book_image = arrSearch.get(position).book_image;
					str_book_id = arrSearch.get(position).book_id;
					str_bookName = arrSearch.get(position).book_name;
					str_bookPrice = arrSearch.get(position).book_price;
					str_bookStatus = arrSearch.get(position).book_status;
					str_bookURL = arrSearch.get(position).book_url;
					str_bookDes = arrSearch.get(position).book_description;
					str_bookISBN = arrSearch.get(position).book_isbn;
					str_bookIssueDate = arrSearch.get(position).book_issue_date;
					System.out.println("str_bookIssueDate: - "+str_bookIssueDate);
					System.out.println("str_book_image: - "+str_book_image);
					System.out.println("str_book_id: - "+str_book_id);
					Intent i =  new Intent(HomeActivity.this,BookDetailActivity.class);
					i.putExtra("KEY_IMAGE", str_book_image);
					i.putExtra("KEY_ID", str_book_id);
					i.putExtra("KEY_NAME", str_bookName);
					i.putExtra("KEY_PRICE", str_bookPrice);
					i.putExtra("KEY_STATUS", str_bookStatus);
					i.putExtra("KEY_URL", str_bookURL);
					i.putExtra("KEY_DES", str_bookDes);
					i.putExtra("KEY_ISBN", str_bookISBN);
					i.putExtra("KEY_DATE", str_bookIssueDate);
					HomeActivity.this.startActivity(i);
					HomeActivity.this.finish();
				}
				else
				{
					str_book_image = arrAllBook.get(position).book_image;
					str_book_id = arrAllBook.get(position).book_id;
					str_bookName = arrAllBook.get(position).book_name;
					str_bookPrice = arrAllBook.get(position).book_price;
					str_bookStatus = arrAllBook.get(position).book_status;
					str_bookURL = arrAllBook.get(position).book_url;
					str_bookDes = arrAllBook.get(position).book_description;
					str_bookISBN = arrAllBook.get(position).book_isbn;
					str_bookIssueDate = arrAllBook.get(position).book_issue_date;
					System.out.println("str_bookIssueDate: - "+str_bookIssueDate);
					System.out.println("str_book_image: - "+str_book_image);
					System.out.println("str_book_id: - "+str_book_id);
					Intent i =  new Intent(HomeActivity.this,BookDetailActivity.class);
					i.putExtra("KEY_IMAGE", str_book_image);
					i.putExtra("KEY_ID", str_book_id);
					i.putExtra("KEY_NAME", str_bookName);
					i.putExtra("KEY_PRICE", str_bookPrice);
					i.putExtra("KEY_STATUS", str_bookStatus);
					i.putExtra("KEY_URL", str_bookURL);
					i.putExtra("KEY_DES", str_bookDes);
					i.putExtra("KEY_ISBN", str_bookISBN);
					i.putExtra("KEY_DATE", str_bookIssueDate);
					HomeActivity.this.startActivity(i);
					HomeActivity.this.finish();
				}
				
			}
		});
		
	}
	
	private void setDataUI()
	{
		btn_profile = (Button)findViewById(R.id.btn_profile);
		btn_search = (Button)findViewById(R.id.btn_search);
		et_search = (EditText)findViewById(R.id.et_search);
		gridview = (GridView)findViewById(R.id.gridview);
		tv_noBook = (TextView)findViewById(R.id.tv_noBook);
	}
	
	private void searchBook()
	{
		str_searchKey = et_search.getText().toString().trim();
		if(str_searchKey.equalsIgnoreCase("")||str_searchKey.length()==0)
		{
			showToast(getResources().getString(R.string.message_searchKeyword));
			new AllBookListLoad().execute();
		}
		else
		{
			if(CheckInternetConection.isInternetConnection(getApplicationContext()))
			{
				new SearchBookListLoad().execute();
			}
			else
			{
				DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
						getResources().getString(R.string.app_name), 
						getResources().getString(R.string.btn_ok), 
						HomeActivity.this);
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (CheckInternetConection.isInternetConnection(getApplicationContext()))
		{
			 new AllBookListLoad().execute();
		} 
		else 
		{
			gridview.setVisibility(View.INVISIBLE);
			tv_noBook.setVisibility(View.VISIBLE);
			tv_noBook.setText(getResources().getString(R.string.tv_noBook));
			DialogClasses.showDialog1(getResources().getString(R.string.app_noConnection),
				getResources().getString(R.string.app_name), 
				getResources().getString(R.string.btn_ok), 
				HomeActivity.this);
		}
	}
	public class AllBookListLoad extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			viewDialog=new ProgressDialog(HomeActivity.this);
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
				str_email = preference.getString("email", "");
				
				System.out.println("Email:- "+str_email);
				System.out.println("User Id:- "+str_userId);
				String allRequir_URL=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_search);
				QWebService.initialize(allRequir_URL);
				arrAllBook=GeneralWebService.allBookData(getApplicationContext(),str_userId,str_searchKey);
				System.out.println("arrAllBook:- "+arrAllBook);
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
				if(arrAllBook!=null && arrAllBook.size()>0)
				{
					String msg=arrAllBook.get(0).json_message;
					if(msg.equalsIgnoreCase("success"))
					{
						gridview.setVisibility(View.VISIBLE);
						tv_noBook.setVisibility(View.INVISIBLE);
						gridview.setAdapter(new AllBookAdapter());
					}
					else
					{
						gridview.setVisibility(View.INVISIBLE);
						tv_noBook.setVisibility(View.VISIBLE);
						tv_noBook.setText(arrAllBook.get(0).json_description);
					}
				}
				else
				{
					try
					{
						gridview.setVisibility(View.INVISIBLE);
						tv_noBook.setVisibility(View.VISIBLE);
						tv_noBook.setText(getResources().getString(R.string.tv_noBook));
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
	private class AllBookAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return arrAllBook.size();
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
				row = View.inflate(HomeActivity.this, R.layout.row_book_list, null);
			}
			
			ImageView img_book = (ImageView)row.findViewById(R.id.img_book);
			ProgressBar pb= (ProgressBar)row.findViewById(R.id.pb);
			TextView tv_bookName = (TextView)row.findViewById(R.id.tv_bookName);
			TextView tv_bookPrice = (TextView)row.findViewById(R.id.tv_bookPrice);
			TextView tv_bookCondition = (TextView)row.findViewById(R.id.tv_bookCondition);
			TextView tv_bookISBN = (TextView)row.findViewById(R.id.tv_bookISBN);
			
			str_book_image = arrAllBook.get(position).book_image;
			str_book_id = arrAllBook.get(position).book_id;
			str_bookName = arrAllBook.get(position).book_name;
			str_bookPrice = arrAllBook.get(position).book_price;
			str_bookStatus = arrAllBook.get(position).book_status;
			str_bookURL = arrAllBook.get(position).book_url;
			str_bookDes = arrAllBook.get(position).book_description;
			str_bookISBN = arrAllBook.get(position).book_isbn;
			
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

	public class SearchBookListLoad extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			viewDialog=new ProgressDialog(HomeActivity.this);
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
				str_email = preference.getString("email", "");
				
				System.out.println("Email:- "+str_email);
				System.out.println("User Id:- "+str_userId);
				String allRequir_URL=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_search);
				QWebService.initialize(allRequir_URL);
				arrSearch=GeneralWebService.allBookData(getApplicationContext(),str_userId,str_searchKey);
				System.out.println("arrSearch:- "+arrSearch);
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
				if(arrSearch!=null && arrSearch.size()>0)
				{
					String msg=arrSearch.get(0).json_message;
					if(msg.equalsIgnoreCase("success"))
					{
						gridview.setVisibility(View.VISIBLE);
						tv_noBook.setVisibility(View.INVISIBLE);
						gridview.setAdapter(new SearchBookAdapter());
					}
					else
					{
						gridview.setVisibility(View.INVISIBLE);
						tv_noBook.setVisibility(View.VISIBLE);
						tv_noBook.setText(arrSearch.get(0).json_description);
					}
				}
				else
				{
					try
					{
						gridview.setVisibility(View.INVISIBLE);
						tv_noBook.setVisibility(View.VISIBLE);
						tv_noBook.setText(getResources().getString(R.string.tv_noBook));
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
	private class SearchBookAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return arrSearch.size();
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
				row = View.inflate(HomeActivity.this, R.layout.row_book_list, null);
			}
			
			ImageView img_book = (ImageView)row.findViewById(R.id.img_book);
			ProgressBar pb= (ProgressBar)row.findViewById(R.id.pb);
			TextView tv_bookName = (TextView)row.findViewById(R.id.tv_bookName);
			TextView tv_bookPrice = (TextView)row.findViewById(R.id.tv_bookPrice);
			TextView tv_bookCondition = (TextView)row.findViewById(R.id.tv_bookCondition);
			TextView tv_bookISBN = (TextView)row.findViewById(R.id.tv_bookISBN);
			
			str_book_image = arrSearch.get(position).book_image;
			str_book_id = arrSearch.get(position).book_id;
			str_bookName = arrSearch.get(position).book_name;
			str_bookPrice = arrSearch.get(position).book_price;
			str_bookStatus = arrSearch.get(position).book_status;
			str_bookURL = arrSearch.get(position).book_url;
			str_bookDes = arrSearch.get(position).book_description;
			str_bookISBN = arrSearch.get(position).book_isbn;
			
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
				img_book.setImageResource(R.drawable.ic_launcher);
				pb.setVisibility(View.GONE);
			}
			return row;
		}
		
	}

	
	private void showToast(String str)
	{
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	private void setupUI(View view) {
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(HomeActivity.this);
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

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		HomeActivity.this.finish();
	}


}
