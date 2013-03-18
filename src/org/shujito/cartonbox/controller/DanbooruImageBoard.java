package org.shujito.cartonbox.controller;

public class DanbooruImageBoard extends Imageboard
{
	public DanbooruImageBoard(String hostSiteUrl)
	{
		super(hostSiteUrl);
	}
	
	@Override
	protected Downloader<?> createDownloader()
	{
		return null;
	}
}
