package org.shujito.cartonbox;

import java.util.List;

import org.shujito.cartonbox.controller.ImageboardApis;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.pm.PackageInfo;
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
		
		// we got an update oh no!
		// XXX: should move elsewhere in the case this becomes big
		if(this.detectUpdate())
		{
			int version = this.detectVersionCode();
			// until version 4 had yandere on it, clear all to remove it
			// (custom sites are not available yet here so this shouldn't bother)
			if(version <= 4)
			{
				// get sites!
				SitesDB sitesdb = new SitesDB(this);
				List<Site> sites = sitesdb.getAll();
				// update!
				for(Site site : sites)
				{
					sitesdb.delete(site);
				}
				Preferences.defaultSites();
				sites = sitesdb.getAll();
				sites = sitesdb.getAll();
			}
			CartonBox.getInstance().finishUpdate();
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
	
	/**
	 * Gets the previous stored version
	 * @return previous install version
	 */
	public int previousVersionCode()
	{
		return Preferences.getInt(R.string.pref_previous_version_number);
	}
	
	/**
	 * Gets the current version code
	 * @return current version
	 */
	public int detectVersionCode()
	{
		int version = 0;
		try
		{
			PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			version = info.versionCode;
		}
		catch(Exception ex)
		{
		}
		return version;
	}
	
	/**
	 * Use this to detect whether CartonBox has been updated
	 * @return true if current version code is greater than the previous
	 * version code
	 */
	public boolean detectUpdate()
	{
		int storedVersion = this.previousVersionCode();
		int currentVersion = this.detectVersionCode();
		
		return currentVersion > storedVersion;
	}
	
	/**
	 * Call this when there's no need anymore to perform update changes
	 * or detect the updated application
	 */
	public void finishUpdate()
	{
		int currentVersion = this.detectVersionCode();
		Preferences.setInt(R.string.pref_previous_version_number, currentVersion);
	}
}
