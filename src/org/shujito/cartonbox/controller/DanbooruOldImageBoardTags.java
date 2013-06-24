package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.Site;

public class DanbooruOldImageBoardTags extends ImageboardTags
{
	public static final String API_TAGS_NAME = "name=%s";
	public static final String API_TAGS_TYPE = "type=%s";
	public static final String API_TAGS_ORDER = "order=count";
	
	public DanbooruOldImageBoardTags(Site site)
	{
		super(site);
	}
	
	@Override
	protected Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruOldJsonTagDownloader(this.buildTagsUrl());
		downloader.setOnResponseReceivedListener(this);
		downloader.setOnErrorListener(this);
		//downloader.setOnAccessDeniedListener(this);
		//downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
	
	@Override
	public String buildTagsUrl()
	{
		String remains = super.buildTagsUrl();
		
		StringBuilder url = new StringBuilder(remains);
		url.append("&");
		url.append(String.format(API_TAGS_ORDER, "count"));
		if(this.query != null && this.query.length() > 0)
		{
			url.append("&");
			url.append(String.format(API_TAGS_NAME, this.query));
		}
		
		return url.toString();
	}
}
