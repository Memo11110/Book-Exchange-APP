package com.ak.app.webservices;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.ak.app.servicedata.InfoBookData;
import com.ak.app.servicedata.InfoProfileData;

public class GeneralWebService  {

	public static void initialize(String url) 
	{
		QWebService.initialize(url);
	}

	public static Dictionary<String, String> registerUserPost(String url, Bundle bundle) throws SocketTimeoutException,TimeoutException 
	{
		Dictionary<String, String> msgResponse = new Hashtable<String, String>();
		String objResponse = UtillClass.postData(url, "POST", bundle);

		System.out.println("Registration Response "+objResponse);
		
		if (objResponse != null && !objResponse.equals(""))
		{
			try 
			{
				JSONObject jsonObj = new JSONObject(objResponse.toString());
				try 
				{
					msgResponse.put("message", jsonObj.getString("message"));
					msgResponse.put("Description",jsonObj.getString("Description"));
					return msgResponse;
				} 
				catch (Exception e) {
				}
			}
			catch (JSONException e) {
			}
		}
		return msgResponse;
	}
	public static Dictionary<String, String> loginUser(Context context, String email, String password) throws SocketTimeoutException,TimeoutException 
	{
		Dictionary<String, String> msgResponse = new Hashtable<String, String>();
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("email", email);
		params.put("password", password);

		QBoolean callSuccess = new QBoolean(false);
		String objResponse = QWebService.callRestService(context, null, params,callSuccess);

		System.out.println("Login response "+objResponse);
		
		if (objResponse != null && !objResponse.equals(""))
		{
			try 
			{
				JSONObject jsonObj = new JSONObject(objResponse.toString());
				try 
				{
					msgResponse.put("message", jsonObj.getString("message"));
					msgResponse.put("Description",jsonObj.getString("Description"));
					msgResponse.put("UserId",jsonObj.getString("UserId"));
					return msgResponse;
				} 
				catch (Exception e) {
				}
			}
			catch (JSONException e) {
			}
		}
		return msgResponse;
	}
	
	public static Dictionary<String, String> forgotUserPassword(Context context, String email) throws SocketTimeoutException,TimeoutException 
	{
		Dictionary<String, String> msgResponse = new Hashtable<String, String>();
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("email", email);

		QBoolean callSuccess = new QBoolean(false);
		String objResponse = QWebService.callRestService(context, null, params,callSuccess);

		System.out.println("forgot response "+objResponse);
		
		if (objResponse != null && !objResponse.equals(""))
		{
			try 
			{
				JSONObject jsonObj = new JSONObject(objResponse.toString());
				try 
				{
					msgResponse.put("message", jsonObj.getString("message"));
					msgResponse.put("Description",jsonObj.getString("Description"));
					return msgResponse;
				} 
				catch (Exception e) {
				}
			}
			catch (JSONException e) {
			}
		}
		return msgResponse;
	}
	
	public static ArrayList<InfoProfileData> viewUserProfile(String url,Bundle bundle) throws MalformedURLException, IOException
	{
		ArrayList<InfoProfileData> profileData = new ArrayList<InfoProfileData>();
		
		String objResponse = UtillClass.postData(url, "POST", bundle);
		System.out.println("view response "+objResponse);

		if (objResponse != null && !objResponse.equals("")) 
		{
			try
			{
				JSONObject objJson = new JSONObject(objResponse.toString().trim());
				JSONObject jsonObject = new JSONObject();
				String msg = objJson.getString("message");
			
				InfoProfileData pData = new InfoProfileData();
				pData.json_message = msg;
				if (msg.equalsIgnoreCase("success")) 
				{
					jsonObject = objJson.getJSONObject("Description");
					System.out.println("First Name "+jsonObject.getString("name"));
					pData.user_name = jsonObject.getString("name");
					pData.user_email = jsonObject.getString("email");
					pData.user_about_me = jsonObject.getString("about");
					pData.user_phone = jsonObject.getString("phone");
					pData.user_total_uploaded_books = jsonObject.getString("total_uploaded_books");
					pData.user_total_purchased_books = jsonObject.getString("total_purchased_books");
					profileData.add(pData);
				} 
				else 
				{
					pData.json_description = objJson.getString("Description");
					profileData.add(pData);
				}
			}
			catch (JSONException e){
			}
		}
		bundle.clear();
		return profileData;
	}
	
