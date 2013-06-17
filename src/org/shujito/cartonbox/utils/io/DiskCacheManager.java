package org.shujito.cartonbox.utils.io;

import java.io.File;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.utils.ConcurrentTask;

import android.content.Context;
import android.preference.PreferenceManager;

public class DiskCacheManager
{
	private DiskCacheManager()
	{
		
	}
	
	public static File getCacheDirectory(Context context)
	{
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_general_cacheexternal), false))
			return context.getExternalCacheDir();
		else
			return context.getCacheDir();
	}
	
	public static void CleanUp(Context context)
	{
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_general_clearcacheonexit_key), false))
		{
			ClearDirectoryTask clearTask = new ClearDirectoryTask(getCacheDirectory(context));
			ConcurrentTask.execute(clearTask);
		}
	}
}
