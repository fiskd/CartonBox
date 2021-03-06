package org.shujito.cartonbox.controller.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnErrorListener;
import org.shujito.cartonbox.controller.task.listener.OnJsonResponseReceivedListener;
import org.shujito.cartonbox.model.parser.JsonParser;

public abstract class JsonDownloader extends Downloader<JsonParser<?>>
{
	/* Listeners */
	private OnErrorListener onErrorListener = null;
	private OnJsonResponseReceivedListener onJsonResponseReceivedListener = null;
	
	public OnErrorListener getOnErrorListener()
	{ return onErrorListener; }
	public OnJsonResponseReceivedListener getOnResponseReceivedListener()
	{ return this.onJsonResponseReceivedListener; }
	
	public void setOnErrorListener(OnErrorListener l)
	{ this.onErrorListener = l; }
	public void setOnResponseReceivedListener(OnJsonResponseReceivedListener l)
	{ this.onJsonResponseReceivedListener = l; }
	
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
		catch(Exception ex1)
		{
			// now see if it's a response object (the outer catch shouldn't
			// take care of anything wrong that happens here)
			try
			{
				return parseError(jsonString);
			}
			catch(Exception ex2)
			{
				// what!!
				Logger.wtf("JsonDownloader::doInBackground", ex2.getMessage(), ex2);
			}
		}
		
		return jp;
	}
	
	protected abstract JsonParser<?> parse(String s) throws Exception;
	protected abstract JsonParser<?> parseError(String s) throws Exception;
	
	@Override
	protected void onRequestSuccessful(int code, JsonParser<?> result)
	{
		if(this.onJsonResponseReceivedListener != null)
			this.onJsonResponseReceivedListener.onResponseReceived(result);
	}
	
	@Override
	protected void onRequestFailed(int code, String message)
	{
		if(this.onErrorListener != null)
			this.onErrorListener.onError(code, message);
	}
}
