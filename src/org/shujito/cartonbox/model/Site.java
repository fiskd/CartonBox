package org.shujito.cartonbox.model;

import java.io.Serializable;

public class Site implements Serializable
{
	public enum Type
	{
		Danbooru1,
		Danbooru2,
		Gelbooru
	}
	
	/* Static */
	private static final long serialVersionUID = 1L;
	
	/* fields */
	private int id;
	private Type type;
	private String url;
	private String postsApi;
	private String poolsApi;
	private String commentsApi;
	private String notesApi;
	private String artistsApi;
	private String tagsApi;
	
	/* getters */
	public int getId()
	{ return this.id; }
	public Type getType()
	{ return this.type; }
	public String getUrl()
	{ return this.url; }
	public String getPostsApi()
	{ return this.postsApi; }
	public String getPoolsApi()
	{ return this.poolsApi; }
	public String getCommentsApi()
	{ return this.commentsApi; }
	public String getNotesApi()
	{ return this.notesApi; }
	public String getArtistsApi()
	{ return this.artistsApi; }
	public String getTagsApi()
	{ return this.tagsApi; }

	/* setters */
	public Site setId(int i)
	{
		this.id = i;
		return this;
	}
	public Site setType(Type e)
	{
		this.type = e;
		return this;
	}
	public Site setUrl(String s)
	{
		this.url = s;
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
	public Site setNotesApi(String s)
	{
		this.notesApi = s;
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
