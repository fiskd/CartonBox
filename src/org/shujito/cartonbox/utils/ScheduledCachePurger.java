package org.shujito.cartonbox.utils;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.utils.io.DiskUtils;
import org.shujito.cartonbox.utils.io.PurgeCacheTask;
import org.shujito.cartonbox.utils.io.listeners.OnDirectoryPurgedListener;

import android.os.Handler;

public class ScheduledCachePurger implements Runnable, OnDirectoryPurgedListener
{
	// runs every 60 seconds
	static final int DELAY_TIME = 10000;
	
	private Handler handler = null;
	
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
		int preferredSize = Integer.valueOf(preferredSizeString);
		// Megabytes to kilobytes to bytes
		preferredSize = preferredSize * 1024 * 1024;
		// get the size of the cache folder
		long cacheDirectorySize = DiskUtils.getDirectorySize(DiskUtils.getCacheDirectory(CartonBox.getInstance()));
		// surpassed?
		if(cacheDirectorySize > preferredSize)
		{
			// work
			PurgeCacheTask task = new PurgeCacheTask();
			task.setOnDirectoryPurgedListener(this);
			ConcurrentTask.execute(task);
		}
		else
		{
			// not enough, run again
			Logger.i(this.getClass().getSimpleName(), "Tasking!");
			this.handler.postDelayed(this, DELAY_TIME);
		}
	}
	
	public void stop()
	{
		this.handler.removeCallbacks(this);
	}
	
	@Override
	public void onDirectoryPurged()
	{
		// run this again later
		Logger.i(this.getClass().getSimpleName(), "Task over!");
		this.handler.postDelayed(this, DELAY_TIME);
	}
}
