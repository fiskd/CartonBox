package org.shujito.cartonbox.utils.io;

import java.io.File;

import org.shujito.cartonbox.utils.io.listeners.OnDirectoryClearedListener;
import org.shujito.cartonbox.utils.io.listeners.OnDiskTaskProgressListener;

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
	
	File file = null;
	
	public ClearDirectoryTask(File file)
	{
		this.file = file;
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		File[] files = this.file.listFiles();
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