package org.shujito.cartonbox.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnDownloadProgressListener;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageDownloader extends AsyncTask<Void, Float, Bitmap>
{
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
	private boolean cachingToExternal;
	private boolean alreadyExecuted;
	
	int width, height;
	
	/* constructor */
	
	public ImageDownloader(Context context, String url)
	{
		this.context = context;
		this.url = url;
	}
	
	/* getters */
	
	public String getUrl()
	{
		return this.url;
	}
	
	/* setters */
	
	public void setCachingToExternal(boolean cacheToExternal)
	{
		this.cachingToExternal = cacheToExternal;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/* are's */
	
	public boolean isCachingToExternal()
	{
		return this.cachingToExternal;
	}
	
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
			// file to open or save
			File file = new File(this.context.getCacheDir(), filename);
			if(file.exists())
			{
				// there's file, load and that's all for today
				InputStream input = new _FilterInputStream(new FileInputStream(file));
				bmp = BitmapFactory.decodeStream(input);
				//input.close();
			}
			else
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
				
				// generate random filename
				//SecureRandom serandom = new SecureRandom();
				//String randomFilename = new BigInteger(64, serandom).toString();
				// and a temp file
				//File tempFile = File.createTempFile(randomFilename, null, this.context.getCacheDir());
				OutputStream output = null;
				try
				{
					// create stream from file
					output = new FileOutputStream(file);
					// stuff we need for the buffer
					byte[] data = new byte[8192];
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
						output.close();
				}
				
				InputStream imageStream = new _FilterInputStream(new FileInputStream(file));
				bmp = BitmapFactory.decodeStream(imageStream);
				//imageStream.close();
			}
		}
		catch(OutOfMemoryError ex)
		{
			Logger.e("ImageDownloader::doInBackground", ex.toString(), ex);
			System.gc();
		}
		catch(Exception ex)
		{
			if(ex != null && ex.getMessage() != null)
				Logger.e("ImageDownloader::doInBackground", ex.getMessage(), ex);
			else
				Logger.e("ImageDownloader::doInBackground", "whatever did happen", ex);
		}
		
		return bmp;
	}
	
	@Override
	protected void onProgressUpdate(Float... values)
	{
		if(this.onDownloadProgressListener != null)
		{
			if(values != null && values.length > 0)
			{
				float progress = values[0];
				this.onDownloadProgressListener.onDownloadProgress(progress);
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
	
	// why: http://code.google.com/p/android/issues/detail?id=6066
	class _FilterInputStream extends FilterInputStream
	{
		public _FilterInputStream(InputStream is)
		{
			super(is);
		}
		
		@Override
		public long skip(long n) throws IOException
		{
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n)
			{
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L)
				{
					int _byte = read();
					if (_byte < 0)
						break;  // we reached EOF
					else
						bytesSkipped = 1; // we read one byte
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
