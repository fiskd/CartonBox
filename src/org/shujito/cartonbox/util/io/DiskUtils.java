package org.shujito.cartonbox.util.io;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.util.Formatters;

import android.content.Context;
import android.preference.PreferenceManager;

public class DiskUtils
{
	public static File getCacheDirectory(Context context)
	{
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_general_cacheexternal_key), false))
		{
			// TODO: handle availability of external cache on the preference activity
			File cacheDir = context.getExternalCacheDir();
			if(cacheDir == null)
			{
				cacheDir = context.getCacheDir();
			}
			return cacheDir;
		}
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
		long unsortedFirstLastModification = files[0].lastModified();
		long unsortedLastLastModification = files[files.length - 1].lastModified();
		// sort files
		Arrays.sort(files, new Comparator<File>()
		{
			@Override
			public int compare(File left, File right)
			{
				long lastLeft = left.lastModified();
				long lastRight = right.lastModified();
				long difference = lastLeft - lastRight;
				return (int)difference;
			}
		});
		long sortedFirstLastModification = files[0].lastModified();
		long sortedLastLastModification = files[files.length - 1].lastModified();

		Logger.i("DiskUtils::purgeDirectory", String.format("First file, unsorted: %s", new Date(unsortedFirstLastModification).toString()));
		Logger.i("DiskUtils::purgeDirectory", String.format("Last file, unsorted: %s", new Date(unsortedLastLastModification).toString()));
		Logger.i("DiskUtils::purgeDirectory", String.format("First file, sorted: %s", new Date(sortedFirstLastModification).toString()));
		Logger.i("DiskUtils::purgeDirectory", String.format("Last file, sorted: %s", new Date(sortedLastLastModification).toString()));
		
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
