package org.shujito.cartonbox.model;

public class Site
{
	/* fields */
	String url;
	String postsApi;
	String poolsApi;
	String commentsApi;
	String artistsApi;
	String tagsApi;
	
	/* getters */
	public String getUrl()
	{ return this.url; }
	public String getPostsApi()
	{ return this.postsApi; }
	public String getPoolsApi()
	{ return this.poolsApi; }
	public String getCommentsApi()
	{ return this.commentsApi; }
	public String getArtistsApi()
	{ return this.artistsApi; }
	public String getTagsApi()
	{ return this.tagsApi; }

	/* setters */
	public Site setUrl(String url)
	{
		this.url = url;
		return this;
	}
	public Site setPostsApi(String s)
	{
		this.postsApi = s;
		return this;
	}
	public Site setPoolsApi(String s)
	{
		this.poolsApi = s;
		return this;
	}
	public Site setCommentsApi(String s)
	{
		this.commentsApi = s;
		return this;
	}
	public Site setArtistsApi(String s)
	{
		this.artistsApi = s;
		return this;
	}
	public Site setTagsApi(String s)
	{
		this.tagsApi = s;
		return this;
	}
}
