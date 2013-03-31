package org.shujito.cartonbox.model;

import java.util.List;

public class Pool
{
	private List<Integer> postIds;
	private List<Post> posts;
	// meta
	private int id;
	private String name;
	private String description;
	private int postCount;
	// useful when the posts lack on information
	private Site site;
	
	public List<Integer> getPostIds()
	{ return postIds; }
	public List<Post> getPosts()
	{ return posts; }
	public int getId()
	{ return id; }
	public String getName()
	{ return name; }
	public String getDescription()
	{ return description; }
	public int getPostCount()
	{ return postCount; }
	public Site getSite()
	{ return site; }
	
	public Pool setPostIds(List<Integer> postIds)
	{
		this.postIds = postIds;
		return this;
	}
	public Pool setPosts(List<Post> posts)
	{
		this.posts = posts;
		return this;
	}
	public Pool setId(int id)
	{
		this.id = id;
		return this;
	}
	public Pool setName(String name)
	{
		this.name = name;
		return this;
	}
	public Pool setDescription(String description)
	{
		this.description = description;
		return this;
	}
	public Pool setPostCount(int postCount)
	{
		this.postCount = postCount;
		return this;
	}
	public Pool setSite(Site site)
	{
		this.site = site;
		return this;
	}
}
