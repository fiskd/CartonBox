package org.shujito.cartonbox;

import org.shujito.cartonbox.controller.ImageboardApis;
import org.shujito.cartonbox.utils.Preferences;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.provider.Settings;

public class CartonBox extends Application
{
	// application instance, we can access the context everywhere with this
	private static CartonBox instance = null;
	
	public static CartonBox getInstance()
	{
		return instance;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		
		//Logger.i("CartonBox::onCreate", String.format("Total Memory: %s", Formatters.humanReadableByteCount(Runtime.getRuntime().maxMemory())));
		
		if(Preferences.isFirstRun())
		{
			Preferences.init();
		}
		
		// detect adb
		int adb = 0;
		
		// gotta love legacy support and backwards compatibility
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			adb = Settings.Secure.getInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
		else
			adb = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
		
		// enable or disable
		if(adb == 1)
		{
			Logger.i("CartonBox::onCreate", "adb detected, logs will be displayed...");
			Logger.setLoggingEnabled(true);
		}
		else
		{
			Logger.i("CartonBox::onCreate", "adb not detected, logs will not be displayed...");
			Logger.setLoggingEnabled(false);
		}
	}
	
	private ImageboardApis apis = null;
	
	public ImageboardApis getApis()
	{
		return apis;
	}
	public void setApis(ImageboardApis apis)
	{
		this.apis = apis;
	}
}
