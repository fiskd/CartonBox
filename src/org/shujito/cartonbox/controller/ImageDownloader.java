package org.shujito.cartonbox.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageDownloader extends AsyncTask<Void, Void, Bitmap>
{
	/* listeners */
	
	private OnImageFetchedListener onImageFetchedListener = null;
	
	public OnImageFetchedListener getOnImageFetchedListener()
	{ return this.onImageFetchedListener; }
	public void setOnImageFetchedListener(OnImageFetchedListener l)
	{ this.onImageFetchedListener = l; }
	
	/* fields */
	
	private String url = null;
	private Context context = null;
	private boolean cachingToExternal;
	
	/* getters */
	
	public String getUrl()
	{
		return url;
	}
	
	/* setters */
	
	public void setCachingToExternal(boolean cacheToExternal)
	{
		this.cachingToExternal = cacheToExternal;
	}
	
	/* are's */
	
	public boolean isCachingToExternal()
	{
		return cachingToExternal;
	}
	
	/* constructor */

	public ImageDownloader(Context context, String url)
	{
		this.context = context;
		this.url = url;
	}
	
	/* meth */
	
	@Override
	protected Bitmap doInBackground(Void... params)
	{
		if(this.url == null)
			return null;
		
		int count = 0;
		int secondslash = 0;
		int thirdslash = 0;
		for(int idx = 0; idx < this.url.length(); idx++)
		{
			if(this.url.charAt(idx) == '/')
			{
				count++;
			}
			if(count == 2 && secondslash == 0)
				secondslash = idx;
			if(count == 3 && thirdslash == 0)
				thirdslash = idx;
		}
		secondslash++;
		
		Bitmap bmp = null;
		// domain
		String filename = this.url.substring(secondslash, thirdslash);
		// separator
		filename = filename.concat(" - ");
		// file
		filename = filename.concat(this.url.substring(thirdslash + 1).replace('/', '-'));
		
		try
		{
			/* TODO: make this a preference
			boolean useexternal = PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("use_external", false);
			File dir = this.context.getCacheDir();
			if(this.context.getExternalCacheDir() != null && useexternal)
				dir = this.context.getExternalCacheDir();
			//*/
			
			File file = new File(this.context.getCacheDir(), filename);
			if(file.exists())
			{
				bmp = BitmapFactory.decodeStream(new FileInputStream(file));
			}
			
			if(bmp == null)
			{
				InputStream is = new URL(this.url).openStream();
				bmp = BitmapFactory.decodeStream(is);
				is.close();
				FileOutputStream fos = new FileOutputStream(file);
				bmp.compress(CompressFormat.PNG, 0, fos);
				fos.close();
			}
		}
		catch(Exception ex)
		{
			if(ex != null && ex.getMessage() != null)
				Logger.e("ImageDownloader::doInBackground", ex.getMessage());
			else
				Logger.e("ImageDownloader::doInBackground", "whatever did happen", ex);
		}
		
		return bmp;
	}
	
	@Override
	protected void onPostExecute(Bitmap result)
	{
		if(this.onImageFetchedListener != null)
			this.onImageFetchedListener.onImageFetched(result);
		
		//Logger.i("ImageDownloader::onPostExecute", String.format("Download for %s completed", this.url));
	}
}
