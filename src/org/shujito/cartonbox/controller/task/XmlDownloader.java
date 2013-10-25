package org.shujito.cartonbox.controller.task;

import java.io.InputStream;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnErrorListener;
import org.shujito.cartonbox.controller.task.listener.OnXmlResponseReceivedListener;
import org.shujito.cartonbox.model.parser.XmlParser;

public abstract class XmlDownloader extends Downloader<XmlParser<?>>
{
	/* Listeners */
	private OnErrorListener onErrorListener = null;
	private OnXmlResponseReceivedListener onXmlResponseReceivedListener = null;
	
	public OnErrorListener getOnErrorListener()
	{ return onErrorListener; }
	public OnXmlResponseReceivedListener getOnResponseReceivedListener()
	{ return this.onXmlResponseReceivedListener; }
	
	public void setOnErrorListener(OnErrorListener l)
	{ this.onErrorListener = l; }
	public void setOnResponseReceivedListener(OnXmlResponseReceivedListener l)
	{ this.onXmlResponseReceivedListener = l; }
	
	public XmlDownloader(String url)
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
				Logger.wtf("XmlDownloader::doInBackground", ex2.getMessage(), ex2);
			}
		}
		
		// return it, we don't care
		return xp;
	}
	
	public abstract XmlParser<?> parse(InputStream is) throws Exception;;
	public abstract XmlParser<?> parseError(InputStream is) throws Exception;;
	
	@Override
	protected void onRequestSuccessful(int code, XmlParser<?> result)
	{
		if(this.onXmlResponseReceivedListener != null)
			this.onXmlResponseReceivedListener.onResponseReceived(result);
	}
	
	@Override
	protected void onRequestFailed(int code, String message)
	{
		if(this.onErrorListener != null)
			this.onErrorListener.onError(code, message);
	}
}
