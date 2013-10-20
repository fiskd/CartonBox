package org.shujito.cartonbox.utils.io;

import org.shujito.cartonbox.utils.io.listeners.OnDirectoryPurgedListener;

import android.os.AsyncTask;

public class PurgeCacheTask extends AsyncTask<Void, Integer, Integer>
{
	OnDirectoryPurgedListener onDirectoryPurgedListener = null;
	
	public OnDirectoryPurgedListener getOnDirectoryPurgedListener()
	{ return this.onDirectoryPurgedListener; }
	public void setOnDirectoryPurgedListener(OnDirectoryPurgedListener l)
	{ this.onDirectoryPurgedListener = l; }
	
	public PurgeCacheTask()
	{
		
	}
	
	@Override
	protected Integer doInBackground(Void... params)
	{
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result)
	{
		if(this.onDirectoryPurgedListener != null)
			this.onDirectoryPurgedListener.onDirectoryPurged();
	}
}
