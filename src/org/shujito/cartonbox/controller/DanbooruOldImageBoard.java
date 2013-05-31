package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.Site;

public class DanbooruOldImageBoard extends ImageboardPosts
{
	public DanbooruOldImageBoard(Site site)
	{
		super(site);
	}
	
	@Override
	protected Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruOldJsonPostDownloader(this.buildPostsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnAccessDeniedListener(this);
		downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
}
