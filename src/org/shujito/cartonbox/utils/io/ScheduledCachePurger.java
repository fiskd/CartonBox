package org.shujito.cartonbox.utils.io;

import java.io.File;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.utils.ConcurrentTask;
import org.shujito.cartonbox.utils.io.listeners.DirectorySizeCallback;
import org.shujito.cartonbox.utils.io.listeners.OnDirectoryPurgedListener;
import org.shujito.cartonbox.utils.io.tasks.DirectorySizeTask;
import org.shujito.cartonbox.utils.io.tasks.PurgeCacheTask;

import android.os.Handler;

public class ScheduledCachePurger implements Runnable, DirectorySizeCallback, OnDirectoryPurgedListener
{
	// run every 60 seconds
	static final int DELAY_TIME = 60000;
	
	private Handler handler = null;
	private int preferredSize;
	private int targetSize;
	private boolean stopped;
	
	public ScheduledCachePurger()
	{
		this.handler = new Handler();
		this.handler.post(this);
	}
	
	@Override
	public void run()
	{
		//Logger.i(this.getClass().getSimpleName(), "Tasking...");
		//this.handler.postDelayed(this, 3000);
		// get the maximum cache size from preferences
		String preferredSizeString = Preferences.getString(R.string.pref_general_cachesize_key);
		// it is a string, must be parsed
		this.preferredSize = Integer.valueOf(preferredSizeString);
		if(this.preferredSize > 0)
		{
			// target size will be five megabytes less than the preferred size
			this.targetSize = Math.max(0, (preferredSize - 5) * 1024 * 1024);
			// Megabytes to kilobytes to bytes
			this.preferredSize = preferredSize * 1024 * 1024;
			// get the size of the cache folder
			File cacheDir = DiskUtils.getCacheDirectory(CartonBox.getInstance());
			DirectorySizeTask sizeTask = new DirectorySizeTask(cacheDir);
			sizeTask.setDirectorySizeCallback(this);
			// continues on the next method
			ConcurrentTask.execute(sizeTask);
		}
	}
	
	@Override
	public void directorySize(long size)
	{
		if(this.stopped)
		{
			this.stop();
		}
		else
		{
			// snip
			//long cacheDirectorySize = DiskUtils.getDirectorySize(DiskUtils.getCacheDirectory(CartonBox.getInstance()));
			// surpassed?
			if(size > preferredSize)
			{
				// work
				PurgeCacheTask task = new PurgeCacheTask(targetSize);
				task.setOnDirectoryPurgedListener(this);
				ConcurrentTask.execute(task);
			}
			else
			{
				// not enough, run again
				Logger.i(this.getClass().getSimpleName(), "Purger is tasking!");
				this.handler.postDelayed(this, DELAY_TIME);
			}
		}
	}
	
	@Override
	public void onDirectoryPurged()
	{
		Logger.i(this.getClass().getSimpleName(), "Purge over!");
		// can't rely on already removed callbacks by calling stop() once
		// check and call stop() again
		if(this.stopped)
		{
			this.stop();
		}
		else
		{
			// run this again later
			this.handler.postDelayed(this, DELAY_TIME);
		}
	}
	
	public void stop()
	{
		this.stopped = true;
		this.handler.removeCallbacks(this);
		Logger.i(this.getClass().getSimpleName(), "Stopping purger!");
	}
}
