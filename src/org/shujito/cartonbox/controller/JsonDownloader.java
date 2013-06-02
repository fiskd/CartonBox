package org.shujito.cartonbox.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.shujito.cartonbox.controller.listeners.OnAccessDeniedListener;
import org.shujito.cartonbox.controller.listeners.OnInternalServerErrorListener;
import org.shujito.cartonbox.controller.listeners.OnPageNotFoundListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.parser.DanbooruJsonResponseParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public abstract class JsonDownloader extends Downloader<JsonParser<?>>
{
	/* Listeners */
	private OnAccessDeniedListener onAccessDeniedListener = null;
	private OnPageNotFoundListener onPageNotFoundListener = null;
	private OnInternalServerErrorListener onInternalServerErrorListener = null;
	private OnResponseReceivedListener onResponseReceivedListener = null;
	
	public OnAccessDeniedListener getOnAccessDeniedListener()
	{ return this.onAccessDeniedListener; }
	public OnPageNotFoundListener getOnPageNotFoundListener()
	{ return this.onPageNotFoundListener; }
	public OnInternalServerErrorListener getOnInternalServerErrorListener()
	{ return this.onInternalServerErrorListener; }
	public OnResponseReceivedListener getOnResponseReceivedListener()
	{ return this.onResponseReceivedListener; }
	
	public void setOnAccessDeniedListener(OnAccessDeniedListener l)
	{ this.onAccessDeniedListener = l; }
	public void setOnPageNotFoundListener(OnPageNotFoundListener l)
	{ this.onPageNotFoundListener = l; }
	public void setOnInternalServerErrorListener(OnInternalServerErrorListener l)
	{ this.onInternalServerErrorListener = l; }
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
	protected void onRequestFailed(int code, JsonParser<?> result)
	{
		if(code == HttpURLConnection.HTTP_FORBIDDEN)
			if(this.onAccessDeniedListener != null)
				this.onAccessDeniedListener.onAccessDenied();
		if(code == HttpURLConnection.HTTP_NOT_FOUND)
			if(this.onPageNotFoundListener != null)
				this.onPageNotFoundListener.onPageNotFound();
		if(code >= HttpURLConnection.HTTP_INTERNAL_ERROR)
			if(this.onInternalServerErrorListener != null)
				this.onInternalServerErrorListener.onInternalServerError(result);
	}
}
