package org.shujito.cartonbox.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class ___JsonDownloader extends AsyncTask<Void, Void, JsonPostParser>
{
	public interface OnAccessDeniedListener
	{ public void onAccessDenied(); }
	public interface OnResponseReceivedListener
	{ public void onResponseReceived(JsonPostParser jarr); }
	public interface OnPageNotFoundListener
	{ public void onPageNotFound(); }
	public interface OnErrorListener
	{ public void onError(int errCode, String message); }
	
	OnAccessDeniedListener onAccessDeniedListener = null;
	OnResponseReceivedListener onResponseReceivedListener = null;
	OnPageNotFoundListener onPageNotFoundListener = null;
	OnErrorListener onErrorListener = null;
	
	public OnAccessDeniedListener getOnAccessDeniedListener()
	{ return this.onAccessDeniedListener; }
	public OnResponseReceivedListener getOnResponseReceivedListener()
	{ return this.onResponseReceivedListener; }
	public OnPageNotFoundListener getOnPageNotFoundListener()
	{ return this.onPageNotFoundListener; }
	public OnErrorListener getOnErrorListener()
	{ return this.onErrorListener; }
	
	public void setOnAccessDeniedListener(OnAccessDeniedListener l)
	{ this.onAccessDeniedListener = l; }
	public void setOnResponseReceivedListener(OnResponseReceivedListener l)
	{ this.onResponseReceivedListener = l; }
	public void setOnPageNotFoundListener(OnPageNotFoundListener l)
	{ this.onPageNotFoundListener = l; }
	public void setOnErrorListener(OnErrorListener l)
	{ this.onErrorListener = l; }
	
	URI uri = null;
	String url = null;
	int code = 0;
	String message = null;
	
	public ___JsonDownloader(URI uri)
	{
		this.uri = uri;
	}
	
	public ___JsonDownloader(String url)
	{
		this.url = url;
	}
	
	@Override
	protected JsonPostParser doInBackground(Void... params)
	{
		// yande.re/
		//String baseUrl = String.format(API_POSTS_JSON, this.msApiUrl);
		URL url = null;
		JsonPostParser jsonparser = null;
		HttpURLConnection http = null;
		
		try
		{
			if(this.url != null)
				url = new URL(this.url); // api url
			else if(this.uri != null)
				url = this.uri.toURL();
			else
				return null;
			
			http = (HttpURLConnection) url.openConnection();
			// it it could not read within ten seconds then it will not
			http.setReadTimeout(10000);
			http.connect();
			this.code = http.getResponseCode();
			
			//Map<String, List<String>> headers = http.getHeaderFields();
			
			// http://www-01.ibm.com/support/docview.wss?uid=swg21249300
			InputStream is = null;
			if(this.code >= 400)
				is = http.getErrorStream();
			else
				is = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String l = null;
			StringBuilder sb = new StringBuilder();
			while((l = br.readLine()) != null)
			{
				sb.append(l);
				//sb.append('\n');
			}
			
			jsonparser = new JsonPostParser(sb.toString());
		}
		catch(Exception ex)
		{
			if(ex != null)
			{
				Log.e("ImageboardApi::requestPosts", ex.toString());
				this.message = ex.getMessage();
			}
		}
		finally
		{
			if(http != null)
				http.disconnect();
			http = null;
		}
		
		return jsonparser;
	}
	
	@Override
	protected void onPostExecute(JsonPostParser result)
	{
		if(this.code == 200)
			if(this.onResponseReceivedListener != null)
				this.onResponseReceivedListener.onResponseReceived(result);
		if(this.code == 403)
			if(this.onAccessDeniedListener != null)
				this.onAccessDeniedListener.onAccessDenied();
		if(this.code == 404)
			if(this.onPageNotFoundListener != null)
				this.onPageNotFoundListener.onPageNotFound();
		if(this.code > 400 || this.code < 100)
			if(this.onErrorListener != null)
				this.onErrorListener.onError(this.code, this.message);
	}
}