package org.shujito.cartonbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences
{
	//public static final String <prefname> = "org.shujito.cartonbox.<prefname>";
	public static final String SITE_USERNAME = "org.shujito.cartonbox.SITE_USERNAME";
	public static final String SITE_PASSWORD = "org.shujito.cartonbox.SITE_PASSWORD";
	
	public static boolean getBool(int id)
	{
		return getBool(id, false);
	}
	
	public static boolean getBool(int id, boolean def)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return globalPrefs.getBoolean(context.getString(id), def);
	}
	
	public static int getInt(int id)
	{
		return getInt(id, 0);
	}
	
	public static int getInt(int id, int def)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return globalPrefs.getInt(context.getString(id), def);
	}
	
	public static String getString(int id)
	{
		return getString(id, null);
	}
	
	public static String getString(int id, String def)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return globalPrefs.getString(context.getString(id), def);
	}
	
	public static void setBool(int id, boolean value)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		globalPrefs
			// start editing
			.edit()
			// collocate
			.putBoolean(context.getString(id), value)
			// submit changes
			.commit();
	}
	
	public static void setInt(int id, int value)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		globalPrefs
			// start editing
			.edit()
			// collocate
			.putInt(context.getString(id), value)
			// submit changes
			.commit();
	}
	
	public static void setString(int id, String value)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		globalPrefs
			// start editing
			.edit()
			// collocate
			.putString(context.getString(id), value)
			// submit changes
			.commit();
	}
}
