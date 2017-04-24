package com.ak.app.staticmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.app.bookexchange.HomeActivity;
import com.android.app.bookexchange.LoginActivity;
import com.android.app.bookexchange.R;


public class DialogClasses {

	public static void showDialog1(String msg, String ttl,String btn, final Context ctx)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(msg).setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(btn,new DialogInterface.OnClickListener()
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

	public static void showDialog2(String msg, String ttl, String pos, String neg, final Activity ctx) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setIcon(R.drawable.logout);
		builder.setMessage(msg).setCancelable(false).setPositiveButton(pos,new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				SharedPreferences pref;
				pref = ctx.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
	        	SharedPreferences.Editor editor = pref.edit();
	        	
	        	editor.remove("email");
	        	editor.remove("UserId");
	        	editor.remove("password");
			    editor.commit();
			   
				if(HomeActivity.homeActivity!=null)
				{
					HomeActivity.homeActivity.finish();
				}
				
				Intent i=new Intent(ctx, LoginActivity.class);
				ctx.startActivity(i);
				ctx.finish();
				HomeActivity.homeActivity.finish();
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

	public static void showDeactivated(String msg, String ttl, String pos, final Activity ctx) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(msg).setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(pos,new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				SharedPreferences pref;
				pref = ctx.getSharedPreferences("BookExchange", Context.MODE_PRIVATE);
	        	SharedPreferences.Editor editor = pref.edit();
	        	
	        	editor.remove("email");
	        	editor.remove("UserId");
	        	editor.remove("password");
			    editor.commit();
			    
				if(HomeActivity.homeActivity!=null)
				{
					HomeActivity.homeActivity.finish();
				}
				Intent i=new Intent(ctx, LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ctx.startActivity(i);
				ctx.finish();
				dialog.cancel();
			}
		});
		if (ttl != null && !ttl.equals(""))
			builder.setTitle(ttl);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
