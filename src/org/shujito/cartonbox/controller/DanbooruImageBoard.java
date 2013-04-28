package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.Site;

public class DanbooruImageBoard extends Imageboard
{
	/* Static */
	public static final String API_LOGIN = "login=%s";
	public static final String API_PASSWORD_HASH = "password_hash=%s";
	public static final String API_KEY = "api_key=%s";
	public static final String API_LIMIT = "limit=%d";
	public static final String API_PAGE = "page=%d";
	public static final String API_TAGS = "tags=%s";
	
	//public static final String API_POSTS_JSON = "%s/posts.json";
	//public static final String API_POSTS_XML = "%s/posts.xml";
	//public static final String API_POOLS_JSON = "%s/pool/index.json";
	//public static final String API_POOLS_XML = "%s/pool/index.xml";
	
	/* Fields */
	
	/* Constructor */
	public DanbooruImageBoard(Site site)
	{
		super(site);
		
		//this.site = new Site();
		//this.site.setUrl(siteUrl);
	}
	
	/* Setters */
	
	/* Meth */
	
	@Override
	protected Downloader<?> createDownloader()
	{
		JsonDownloader downloader = new DanbooruJsonPostDownloader(this.buildPostsUrl());
		downloader.setOnResponseReceivedListener(this);
		//downloader.setOnErrorListener(this);
		downloader.setOnAccessDeniedListener(this);
		downloader.setOnInternalServerErrorListener(this);
		return downloader;
	}
	
	protected String buildPostsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		//url.append(String.format(API_POSTS_JSON, this.site.getUrl()));
		url.append(String.format(this.site.getPostsApi(), this.site.getUrl()));
		url.append("?");
		
		if(this.username != null)
		{
			url.append(String.format(API_LOGIN, this.username));
			url.append("&");
		}
		if(this.password != null)
		{
			url.append(String.format(API_PASSWORD_HASH, this.password));
			//url.append(String.format(API_KEY, this.password));
			url.append("&");
		}
		
		url.append(String.format(API_PAGE, this.page));
		url.append("&");
		url.append(String.format(API_LIMIT, this.postsPerPage));
		
		if(this.tags != null && this.tags.size() > 0)
		{
			url.append("&");
			url.append(String.format(API_TAGS, this.buildTags()));
		}
		
		return url.toString();
	}
}
