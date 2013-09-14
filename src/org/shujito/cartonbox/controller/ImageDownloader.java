package org.shujito.cartonbox.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnDownloadProgressListener;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.utils.ImageUtils;
import org.shujito.cartonbox.utils.io.DiskCacheManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ImageDownloader extends AsyncTask<Void, Float, Bitmap>
{
	/* static */
	static final int BUFFER = 8192;
	
	/* listeners */
	
	private OnImageFetchedListener onImageFetchedListener = null;
	private OnDownloadProgressListener onDownloadProgressListener = null;
	
	public OnImageFetchedListener getOnImageFetchedListener()
	{ return this.onImageFetchedListener; }
	public OnDownloadProgressListener getOnDownloadProgressListener()
	{ return this.onDownloadProgressListener; }
	
	public void setOnImageFetchedListener(OnImageFetchedListener l)
	{ this.onImageFetchedListener = l; }
	public void setOnDownloadProgressListener(OnDownloadProgressListener l)
	{ this.onDownloadProgressListener = l; }
	
	/* fields */
	
	private String url = null;
	private Context context = null;
	private boolean alreadyExecuted;
	
	int width, height;
	
	/* constructor */
	
	public ImageDownloader(Context context, String url)
	{
		this.context = context;
		this.url = url;
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
		boolean bCorrupted = false;
		
		if(this.url == null)
			return null;
		
		int slashCount = 0;
		int secondslash = 0;
		int thirdslash = 0;
		for(int idx = 0; idx < this.url.length(); idx++)
		{
			if(this.url.charAt(idx) == '/')
			{
				slashCount++;
			}
			if(slashCount == 2 && secondslash == 0)
				secondslash = idx;
			if(slashCount == 3 && thirdslash == 0)
				thirdslash = idx;
		}
		secondslash++;
		
		// bitmap
		Bitmap bmp = null;
		// domain
		String filename = this.url.substring(secondslash, thirdslash);
		// file
		filename = filename.concat(this.url.substring(thirdslash + 1).replace('/', '-'));
		
		try
		{
			// cache dir to write into
			File cacheDir = DiskCacheManager.getCacheDirectory(this.context);
			// file to open or save
			File file = new File(cacheDir, filename);
			if(file.exists())
			{
				// there's file, load and that's all for today
				bmp = ImageUtils.decodeSampledBitmap(file, this.width, this.height);
				//input.close();
			}
			
			if(bmp == null)
			{
				int size = 0;
				// no file, download and then save
				// have an url
				URL url = new URL(this.url);
				// open it
				HttpURLConnection httpurlconn = (HttpURLConnection)url.openConnection();
				// behoimi has hotlinking protection, bypass it with a referer
				httpurlconn.setRequestProperty("Referer", this.url);
				// connect now
				httpurlconn.connect();
				// get stream size
				size = httpurlconn.getContentLength();
				// put the network stream into a buffer
				InputStream input = new BufferedInputStream(httpurlconn.getInputStream());
				
				OutputStream output = null;
				try
				{
					// create stream from file
					output = new FileOutputStream(file);
					// stuff we need for the buffer
					byte[] data = new byte[BUFFER];
					int total = 0;
					int bytesRead;
					// start reading the stream
					while((bytesRead = input.read(data)) > 0)
					{
						if(this.isCancelled())
						{
							// PANIC!
							return null;
						}
						total += bytesRead;
						// progress available only when streamsize is available
						if(size > 0)
							this.publishProgress( (float)total / size );
						// writting...
						output.write(data, 0, bytesRead);
					}
				}
				finally
				{
					if(output != null)
					{
						output.flush();
						output.close();
					}
				}
				
				//InputStream imageStream = new FileInputStream(file);
				//bmp = BitmapFactory.decodeStream(imageStream);
				bmp = ImageUtils.decodeSampledBitmap(file, this.width, this.height);
				//imageStream.close();
			}
		}
		catch(OutOfMemoryError ex)
		{
			Logger.e("ImageDownloader::doInBackground", ex.toString(), ex);
			System.gc();
			bCorrupted = true;
		}
		catch(Exception ex)
		{
			bCorrupted = true;
			if(ex != null && ex.getMessage() != null)
				Logger.e("ImageDownloader::doInBackground", ex.getMessage(), ex);
			else
				Logger.e("ImageDownloader::doInBackground", "whatever did happen", ex);
		}
		
		if(!bCorrupted)
			return bmp;
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Float... values)
	{
		if(this.onDownloadProgressListener != null)
		{
			if(values != null && values.length > 0)
			{
				float progress = values[0];
				// whoops! 101%?
				this.onDownloadProgressListener.onDownloadProgress(Math.min(progress, 1f));
			}
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
