package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.controller.task.DanbooruJsonTagDownloader;
import org.shujito.cartonbox.controller.task.Downloader;
import org.shujito.cartonbox.controller.task.JsonDownloader;
import org.shujito.cartonbox.model.Site;

public class DanbooruImageBoardTags extends ImageboardTags
{
	public static final String API_TAGS_NAME_MATCHES = "search[name_matches]=%s";
	public static final String API_TAGS_CATEGORY = "search[category]=%s";
	public static final String API_TAGS_SORT = "search[sort]=%s";
	public static final String API_TAGS_HIDE_EMPTY = "search[hide_empty]=%s";
	
	public DanbooruImageBoardTags(Site site)
	{
		super(site);
	}
	
	@Override
	public Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruJsonTagDownloader(this.buildTagsUrl());
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
		url.append(String.format(API_TAGS_SORT, "count"));
		url.append("&");
		url.append(String.format(API_TAGS_HIDE_EMPTY, "yes"));
		if(this.query != null && this.query.length() > 0)
		{
			url.append("&");
			url.append(String.format(API_TAGS_NAME_MATCHES, this.query));
		}
		
		return url.toString();
	}
}
