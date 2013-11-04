package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.controller.task.Downloader;
import org.shujito.cartonbox.controller.task.GelbooruTagsDownloader;
import org.shujito.cartonbox.controller.task.XmlDownloader;
import org.shujito.cartonbox.model.Site;

public class GelbooruImageboardTags extends ImageboardXmlTags
{
	public GelbooruImageboardTags(Site site)
	{
		super(site);
	}
	
	@Override
	public Downloader<?> createDownloader()
	{
		XmlDownloader downloader = new GelbooruTagsDownloader(this.buildTagsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnErrorListener(this);
		return downloader;
	}
}
