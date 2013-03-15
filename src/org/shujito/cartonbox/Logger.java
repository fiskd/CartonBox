package org.shujito.cartonbox;

import android.util.Log;

public class Logger
{
	static boolean loggingEnabled = true;
	
	public static boolean isLoggingEnabled()
	{
		return loggingEnabled;
	}
	
	public static void setLoggingEnabled(boolean b)
	{
		loggingEnabled = b;
	}
	
	public static int d(String tag, String msg)
	{
		if(loggingEnabled)
			return Log.d(tag, msg);
		return 0;
	}
	
	public static int d(String tag, String msg, Throwable tr)
	{
		if(loggingEnabled)
			return Log.d(tag, msg, tr);
		return 0;
	}
	
	public static int e(String tag, String msg)
	{
		if(loggingEnabled)
			return Log.e(tag, msg);
		return 0;
	}
	
	public static int e(String tag, String msg, Throwable tr)
	{
		if(loggingEnabled)
			return Log.e(tag, msg, tr);
		return 0;
	}
	
	public static String getStackTraceString(Throwable tr)
	{
		if(loggingEnabled)
			return Log.getStackTraceString(tr);
		return null;
	}
	
	public static int i(String tag, String msg)
	{
		if(loggingEnabled)
			return Log.i(tag, msg);
		return 0;
	}
	
	public static int i(String tag, String msg, Throwable tr)
	{
		if(loggingEnabled)
			return Log.i(tag, msg, tr);
		return 0;
	}
	
	public static boolean isLoggable(String tag, int level)
	{
		if(loggingEnabled)
			return Log.isLoggable(tag, level);
		return false;
	}
	
	public static int println(int priority, String tag, String msg)
	{
		if(loggingEnabled)
			return Log.println(priority, tag, msg);
		return 0;
	}
	
	public static int v(String tag, String msg)
	{
		if(loggingEnabled)
			return Log.v(tag, msg);
		return 0;
	}
	
	public static int v(String tag, String msg, Throwable tr)
	{
		if(loggingEnabled)
			return Log.v(tag, msg, tr);
		return 0;
	}
	
	public static int w(String tag, Throwable tr)
	{
		if(loggingEnabled)
			return Log.w(tag, tr);
		return 0;
	}
	
	public static int w(String tag, String msg, Throwable tr)
	{
		if(loggingEnabled)
			return Log.w(tag, msg, tr);
		return 0;
	}
	
	public static int w(String tag, String msg)
	{
		if(loggingEnabled)
			return Log.w(tag, msg);
		return 0;
	}
	
	public static int wtf(String tag, Throwable tr)
	{
		if(loggingEnabled)
			return Log.wtf(tag, tr);
		return 0;
	}
	
	public static int wtf(String tag, String msg)
	{
		if(loggingEnabled)
			return Log.wtf(tag, msg);
		return 0;
	}
	
	public static int wtf(String tag, String msg, Throwable tr)
	{
		if(loggingEnabled)
			return Log.wtf(tag, msg, tr);
		return 0;
	}
}
