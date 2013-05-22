package org.shujito.cartonbox.utils;

public class URLEncoder
{
	/**
	 * why you do this
	 * @param s: string to be encoded
	 * @return encoded string
	 */
	public static String encode(String s)
	{
		try
		{ s = java.net.URLEncoder.encode(s, "UTF-8"); }
		catch(Exception e) { }
		return s;
	}
}
