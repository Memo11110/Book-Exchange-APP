package com.ak.app.staticmethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationMethod {
	
	static Matcher m;
	static String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	static Pattern emailPattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
	static String passwordExpression ="((?=.*\\d)(?=.*[A-Z])(?=.*[0-9]).{8,15})";
	static Pattern passwordPattern=Pattern.compile(passwordExpression);
	
	public static boolean emailValidation(String s)
	{
		if( s == null)
		{
			return false; 
		}
		else
		{
			m = emailPattern.matcher(s);
			return m.matches();
		}
	}
	
	public static boolean passwordValidation(String s)
	{
		if( s == null)
		{
			return false; 
		}
		else
		{
			m = passwordPattern.matcher(s);
			return m.matches();
		}
	}
	
	public static boolean emailValidation2(String s)
	{
		m = emailPattern.matcher(s);
		return m.matches();
	}
}
