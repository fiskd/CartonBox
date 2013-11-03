package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.controller.task.Downloader;
import org.shujito.cartonbox.controller.task.GelbooruPostsDownloader;
import org.shujito.cartonbox.controller.task.XmlDownloader;
import org.shujito.cartonbox.model.Site;

public class GelbooruImageboardPosts extends ImageboardXmlPosts
{
	public GelbooruImageboardPosts(Site site)
	{
		super(site);
	}
	
	@Override
	public Downloader<?> createDownloader()
	{
		XmlDownloader downloader = new GelbooruPostsDownloader(this.buildPostsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnErrorListener(this);
		return downloader;
	}
}
