package org.shujito.cartonbox.utils;

import org.shujito.cartonbox.controller.Downloader;
import org.shujito.cartonbox.utils.io.ClearDirectoryTask;
import org.shujito.cartonbox.utils.io.DirectorySizeTask;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ConcurrentTask
{
	// I tried this:
	// http://www.jayway.com/2012/11/28/is-androids-asynctask-executing-tasks-serially-or-concurrently/
	// and it didn't work, idk why
	// anyway, this thing is a workaround, a rather ugly one but it works,
	// and I don't have to look at it every time (:
	
	public static void execute(ClearDirectoryTask task)
	{
		// the tasking thing
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}
	
	public static void execute(DirectorySizeTask task)
	{
		// the tasking thing
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}
	
	public static void execute(Downloader<?> task)
	{
		// the tasking thing
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}
	
	public static void execute(ImageDownloader task)
	{
		// the tasking thing
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}
}
