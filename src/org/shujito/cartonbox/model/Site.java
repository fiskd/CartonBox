package org.shujito.cartonbox.model;

import java.io.Serializable;

public class Site implements Serializable
{
	public enum Type
	{
		Danbooru1(1),
		Danbooru2(2),
		Gelbooru(3),
		;
		
		private int value;
		
		Type(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{ return this.value; }
		
		public static Type fromInt(int value)
		{
			switch(value)
			{
				case 1: return Danbooru1;
				case 2: return Danbooru2;
				case 3: return Gelbooru;
			}
			return null;
		}
	}
	
	/* Static */
	private static final long serialVersionUID = 1L;
	
	public static Site createByType(Type type)
	{
		if(type == Type.Danbooru1)
		{
			return new Site()
				.setType(type)
				.setPostViewApi("/post/show/")
				.setPostsApi("/post/index.json")
				.setPoolsApi("/pool/index.json")
				.setCommentsApi("/comment/index.json")
				.setNotesApi("/note/index.json")
				.setTagsApi("/tag/index.json");
		}
		if(type == Type.Danbooru2)
		{
			return new Site()
				.setType(type)
				.setPostViewApi("/posts")
				.setPostsApi("/posts.json")
				.setPoolsApi("/pools.json")
				.setCommentsApi("/comments.json")
				.setNotesApi("/notes.json")
				.setTagsApi("/tags.json");
		}
		if(type == Type.Gelbooru)
		{
			return new Site()
				.setType(type)
				.setPostsApi("/index.php?page=dapi&s=post&q=index")
				.setTagsApi("/index.php?page=dapi&s=tag&q=index");
		}
		return null;
	}
	
	/* fields */
	private long id;
	private Type type;
	private String icon;
	private String name;
	private String url;
	private String postViewApi;
	private String postsApi;
	private String poolsApi;
	private String commentsApi;
	private String notesApi;
	private String artistsApi;
	private String tagsApi;
	
	/* constructor */
	public Site()
	{
		this.id = System.currentTimeMillis();
	}
	
	/* getters */
	public long getId()
	{ return this.id; }
	public Type getType()
	{ return this.type; }
	public String getIcon()
	{ return this.icon; }
	public String getName()
	{ return this.name; }
	public String getUrl()
	{ return this.url; }
	public String getPostViewApi()
	{ return postViewApi; }
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
	//*
	public Site setId(long i)
	{
		this.id = i;
		return this;
	}
	//*/
	public Site setType(Type e)
	{
		this.type = e;
		return this;
	}
	public Site setIcon(String icon)
	{
		this.icon = icon;
		return this;
	}
	public Site setName(String s)
	{
		this.name = s;
		return this;
	}
	public Site setUrl(String s)
	{
		this.url = s;
		return this;
	}
	public Site setPostViewApi(String s)
	{
		this.postViewApi = s;
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
