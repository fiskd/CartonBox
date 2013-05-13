package org.shujito.cartonbox.utils.io;

import java.io.File;

import org.shujito.cartonbox.utils.io.listeners.DirectorySizeCallback;
import org.shujito.cartonbox.utils.io.listeners.OnDiskTaskProgressListener;

import android.os.AsyncTask;

public class DirectorySizeTask extends AsyncTask<Void, Integer, Long>
{
	/* listeners */
	OnDiskTaskProgressListener onDiskTaskProgressListener = null;
	DirectorySizeCallback directorySizeCallback = null;
	
	public void setOnDiskTaskProgress(OnDiskTaskProgressListener l)
	{ this.onDiskTaskProgressListener = l; }
	public void setDirectorySizeCallback(DirectorySizeCallback l)
	{ this.directorySizeCallback = l; }
	
	public OnDiskTaskProgressListener getOnDiskTaskProgress()
	{ return this.onDiskTaskProgressListener; }
	public DirectorySizeCallback getDirectorySizeCallback()
	{ return this.directorySizeCallback; }
	
	File file = null;
	
	public DirectorySizeTask(File file)
	{
		this.file = file;
	}
	
	@Override
	protected Long doInBackground(Void... params)
	{
		File[] files = this.file.listFiles();
		long size = 0;
		int numFiles = files.length;
		
		for(int idx = 0; idx < numFiles; idx++)
		{
			File f = files[idx];
			if(f.isFile())
			{
				size += f.length();
				this.publishProgress(numFiles, idx);
			}
		}
		
		return size;
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
	protected void onPostExecute(Long result)
	{
		if(this.directorySizeCallback != null)
			this.directorySizeCallback.directorySize(result);
	}
}