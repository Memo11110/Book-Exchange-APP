package com.android.app.bookexchange;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ak.app.imageloader.ImageLoader;
import com.ak.app.staticmethod.CheckInternetConection;
import com.ak.app.webservices.GeneralWebService;
import com.ak.app.webservices.QWebService;

public class EditBookDetailActivity extends Activity {

	private Button btn_back,btn_browseImage,btn_submit,btn_reset,
				   btn_condition,btn_issueDate;
	private EditText et_bookName,et_bookISBN,et_price,et_bookUrl,
					 et_description;
	private String str_userId="",str_email,str_book_id,str_book_image,str_bookName,str_bookPrice,
			str_bookStatus,str_bookURL,str_bookDes,str_bookISBN,str_bookIssueDate;
				   
	private ImageView img_book;
	private ProgressBar pb;
	private Calendar myCalendar;
//	private int from_year, from_month, from_day;
	
	 public static final int MEDIA_TYPE_IMAGE = 1;
		// directory name to store captured images and videos
	 private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	 
	 private Uri fileUri; // file url to store image/video
	
	private static int RESULT_LOAD_IMAGE = 101;
	private static final int CAMERA_PIC_REQUEST = 100;
	private int CAM_REQUEST_CODE=0;
	private Intent CAM_REQUST_DATA;
	private Bitmap bitmap;
	private ByteArrayOutputStream stream;
	private byte[] buffer;
	private String str_imageName = "avatar.png";
	private Dictionary<String, String> msgResponse;
	private SharedPreferences preference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_book);
		
		setDataUI();
		setupUI(findViewById(R.id.rl_parent));
		
		preference = this.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
		
		str_userId = preference.getString("UserId", "");
		str_email = preference.getString("email", "");
		
		System.out.println("Email:- "+str_email);
		System.out.println("User Id:- "+str_userId);
		
		getIntentData();
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditBookDetailActivity.this.finish();
			}
		});
		btn_browseImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				chooseImage();
			}
		});
		btn_condition.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				selectCondition();
			}
		});
		btn_issueDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				myCalendar = Calendar.getInstance();
				new DatePickerDialog(EditBookDetailActivity .this, date, myCalendar
	                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
	                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editBook();
			}
		});
		btn_reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getIntentData();
			}
		});

	}
	
	private void setDataUI()
	{
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_browseImage = (Button)findViewById(R.id.btn_browseImage);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		btn_reset = (Button)findViewById(R.id.btn_reset);
		et_bookName = (EditText)findViewById(R.id.et_bookName);
		et_bookISBN = (EditText)findViewById(R.id.et_bookISBN);
		et_price = (EditText)findViewById(R.id.et_price);
		et_bookUrl = (EditText)findViewById(R.id.et_bookUrl);
		btn_condition = (Button)findViewById(R.id.btn_condition);
		btn_issueDate = (Button)findViewById(R.id.btn_issueDate);
		et_description = (EditText)findViewById(R.id.et_description);
		img_book = (ImageView)findViewById(R.id.img_book);
		pb= (ProgressBar)findViewById(R.id.pb);
		pb.setVisibility(View.VISIBLE);
	}
	
	private void getIntentData()
	{
		str_book_id = getIntent().getStringExtra("str_book_id");
		str_book_image = getIntent().getStringExtra("str_book_image");
		str_bookName = getIntent().getStringExtra("str_bookName");
		str_bookPrice = getIntent().getStringExtra("str_bookPrice");
		str_bookStatus = getIntent().getStringExtra("str_bookStatus");
		str_bookURL = getIntent().getStringExtra("str_bookURL");
		str_bookDes = getIntent().getStringExtra("str_bookDes");
		str_bookISBN = getIntent().getStringExtra("str_bookISBN");
		str_bookIssueDate = getIntent().getStringExtra("str_bookIssueDate");
		setData();
		
	}
	private void setData()
	{
		et_bookName.setText(str_bookName);
		et_bookISBN.setText(str_bookISBN);
		et_price.setText(str_bookPrice);
		et_bookUrl.setText(str_bookURL);
		btn_condition.setText(str_bookStatus);
		btn_issueDate.setText(str_bookIssueDate);
		et_description.setText(str_bookDes);
	
		if(str_book_image!=null && !str_book_image.equals(""))
		{
//			String url=getResources().getString(R.string.URL_requirImage)+str_image1;
			img_book.setTag(str_book_image);
			System.out.println("img url:-  "+str_book_image);
			ImageLoader.DisplayImage(str_book_image, img_book, pb);
		}
		else
		{
			img_book.setImageResource(R.drawable.no_image);
			pb.setVisibility(View.GONE);
		}
	}
	@SuppressLint("InlinedApi")
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
		
	    @Override
	    public void onDateSet(DatePicker view, int year, int monthOfYear,
	            int dayOfMonth) {
	    	
	    	myCalendar.set(Calendar.YEAR, year);
	        myCalendar.set(Calendar.MONTH, monthOfYear);
	        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	        
			updateLabel();
	    }

	};
	private void updateLabel() 
	{
//	    String myFormat = "MM/dd/yyyy"; //In which you need put here
		 String myFormat = "yyyy-MM-dd";
	    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
	    btn_issueDate.setText(sdf.format(myCalendar.getTime()));
	}
	private void selectCondition()
	{
		try 
		{
			final CharSequence[] items = {"New","Used"};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.hint_condition));
			builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					str_bookStatus=String.valueOf(items[which]);
					System.out.println("str_condition:- " +str_bookStatus);
					btn_condition.setText(str_bookStatus);
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public  long getDateInMili(String date)
	{
		SimpleDateFormat df=new SimpleDateFormat("MM/dd/yyyy",Locale.US);
		Date new_date;
		long time=0L;
		try 
		{
			new_date = df.parse(date);
			time=new_date.getTime();
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return time;
	}
	public void chooseImage()
	{
		final CharSequence[] items = {
				getResources().getString(R.string.app_dialogTakePicture),
				getResources().getString(R.string.app_dialogPickGallery) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.app_dialogSelectImage));
		builder.setSingleChoiceItems(items, -1,new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0)
				{
//					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//					startActivityForResult(cameraIntent,CAMERA_PIC_REQUEST);
//					dialog.dismiss();
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					startActivityForResult(cameraIntent,CAMERA_PIC_REQUEST);
					dialog.dismiss();
				}
				else
				{
					Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
					dialog.dismiss();
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
 
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
 
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
 
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
 
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
 
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
     // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }  else {
            return null;
        }
 
        return mediaFile;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) 
		{
			CAM_REQUEST_CODE=requestCode;
			CAM_REQUST_DATA=data;
			new SetImageGallery().execute();
		} 
		else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && null != data)
		{
			CAM_REQUEST_CODE=requestCode;
			CAM_REQUST_DATA=data;
			new SetImageGallery().execute();
		}
	}
	
	private class SetImageGallery extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog imagedialog = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			imagedialog = new ProgressDialog(EditBookDetailActivity.this);
			imagedialog.setMessage(getResources().getString(R.string.app_pleaseWait));
			imagedialog.setCancelable(false);
			imagedialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (CAM_REQUEST_CODE==CAMERA_PIC_REQUEST)
			{
//				bitmap = (Bitmap) CAM_REQUST_DATA.getExtras().get("data");
				BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 8;
	            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
	                    options);
			}
			else
			{
				Uri selectedImage = CAM_REQUST_DATA.getData();
				String[] filePatchColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,filePatchColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePatchColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				BitmapFactory.Options o2 = new BitmapFactory.Options();
				bitmap = BitmapFactory.decodeFile(picturePath, o2);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			if (bitmap.getHeight() <= 50 && bitmap.getWidth() <= 50)
			{
			showToast("Please Upload image with good quility");
			} 
			else
			{
				try 
				{
					img_book.setImageResource(android.R.color.transparent);
					img_book.setImageBitmap(bitmap);
					stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					buffer = stream.toByteArray();
					stream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				stream=null;
			}
			imagedialog.dismiss();
		}
	}

	private void editBook()
	{
		str_bookName  = et_bookName.getText().toString().trim();
		str_bookISBN  = et_bookISBN.getText().toString().trim();
		str_bookPrice  = et_price.getText().toString().trim();
		str_bookURL  = et_bookUrl.getText().toString().trim();
		str_bookStatus  = btn_condition.getText().toString().trim();
		str_bookIssueDate  = btn_issueDate.getText().toString().trim();
		str_bookDes  = et_description.getText().toString().trim();
		System.out.println("str_userId:- "+str_userId);
		if(str_bookName.equalsIgnoreCase("")||str_bookName.length()==0)
		{
			et_bookName.requestFocus();
			et_bookName.setError(getResources().getString(R.string.message_bookName));
		}
		else if(str_bookISBN.equalsIgnoreCase("")||str_bookISBN.length()==0)
		{
			et_bookISBN.requestFocus();
			et_bookISBN.setError(getResources().getString(R.string.message_bookIsbn));
		}
		else if(str_bookPrice.equalsIgnoreCase("")||str_bookPrice.length()==0)
		{
			et_price.requestFocus();
			et_price.setError(getResources().getString(R.string.message_bookPrice));
		}
		else if(str_bookURL.equalsIgnoreCase("")||str_bookURL.length()==0)
		{
			et_bookUrl.requestFocus();
			et_bookUrl.setError(getResources().getString(R.string.message_bookURL));
		}
		else if(str_bookStatus.equalsIgnoreCase("")||str_bookStatus.length()==0)
		{
//			btn_condition.requestFocus();
			btn_condition.setError(getResources().getString(R.string.message_bookCondition));
		}
		else if(str_bookIssueDate.equalsIgnoreCase("")||str_bookIssueDate.length()==0)
		{
//			btn_issueDate.requestFocus();
			btn_issueDate.setError(getResources().getString(R.string.message_bookIssueDate));
		}
		else if(str_bookDes.equalsIgnoreCase("")||str_bookDes.length()==0)
		{
			et_description.requestFocus();
			et_description.setError(getResources().getString(R.string.message_bookDescr));
		}
		else
		{
			if(CheckInternetConection.isInternetConnection(getApplicationContext()))
			{
				new EditBookLoad().execute();
			}
			else
			{
				showToast(getResources().getString(R.string.app_noConnection));
			}
		}
		
	}
	private class EditBookLoad extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog logDialog=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			logDialog=new ProgressDialog(EditBookDetailActivity.this);
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
				String editBook_URL=getResources().getString(R.string.URL_appbook)+getResources().getString(R.string.URL_editbook);
				QWebService.initialize(editBook_URL);
				msgResponse=GeneralWebService.editUserProfile(editBook_URL, getBundleData());
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
						
						EditBookDetailActivity.this.finish();
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
//		str_bookIssueDate  = btn_issueDate.getText().toString().trim();
//		System.out.println("str_issudate:- "+str_bookIssueDate);
//		str_bookIssueDate = String.valueOf((getDateInMili(str_bookIssueDate)));
//		System.out.println(" milli str_issudate:- "+str_bookIssueDate);
		
		str_bookIssueDate  = btn_issueDate.getText().toString().trim();
//		String myFormat = "yyyy-MM-dd"; //In which you need put here
//		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//		str_bookIssueDate = (sdf.format(str_bookIssueDate));
		System.out.println("str_bookIssueDate:- "+str_bookIssueDate);
		
		Bundle bundle =new Bundle();
		bundle.putString("user_id", str_userId);
		bundle.putString("book_id", str_book_id);
		bundle.putString("book_name", str_bookName);
		bundle.putString("status", str_bookStatus);
		bundle.putString("book_price", str_bookPrice);
		bundle.putString("book_isbn", str_bookISBN);
		bundle.putString("book_description", str_bookDes);
		bundle.putString("book_url", str_bookURL);
		bundle.putString("book_issue_date", str_bookIssueDate);
		if(buffer!=null)
		{
			bundle.putByteArray("book_image", buffer);
			System.out.println("image buffer "+buffer);
			bundle.putString("filename", str_imageName);	
			System.out.println("filename buffer "+str_imageName);
		}
		return bundle;
	}
	
	
	private void setupUI(View view) {
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(EditBookDetailActivity.this);
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
		EditBookDetailActivity.this.finish();
	}

}
