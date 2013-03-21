package org.shujito.cartonbox.controller;

public class Danbooru2ImageBoard extends Imageboard
{
	/* Static */
	public static final String API_DANBOORU_PASSWORD = "choujin-steiner--%s--";
	public static final String API_LOGIN = "login=%s";
	public static final String API_PASSWORD_HASH = "password_hash=%s";
	public static final String API_LIMIT = "limit=%d";
	public static final String API_PAGE = "page=%d";
	public static final String API_TAGS = "tags=%s";
	
	public static final String API_POSTS_JSON = "%s/posts.json?";
	//public static final String API_POSTS_XML = "%s/posts.xml?";
	//public static final String API_POOLS_JSON = "%s/pool/index.json?";
	//public static final String API_POOLS_XML = "%s/pool/index.xml?";
	
	/* Fields */
	
	/* Constructor */
	public Danbooru2ImageBoard(String siteUrl)
	{
		super(siteUrl);
	}
	
	/* Meth */
	
	@Override
	protected Downloader<?> createDownloader()
	{
		Downloader<?> downloader = new JsonDownloader(this.buildPostsUrl());
		return downloader;
	}
	
	protected String buildPostsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(String.format(API_POSTS_JSON, this.siteUrl));

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
