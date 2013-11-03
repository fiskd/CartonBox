package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.controller.task.Downloader;
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
		return null;
	}
}