	public static Dictionary<String, String> editUserProfile(String url,Bundle bundle) throws MalformedURLException, IOException
	{
		Dictionary<String, String> msgResponse = new Hashtable<String, String>();
		
		String objResponse = UtillClass.postData(url, "POST", bundle);
		System.out.println("Edit Profile response "+objResponse);

		
		if (objResponse != null && !objResponse.equals(""))
		{
			try 
			{
				JSONObject jsonObj = new JSONObject(objResponse.toString());
				try 
				{
					msgResponse.put("message", jsonObj.getString("message"));
					msgResponse.put("Description",jsonObj.getString("Description"));
					return msgResponse;
				} 
				catch (Exception e) {
				}
			}
			catch (JSONException e) {
			}
		}
		bundle.clear();
		return msgResponse;
	}
	
	public static ArrayList<InfoBookData> allBookData(Context context, String user_id,String keyword) throws SocketTimeoutException,TimeoutException 
	{
		ArrayList<InfoBookData> projectData = new ArrayList<InfoBookData>();
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("user_id",user_id);
		params.put("keyword",keyword);

		QBoolean callSuccess = new QBoolean(false);
		String objResponse = QWebService.callRestService(context, null, params,callSuccess);
		System.out.println("All Book Response "+objResponse);
		if (objResponse != null && !objResponse.equals("")) 
		{
			try 
			{
				JSONObject objJson = new JSONObject(objResponse.toString().trim());
				String msg = objJson.getString("message");
				if (msg.equalsIgnoreCase("success")) 
				{
					JSONArray arrJson = objJson.getJSONArray("Description");
					System.out.println("Book ARRAY Response"+arrJson.length());
					for (int i = 0; i < arrJson.length(); i++) 
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject = arrJson.getJSONObject(i);
						InfoBookData allData = new InfoBookData();
						allData.json_message=msg;
						allData.book_id = jsonObject.getString("id");
						allData.book_image = jsonObject.getString("book_image");
						allData.book_isbn = jsonObject.getString("book_isbn");
						allData.book_issue_date= jsonObject.getString("book_issue_date");
						allData.book_name = jsonObject.getString("book_name");
						allData.book_price = jsonObject.getString("book_price");
						allData.book_status = jsonObject.getString("book_status");
						allData.book_url = jsonObject.getString("book_url");
						allData.book_description = jsonObject.getString("book_description");
						projectData.add(allData);
					}
				} 
				else
				{
					try
					{
						JSONObject objJson1 = new JSONObject(objResponse.toString().trim());
						InfoBookData allData = new InfoBookData();
						allData.json_message=objJson1.getString("message");
						allData.json_description=objJson1.getString("Description");
						System.out.println("Error Description:- "+allData.json_description);
						projectData.add(allData);
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return projectData;
	}
	public static ArrayList<InfoBookData> allMyBooksData(Context context, String user_id) throws SocketTimeoutException,TimeoutException 
	{
		ArrayList<InfoBookData> projectData = new ArrayList<InfoBookData>();
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("user_id",user_id);

		QBoolean callSuccess = new QBoolean(false);
		String objResponse = QWebService.callRestService(context, null, params,callSuccess);
		System.out.println("My Book Response "+objResponse);
		if (objResponse != null && !objResponse.equals("")) 
		{
			try 
			{
				JSONObject objJson = new JSONObject(objResponse.toString().trim());
				String msg = objJson.getString("message");
				if (msg.equalsIgnoreCase("success")) 
				{
					JSONArray arrJson = objJson.getJSONArray("Description");
					System.out.println("My Book Response"+arrJson.length());
					for (int i = 0; i < arrJson.length(); i++) 
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject = arrJson.getJSONObject(i);
						InfoBookData allData = new InfoBookData();
						allData.json_message=msg;
						allData.book_id = jsonObject.getString("id");
						allData.book_image = jsonObject.getString("book_image");
						allData.book_isbn = jsonObject.getString("book_isbn");
						allData.book_issue_date= jsonObject.getString("book_issue_date");
						allData.book_name = jsonObject.getString("book_name");
						allData.book_price = jsonObject.getString("book_price");
						allData.book_status = jsonObject.getString("book_status");
						allData.book_url = jsonObject.getString("book_url");
						allData.book_description = jsonObject.getString("book_description");
						projectData.add(allData);
					}
				} 
				else
				{
					try
					{
						JSONObject objJson1 = new JSONObject(objResponse.toString().trim());
						InfoBookData allData = new InfoBookData();
						allData.json_message=objJson1.getString("message");
						allData.json_description=objJson1.getString("Description");
						System.out.println("Error Description:- "+allData.json_description);
						projectData.add(allData);
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return projectData;
	}
	public static ArrayList<InfoBookData> allMyPurchaseBookData(Context context, String user_id) throws SocketTimeoutException,TimeoutException 
	{
		ArrayList<InfoBookData> projectData = new ArrayList<InfoBookData>();
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("user_id",user_id);

		QBoolean callSuccess = new QBoolean(false);
		String objResponse = QWebService.callRestService(context, null, params,callSuccess);
		System.out.println("Purchased Book Response "+objResponse);
		if (objResponse != null && !objResponse.equals("")) 
		{
			try 
			{
				JSONObject objJson = new JSONObject(objResponse.toString().trim());
				String msg = objJson.getString("message");
				if (msg.equalsIgnoreCase("success")) 
				{
					JSONArray arrJson = objJson.getJSONArray("Description");
					System.out.println("Purchased Array Response"+arrJson.length());
					for (int i = 0; i < arrJson.length(); i++) 
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject = arrJson.getJSONObject(i);
						InfoBookData allData = new InfoBookData();
						allData.json_message=msg;
						allData.book_id = jsonObject.getString("id");
						allData.book_image = jsonObject.getString("book_image");
						allData.book_isbn = jsonObject.getString("book_isbn");
						allData.book_issue_date= jsonObject.getString("book_issue_date");
						allData.book_name = jsonObject.getString("book_name");
						allData.book_price = jsonObject.getString("book_price");
						allData.book_status = jsonObject.getString("book_status");
						allData.book_url = jsonObject.getString("book_url");
						allData.book_description = jsonObject.getString("book_description");
						projectData.add(allData);
					}
				} 
				else
				{
					try
					{
						JSONObject objJson1 = new JSONObject(objResponse.toString().trim());
						InfoBookData allData = new InfoBookData();
						allData.json_message=objJson1.getString("message");
						allData.json_description=objJson1.getString("Description");
						System.out.println("Error Description:- "+allData.json_description);
						projectData.add(allData);
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return projectData;
	}
	public static Dictionary<String, String> orderBook(String url, Bundle bundle) throws SocketTimeoutException,TimeoutException 
	{
		Dictionary<String, String> msgResponse = new Hashtable<String, String>();
		String objResponse = UtillClass.postData(url, "POST", bundle);

		System.out.println("Order Book Response "+objResponse);
		if (objResponse != null && !objResponse.equals(""))
		{
			try 
			{
				JSONObject jsonObj = new JSONObject(objResponse.toString());
				try 
				{
					msgResponse.put("message", jsonObj.getString("message"));
					msgResponse.put("Description",jsonObj.getString("Description"));
					return msgResponse;
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return msgResponse;
	}
	
	public static Dictionary<String, String> deleteBook(String url, Bundle bundle) throws SocketTimeoutException,TimeoutException 
	{
		Dictionary<String, String> msgResponse = new Hashtable<String, String>();
		String objResponse = UtillClass.postData(url, "POST", bundle);

		System.out.println("Delete Book Response "+objResponse);
		if (objResponse != null && !objResponse.equals(""))
		{
			try 
			{
				JSONObject jsonObj = new JSONObject(objResponse.toString());
				try 
				{
					msgResponse.put("message", jsonObj.getString("message"));
					msgResponse.put("description",jsonObj.getString("Description"));
					return msgResponse;
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return msgResponse;
	}
	
}