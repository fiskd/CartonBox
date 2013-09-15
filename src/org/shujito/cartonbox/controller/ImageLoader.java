package org.shujito.cartonbox.controller;

import java.io.File;

import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.utils.ImageUtils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ImageLoader extends AsyncTask<Void, Float, Bitmap>
{
	/* static */
	static final int BUFFER = 8192;
	
	/* listeners */
	
	private OnImageFetchedListener onImageFetchedListener = null;
	
	public OnImageFetchedListener getOnImageFetchedListener()
	{ return this.onImageFetchedListener; }
	
	public void setOnImageFetchedListener(OnImageFetchedListener l)
	{ this.onImageFetchedListener = l; }
	
	/* fields */
	
	private String location = null;
	private boolean alreadyExecuted;
	
	int width, height;
	
	/* constructor */
	
	public ImageLoader(String location)
	{
		this.location = location;
		this.width = 256;
		this.height = 256;
	}
	
	/* setters */
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/* are's */
	
	public boolean isAlreadyExecuted()
	{
		return this.alreadyExecuted;
	}
	
	/* meth */
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		this.alreadyExecuted = true;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params)
	{
		try
		{
			File file = new File(this.location);
			Bitmap b = ImageUtils.decodeSampledBitmap(file, this.width, this.height);
			return b;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Bitmap result)
	{
		if(this.onImageFetchedListener != null)
			this.onImageFetchedListener.onImageFetched(result);
		
		//Logger.i("ImageDownloader::onPostExecute", String.format("Download for %s completed", this.url));
	}
}
