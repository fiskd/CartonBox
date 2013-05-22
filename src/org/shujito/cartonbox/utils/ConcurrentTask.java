package org.shujito.cartonbox.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class ConcurrentTask
{
	// I tried this:
	// http://www.jayway.com/2012/11/28/is-androids-asynctask-executing-tasks-serially-or-concurrently/
	// and it didn't work, idk why
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void execute(AsyncTask task)
	{
		// the tasking thing
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
		else
		{
			task.execute();
		}
	}
}
