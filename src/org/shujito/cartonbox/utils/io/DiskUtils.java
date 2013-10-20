package org.shujito.cartonbox.utils.io;

import java.io.File;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.utils.Formatters;

import android.content.Context;
import android.preference.PreferenceManager;

public class DiskUtils
{
	public static File getCacheDirectory(Context context)
	{
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_general_cacheexternal_key), false))
			return context.getExternalCacheDir();
		else
			return context.getCacheDir();
	}
	
	public static File getCacheFile(Context context, String filename)
	{
		return new File(getCacheDirectory(context), filename);
	}
	
	public synchronized static void purgeDirectory(File directory, long target)
	{
		Logger.i("DiskUtils::purgeDirectory", String.format("purging directory '%s' to match %s", directory.getAbsolutePath(), Formatters.humanReadableByteCount(target)));
		// get files
		File[] files = directory.listFiles();
		// get all files' size
		long dirsize = getDirectorySize(directory);
		// go though files
		for(File file : files)
		{
			long fileSize = file.length();
			// check if it was deleted
			if(file.delete())
			{
				// substract
				dirsize -= fileSize;
				// stop if the size is less than the target size
				if(dirsize < target)
				{
					// yess
					break;
				}
			}
		}
	}
	
	public synchronized static long getDirectorySize(File directory)
	{
		// get files
		File[] files = directory.listFiles();
		// this will be the total size
		long size = 0;
		// go through files
		for(File file : files)
		{
			// check if it is a file
			if(file.isFile())
			{
				// add it to the total size
				size += file.length();
			}
		}
		// the result is here:
		return size;
	}
}
