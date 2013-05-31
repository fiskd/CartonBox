package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.Site;

public class DanbooruImageBoard extends ImageboardPosts
{
	public DanbooruImageBoard(Site site)
	{
		super(site);
	}
	
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
