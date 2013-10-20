package org.shujito.cartonbox.utils.io.tasks;

import java.io.File;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.utils.io.DiskUtils;
import org.shujito.cartonbox.utils.io.listeners.OnDirectoryPurgedListener;

import android.os.AsyncTask;

public class PurgeCacheTask extends AsyncTask<Void, Integer, Integer>
{
	OnDirectoryPurgedListener onDirectoryPurgedListener = null;
	
	public OnDirectoryPurgedListener getOnDirectoryPurgedListener()
	{ return this.onDirectoryPurgedListener; }
	public void setOnDirectoryPurgedListener(OnDirectoryPurgedListener l)
	{ this.onDirectoryPurgedListener = l; }
	
	long target;
	
	public PurgeCacheTask(long target)
	{
		this.target = target;
	}
	
	@Override
	protected Integer doInBackground(Void... params)
	{
		File cacheDir = DiskUtils.getCacheDirectory(CartonBox.getInstance());
		DiskUtils.purgeDirectory(cacheDir, this.target);
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result)
	{
		if(this.onDirectoryPurgedListener != null)
			this.onDirectoryPurgedListener.onDirectoryPurged();
	}
}
