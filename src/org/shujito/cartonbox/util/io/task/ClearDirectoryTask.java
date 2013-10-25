package org.shujito.cartonbox.util.io.task;

import java.io.File;

import org.shujito.cartonbox.util.io.listener.OnDirectoryClearedListener;
import org.shujito.cartonbox.util.io.listener.OnDiskTaskProgressListener;

import android.os.AsyncTask;

public class ClearDirectoryTask extends AsyncTask<Void, Integer, Void>
{
	/* listeners */
	OnDiskTaskProgressListener onDiskTaskProgressListener = null;
	OnDirectoryClearedListener onDirectoryClearedListener = null;
	
	public void setOnDiskTaskProgress(OnDiskTaskProgressListener l)
	{ this.onDiskTaskProgressListener = l; }
	public void setOnDirectoryClearedListener(OnDirectoryClearedListener l)
	{ this.onDirectoryClearedListener = l; }
	
	public OnDiskTaskProgressListener getOnDiskTaskProgress()
	{ return this.onDiskTaskProgressListener; }
	public OnDirectoryClearedListener getOnDirectoryClearedListener()
	{ return this.onDirectoryClearedListener; }
	
	File directory = null;
	
	public ClearDirectoryTask(File directory)
	{
		this.directory = directory;
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		File[] files = this.directory.listFiles();
		int deleted = 0;
		int numFiles = files.length;
		
		for(int idx = 0; idx < numFiles; idx++)
		{
			File f = files[idx];
			if(f.isFile())
			{
				if(f.delete())
				{
					deleted++;
					this.publishProgress(numFiles, deleted);
				}
			}
		}
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values)
	{
		if(this.onDiskTaskProgressListener != null)
		{
			int total = values[0];
			int current = values[1];
			
			this.onDiskTaskProgressListener.onDiskTaskProgress(total, current);
		}
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		if(this.onDirectoryClearedListener != null)
			this.onDirectoryClearedListener.onDirectoryCleared();
	}
}