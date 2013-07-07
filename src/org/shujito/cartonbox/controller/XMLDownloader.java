package org.shujito.cartonbox.controller;

import java.io.InputStream;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.parser.XmlParser;

public abstract class XMLDownloader extends Downloader<XmlParser<?>>
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
	
	public XMLDownloader(String url)
	{
		super(url);
	}
	
	@Override
	protected XmlParser<?> doInBackground(InputStream is) throws Exception
	{
		// put parser here
		XmlParser<?> xp = null;
		
		try
		{
			// try once
			xp = this.parse(is);
		}
		catch(Exception ex1)
		{
			try
			{
				// fail
				xp = this.parseError(is);
			}
			catch(Exception ex2)
			{
				// egh
				Logger.wtf("XMLDownloader::doInBackground", ex2.getMessage(), ex2);
			}
		}
		
		// return it, we don't care
		return xp;
	}
	
	public abstract XmlParser<?> parse(InputStream is);
	public abstract XmlParser<?> parseError(InputStream is);
	
	@Override
	protected void onRequestSuccessful(int code, XmlParser<?> result)
	{
		//if(this.onResponseReceivedListener != null)
			//this.onResponseReceivedListener.onResponseReceived(result);
	}
	
	@Override
	protected void onRequestFailed(int code, String message)
	{
		if(this.onErrorListener != null)
			this.onErrorListener.onError(code, message);
	}
}
