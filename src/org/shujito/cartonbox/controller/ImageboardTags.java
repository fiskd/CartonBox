package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.JsonParser;

/* tags logic */
// TODO: figure out
public class ImageboardTags extends Imageboard implements OnResponseReceivedListener
{
	public ImageboardTags(Site site)
	{
		super(site);
	}
	
	@Override
	protected Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruJsonPostDownloader(null);
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnAccessDeniedListener(this);
		downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
	
	@Override
	public void clear()
	{
	}
	
	@Override
	public void request()
	{
		if(!this.working)
		{
			
		}
	}
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		
	}
}
