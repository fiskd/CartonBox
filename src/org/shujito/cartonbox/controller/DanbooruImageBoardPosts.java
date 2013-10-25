package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.controller.task.DanbooruJsonPostDownloader;
import org.shujito.cartonbox.controller.task.Downloader;
import org.shujito.cartonbox.controller.task.JsonDownloader;
import org.shujito.cartonbox.model.Site;

public class DanbooruImageBoardPosts extends ImageboardPosts
{
	public DanbooruImageBoardPosts(Site site)
	{
		super(site);
	}
	
	@Override
	public Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruJsonPostDownloader(this.buildPostsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnErrorListener(this);
		//downloader.setOnAccessDeniedListener(this);
		//downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
}
