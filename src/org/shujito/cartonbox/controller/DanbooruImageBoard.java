package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.Site;

public class DanbooruImageBoard extends ImageboardPosts
{
	/* Fields */
	
	/* Constructor */
	public DanbooruImageBoard(Site site)
	{
		super(site);
	}
	
	/* Setters */
	
	/* Meth */
	
	@Override
	protected Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruJsonPostDownloader(this.buildPostsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnAccessDeniedListener(this);
		downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
}
