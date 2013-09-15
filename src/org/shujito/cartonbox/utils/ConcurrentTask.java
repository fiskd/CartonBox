package org.shujito.cartonbox.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ConcurrentTask
{
	// where:
	// http://www.jayway.com/2012/11/28/is-androids-asynctask-executing-tasks-serially-or-concurrently/
	// looke like android (java rather) has issues with generics and varargs,
	// all the tasks I use have void parameters so adding a Void generic arg
	// solves both the type warning and the runtime cast exception
	public static void execute(AsyncTask<Void, ?, ?> task)
	{
		// the tasking thing
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{ task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); }
		else
		{ task.execute(); }
	}
}
