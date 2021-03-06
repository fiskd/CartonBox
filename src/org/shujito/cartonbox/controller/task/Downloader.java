package org.shujito.cartonbox.controller.task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;

import org.shujito.cartonbox.Logger;

import android.os.AsyncTask;

public abstract class Downloader<T> extends AsyncTask<Void, Float, T>
{
	/* Fields */
	
	private String message = null;
	private int code = 0;
	private String url = null;
	private URI uri = null;
	
	/* Constructor */
	
	public Downloader(String url)
	{
		this.url = url;
	}
	
	/* getters */
	
	public String getUrl()
	{
		return url;
	}
	
	/* meth */
	
	@Override
	protected T doInBackground(Void... params)
	{
		URL url = null;
		HttpURLConnection http = null;
		//HttpURLConnection.setFollowRedirects(true);
		T t = null;
		
		try
		{
			Logger.i("Downloader::doInBackground", this.url);
			if(this.url != null)
				url = new URL(this.url);
			else if(this.uri != null)
				url = this.uri.toURL();
			else
				return null;
			
			http = (HttpURLConnection)url.openConnection();
			// it it could not read within ten seconds then it will not
			http.setReadTimeout(10000);
			//http.setInstanceFollowRedirects(true);
			http.connect();
			// TODO: handle better, http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
			this.code = http.getResponseCode();
			this.message = http.getResponseMessage();
			
			//Map<String, List<String>> headers = http.getHeaderFields();
			
			while(this.code == HttpURLConnection.HTTP_MOVED_PERM)
			{
				// where now?
				String newLocation = http.getHeaderField("Location");
				// disconnect
				http.disconnect();
				// new url
				url = new URL(newLocation);
				// reconnect again
				http = (HttpURLConnection)url.openConnection();
				// what code do we have now?
				this.code = http.getResponseCode();
				this.message = http.getResponseMessage();
			}
			
			// http://www-01.ibm.com/support/docview.wss?uid=swg21249300
			InputStream is = null;
			if(this.code >= HttpURLConnection.HTTP_BAD_REQUEST)
				is = http.getErrorStream();
			//if(this.code >= HttpURLConnection.HTTP_MULT_CHOICE)
			else
				is = http.getInputStream();
			
			t = this.doInBackground(is);
		}
		catch(SocketTimeoutException ex)
		{
			this.message = "Connection timed out";
			this.code = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
		}
		catch(Exception ex)
		{
			if(ex != null)
			{
				Logger.e("Downloader::doInBackground", ex.toString());
				this.message = ex.getMessage();
				this.code = HttpURLConnection.HTTP_CONFLICT;
			}
		}
		finally
		{
			if(http != null)
				http.disconnect();
			http = null;
		}
		
		return t;
	}
	
	@Override
	protected void onPostExecute(T result)
	{
		if(this.code >= HttpURLConnection.HTTP_BAD_REQUEST || this.code == 0)
		{
			this.onRequestFailed(this.code, this.message);
			if(this.message != null)
				Logger.e("Downloader::onPostExecute", this.message);
			else
				Logger.e("Downloader::onPostExecute", "something wrong happened");
		}
		else if(this.code >= HttpURLConnection.HTTP_OK)
		{
			this.onRequestSuccessful(this.code, result);
			Logger.i("Downloader::onPostExecute", String.format("Download for %s completed", this.url));
		}
		
	}
	
	/* abstract meth */
	
	protected abstract T doInBackground(InputStream is) throws Exception;
	protected abstract void onRequestSuccessful(int code, T result);
	protected abstract void onRequestFailed(int code, String message);
}
