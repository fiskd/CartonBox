package org.shujito.cartonbox.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.parser.DanbooruJsonResponseParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public abstract class JsonDownloader extends Downloader<JsonParser<?>>
{
	/* Listeners */
	private OnErrorListener onErrorListener = null;
	private OnResponseReceivedListener onResponseReceivedListener = null;
	
	public OnErrorListener getOnErrorListener()
	{ return onErrorListener; }
	public OnResponseReceivedListener getOnResponseReceivedListener()
	{ return this.onResponseReceivedListener; }
	
	public void setOnErrorListener(OnErrorListener l)
	{ this.onErrorListener = l; }
	public void setOnResponseReceivedListener(OnResponseReceivedListener l)
	{ this.onResponseReceivedListener = l; }
	
	/* Constructor */
	public JsonDownloader(String url)
	{
		super(url);
	}
	
	/* Meth */
	@Override
	protected JsonParser<?> doInBackground(InputStream is) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String l = null;
		StringBuilder sb = new StringBuilder();
		while((l = br.readLine()) != null)
			sb.append(l);
		
		JsonParser<?> jp = null; //this.parse(sb.toString());
		
		String jsonString = sb.toString();
		
		// I don't like to use exceptions on cases like this one
		// but it's safer to validate this way
		try
		{
			// parse first
			jp = this.parse(jsonString);
		}
		catch(Exception ex)
		{
			// now see if it's a response object (the outer catch should take
			// care of anything wrong that happens here)
			jp = new DanbooruJsonResponseParser(jsonString);
		}
		
		return jp;
	}
	
	protected abstract JsonParser<?> parse(String s) throws Exception;
	
	@Override
	protected void onRequestSuccessful(int code, JsonParser<?> result)
	{
		if(this.onResponseReceivedListener != null)
			this.onResponseReceivedListener.onResponseReceived(result);
	}
	
	@Override
	protected void onRequestFailed(int code, String message)
	{
		/*
		if(code == HttpURLConnection.HTTP_FORBIDDEN)
			if(this.onAccessDeniedListener != null)
				this.onAccessDeniedListener.onAccessDenied();
		if(code == HttpURLConnection.HTTP_NOT_FOUND)
			if(this.onPageNotFoundListener != null)
				this.onPageNotFoundListener.onPageNotFound();
		if(code >= HttpURLConnection.HTTP_INTERNAL_ERROR)
			if(this.onInternalServerErrorListener != null)
				this.onInternalServerErrorListener.onInternalServerError(result);
		//*/
		if(this.onErrorListener != null)
		{
			this.onErrorListener.onError(code, message);
		}
	}
}
