/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ak.app.webservices;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;



/**
 * Utility class supporting the Facebook Object.
 * 
 * @author ssoneff@facebook.com
 * 
 */
public final class UtillClass 
{
	/**
	 * Generate the multi-part post body providing the parameters and boundary
	 * string
	 * 
	 * @param parameters
	 *            the parameters need to be posted
	 * @param boundary
	 *            the random string as boundary
	 * @return a string of the post body
	 */
	public static String encodePostBody(Bundle parameters, String boundary) 
	{
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) 
		{
			if (parameters.getByteArray(key) != null) 
			{
				continue;
			}
			sb.append("Content-Disposition: form-data; name=\"" + key+ "\"\r\n\r\n" + parameters.getString(key));
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	public static String encodeUrl(Bundle parameters) 
	{
		if (parameters == null) 
		{
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) 
		{
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "="+ URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	public static Bundle decodeUrl(String s)
	{
		Bundle params = new Bundle();
		if (s != null) 
		{
			String array[] = s.split("&");
			for (String parameter : array) 
			{
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Bundle parseUrl(String url)
	{
		// hack to prevent MalformedURLException
		url = url.replace("fbconnect", "http");
		try 
		{
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} 
		catch (MalformedURLException e) 
		{
			return new Bundle();
		}
	}

	/**
	 * Connect to an HTTP URL and return the response as a string.
	 * 
	 * Note that the HTTP method override is used on non-GET requests. (i.e.
	 * requests are made as "POST" with method specified in the body).
	 * 
	 * @param url
	 *            - the resource to open: must be a welformed URL
	 * @param method
	 *            - the HTTP method to use ("GET", "POST", etc.)
	 * @param params
	 *            - the query parameter for the URL (e.g. access_token=foo)
	 * @return the URL contents as a String
	 * @throws MalformedURLException
	 *             - if the URL format is invalid
	 * @throws IOException
	 *             - if a network problem occurs
	 */

	@SuppressWarnings("deprecation")
	public static String openUrl(String url, String method, Bundle params,Boolean type) throws MalformedURLException, IOException
	{
		// random string as boundary for multi-part http post
		String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
		String endLine = "\r\n";
		@SuppressWarnings("unused")
		boolean methodStatus=true;
		OutputStream os = null;

		if (method.equals("GET"))
		{
			url = url + "?" + encodeUrl(params);
		}

		// Open a HTTP connection to the URL
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		/* conn.setChunkedStreamingMode(1024*4); */

		conn.setRequestProperty("User-Agent", System.getProperties().getProperty("http.agent")+ " FacebookAndroidSDK");
		try 
		{
			if (!method.equals("GET")) 
			{
				Bundle dataparams = new Bundle();
				for (String key : params.keySet())
				{
					if (params.getByteArray(key) != null) 
					{
						dataparams.putByteArray(key, params.getByteArray(key));
					}
					else
					{
					
					}
				}
			
				// use method override
				if (!params.containsKey("method")) 
				{
					params.putString("method", method);
				}

				if (params.containsKey("access_token")) 
				{
					String decoded_token = URLDecoder.decode(params.getString("access_token"));
					params.putString("access_token", decoded_token);
				}

				// Use a post method.
				conn.setRequestMethod("POST");

				if (type) 
				{
					conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + strBoundary);
				}

				// Allow Outputs
				conn.setDoOutput(true);

				// Allow Inputs
				conn.setDoInput(true);
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.connect();
				os = new BufferedOutputStream(conn.getOutputStream());

				if (type) 
				{
					os.write(("--" + strBoundary + endLine).getBytes());
					os.write((encodePostBody(params, strBoundary)).getBytes());
					os.write((endLine + "--" + strBoundary + endLine).getBytes());
				} 
				else
				{
					os.write((params.getString("input")).getBytes());
				}

				if (!dataparams.isEmpty()) 
				{
					for (String key : dataparams.keySet()) 
					{
						os.write(("Content-Disposition: form-data; filename=\""+ key + "\"" + endLine).getBytes());
						os.write(("Content-Type: content/unknown"+ endLine + endLine).getBytes());
						os.write(dataparams.getByteArray(key));
						os.write((endLine + "--" + strBoundary + endLine).getBytes());
						// os.flush();
					}
				}
				os.flush();
				dataparams.clear();

			}
			String response = "";
			try 
			{
				response = read(conn.getInputStream());
			} 
			catch (FileNotFoundException e)
			{
				// Error Stream contains JSON that we can parse to a FB error
				response = read(conn.getErrorStream());
			}
			conn.disconnect();
			return response;
		} 
		catch (OutOfMemoryError e) 
		{
			if (os != null)
				os.flush();
			conn.disconnect();
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static String openUrll(String url, String method, Bundle params,String imageName)
			throws MalformedURLException, IOException {
		// random string as boundary for multi-part http post
		String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
		String endLine = "\r\n";

		OutputStream os = null;

		if (method.equals("GET")) {
			url = url + "?" + encodeUrl(params);
		}

		// Open a HTTP connection to the URL
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		/* conn.setChunkedStreamingMode(1024*4); */

		conn.setRequestProperty("User-Agent", System.getProperties().getProperty("http.agent")+ " FacebookAndroidSDK");
		try 
		{
			if (!method.equals("GET")) 
			{
				Bundle dataparams = new Bundle();
				for (String key : params.keySet())
				{
					if (params.getByteArray(key) != null) 
					{
						dataparams.putByteArray(key, params.getByteArray(key));
					} 
					else 
					{
						dataparams.putString(key, params.getString(key));
					}
				}

				// use method override
				if (!params.containsKey("method"))
				{
					params.putString("method", method);
				}

				if (params.containsKey("access_token")) 
				{
					String decoded_token = URLDecoder.decode(params.getString("access_token"));
					params.putString("access_token", decoded_token);
				}

				// Use a post method.
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + strBoundary);

				// Allow Outputs
				conn.setDoOutput(true);

				// Allow Inputs
				conn.setDoInput(true);
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.connect();
				os = new BufferedOutputStream(conn.getOutputStream());
				os.write(("--" + strBoundary + endLine).getBytes());
				os.write((encodePostBody(params, strBoundary)).getBytes());
				os.write((endLine + "--" + strBoundary + endLine).getBytes());
				
				if (!dataparams.isEmpty()) 
				{
					for (String key : dataparams.keySet()) 
					{
						if(key.equals("userphoto"))
						{
							os.write(("Content-Disposition: form-data; name=\""+  key  +"\"; filename=\""+ imageName + "\"" + endLine).getBytes());
							os.write(("Content-Type: application/octet-stream"+ endLine + endLine).getBytes());
							os.write(dataparams.getByteArray(key));
						}
						else
						{
							os.write(("Content-Disposition: form-data; name=\""+ key + "\"" + endLine).getBytes());
							os.write(("Content-Type: content/unknown"+ endLine + endLine).getBytes());						
							os.write(dataparams.getString(key).getBytes());
						}
						//os.write();
						os.write((endLine + "--" + strBoundary + endLine).getBytes());
						// os.flush();
					}
				}
				os.flush();
				dataparams.clear();
			}
			String response = "";
			try 
			{
				response = read(conn.getInputStream());
			} 
			catch (FileNotFoundException e)
			{
				// Error Stream contains JSON that we can parse to a FB error
				response = read(conn.getErrorStream());
			}
			conn.disconnect();

			return response;
		} 
		catch (OutOfMemoryError e)
		{
			if (os != null)os.flush();
			conn.disconnect();
			e.printStackTrace();
			return null;
		}
	}

	public static String postData(String url, String method, Bundle params)
	{
		String response = "";
		if (!params.containsKey("method")) 
		{
			params.putString("method", method);
		}

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		 
		try 
		{
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (String key : params.keySet()) 
			{
				if (params.getByteArray(key) != null)
				{
					ByteArrayBody bab = new ByteArrayBody(params.getByteArray(key), params.getString("filename"));
					entity.addPart(key, bab);
				}
				else
				{		
					entity.addPart(key,new StringBody(params.getString(key)));					
				}
			}
			httppost.setEntity(entity);
		  
			// Execute HTTP Post Request
			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			response = httpclient.execute(httppost, responseHandler);

			//response = httpclient.execute(httppost);
		} 
		catch (ClientProtocolException e) 
		{
		}
		catch (IOException e) 
		{
		}	
		return response;
	} 

	private static String read(InputStream in) throws IOException 
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine())
		{
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	public static void clearCookies(Context context) 
	{
		// Edge case: an illegal state exception is thrown if an instance of
		// CookieSyncManager has not be created. CookieSyncManager is normally
		// created by a WebKit view, but this might happen if you start the
		// app, restore saved state, and click logout before running a UI
		// dialog in a WebView -- in which case the app crashes
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * Parse a server response into a JSON Object. This is a basic
	 * implementation using org.json.JSONObject representation. More
	 * sophisticated applications may wish to do their own parsing.
	 * 
	 * The parsed JSON is checked for a variety of error fields and a
	 * FacebookException is thrown if an error condition is set, populated with
	 * the error message and error type or code if available.
	 * 
	 * @param response
	 *            - string representation of the response
	 * @return the response as a JSON Object
	 * @throws JSONException
	 *             - if the response is not valid JSON
	 * @throws FacebookError
	 *             - if an error condition is set
	 */
	public static JSONObject parseJson(String response) throws JSONException 
	{
		// Edge case: when sending a POST request to /[post_id]/likes
		// the return value is 'true' or 'false'. Unfortunately
		// these values cause the JSONObject constructor to throw
		// an exception.

		if (response.equals("true")) 
		{
			response = "{value : true}";
		}
		JSONObject json = new JSONObject(response);

		// errors set by the server are not consistent
		// they depend on the method and endpoint
		if (json.has("error")) 
		{
			@SuppressWarnings("unused")
			JSONObject error = json.getJSONObject("error");
		}
		return json;
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 * 
	 * @param context
	 *            Android context in which the dialog should be displayed
	 * @param title
	 *            Alert dialog title
	 * @param text
	 *            Alert dialog message
	 */
	public static void showAlert(Context context, String title, String text) 
	{
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}
}
