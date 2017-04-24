// FileName: QWebService.java
// ----------------------------------------------------------------------------
// 
// QBitSystems, 2008
// www.qbitsystems.com
//
// Description:     
// Created:			Shamim
// Last Changed By: Shamim
//
// FileRevision:        1.0.0
// FileRevision Date:	12/26/08
//
// ============================================================================

package com.ak.app.webservices;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

public final class QWebService 
{
	private static String mUrl;
	private static HttpClient httpClient = null;

	public static void initialize(String url) 
	{
		mUrl = url;
	}

	public static String callRestService(Context context, String methodName,QBoolean callSuccess)throws TimeoutException,SocketTimeoutException 
	{
		return callRestService(context, methodName, null, callSuccess);
	}

	public static String callRestService(Context context, String methodName,Dictionary<String, String> params, QBoolean callSuccess)throws TimeoutException, SocketTimeoutException 
	{
		
		String url = getRestUrl(methodName, params);
		callSuccess.setValue(false);
		String strResponse = "";
		if (httpClient == null) 
		{
			HttpPost httpPost = new HttpPost(url);
			StringEntity se = null;
			try 
			{
				se = new StringEntity("Hello", HTTP.UTF_8);
			}
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
			httpPost.setEntity(se);

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 120000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
			int timeoutSocket = 120000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			httpClient = new DefaultHttpClient(httpParameters);
		}

		HttpGet getMethod = null;
		try 
		{
			getMethod = new HttpGet(new URI(url));
		}
		catch (URISyntaxException e1) 
		{
			e1.printStackTrace();
		}

		try 
		{
			HttpResponse response = httpClient.execute(getMethod);

			if (response != null) 
			{
				HttpEntity entity = null;
				try
				{
					entity = response.getEntity();
					@SuppressWarnings("unused")
					long i = entity.getContentLength();
				}
				catch (Exception e) 
				{
				
				}
				if (entity != null) 
				{
					strResponse = getResponse(entity);
				} 
				else 
				{
				}
			}
			else
			{
			}
			callSuccess.setValue(true);
		} 
		catch (Exception e)
		{
			//Log.e("Exception", e.getMessage());
		}
		finally 
		{
			getMethod.abort();
		}

		if (strResponse == "") {
			return null;
		}

		return strResponse;
	}

	@SuppressWarnings("deprecation")
	private static String getRestUrl(String methodName,Dictionary<String, String> params) 
	{
		StringBuffer sb = new StringBuffer(mUrl);
		if (methodName != null)
			sb.append(methodName);

		if (params != null) 
		{
			sb.append('?');
			for (Enumeration<String> keyEnu = params.keys(); keyEnu.hasMoreElements();)
			{
				final String key = keyEnu.nextElement();
				String strKey = java.net.URLEncoder.encode(key);
				String strVal = java.net.URLEncoder.encode(params.get(key));
				sb.append(strKey);
				sb.append('=');
				sb.append(strVal);
				sb.append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	private static String getResponse(HttpEntity entity) 
	{
		String response = "";
		try 
		{
			StringBuffer sb = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(entity.getContent(),"UTF-8");
			char buff[] = new char[1024];
			int cnt;
			while ((cnt = isr.read(buff, 0, 1024 - 1)) > 0) 
			{
				sb.append(buff, 0, cnt);
			}
			response = sb.toString();
			isr.close();
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
		finally 
		{
			try
			{
				entity.consumeContent();
			} 
			catch (IOException ioe) 
			{
			}
		}
		return response;
	}

	public static String callImagePostRestService(String methodName,
			Dictionary<String, Object> params, QBoolean callSuccess) {

		callSuccess.setValue(false);
		String strResponse = "";
		if (httpClient == null) 
		{
			httpClient = new DefaultHttpClient();
		}
		String url;
		HttpPost postMethod = null;

		HttpResponse response = null;
		StringBuffer sb = new StringBuffer(mUrl);
		sb.append("?strJSON=");
		sb.append(methodName);
		// sb.append(".php");
		url = sb.toString();
		postMethod = new HttpPost(url);
		postMethod.setHeader("Content-Type","application/x-www-form-urlencoded");
		Enumeration<String> e = params.keys();
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		while (e.hasMoreElements())
		{
			String key = (e.nextElement());
			nvps.add(new BasicNameValuePair(key, (String) params.get(key).toString()));
		}
		try 
		{
			postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		}
		catch (UnsupportedEncodingException e2) 
		{
			e2.printStackTrace();
		}

		try 
		{
			response = httpClient.execute(postMethod);
			if (response != null) 
			{
				HttpEntity entity = null;
				try 
				{
					entity = response.getEntity();
					@SuppressWarnings("unused")
					long i = entity.getContentLength();
				}
				catch (Exception ex) 
				{
				}
				if (entity != null) 
				{
					strResponse = getResponse(entity);
				}
				else 
				{
				}
			} 
			else 
			{
			}
			callSuccess.setValue(true);
		} 
		catch (Exception ex)
		{
			Log.e("Exception", ex.getMessage());
		}
		finally 
		{
			postMethod.abort();
		}

		if (strResponse == "") {
			// dialog.dismiss();
			return null;
		}

		// dialog.dismiss();
		return strResponse;
	}
}
