package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.Site;

public class DanbooruOldImageBoardPosts extends ImageboardPosts
{
	public DanbooruOldImageBoardPosts(Site site)
	{
		super(site);
	}
	
	@Override
	public Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruOldJsonPostDownloader(this.buildPostsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnErrorListener(this);
		//downloader.setOnAccessDeniedListener(this);
		//downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
}
