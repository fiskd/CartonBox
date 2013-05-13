package org.shujito.cartonbox.controller;

public class ImageboardApis
{
	ImageboardPosts imageboardPosts = null;
	ImageboardTags imageboardTags = null;
	
	public ImageboardApis()
	{
		
	}
	
	public ImageboardPosts getImageboardPosts()
	{
		return imageboardPosts;
	}
	public ImageboardTags getImageboardTags()
	{
		return imageboardTags;
	}
	
	public void setImageboardPosts(ImageboardPosts imageboardPosts)
	{
		this.imageboardPosts = imageboardPosts;
	}
	public void setImageboardTags(ImageboardTags imageboardTags)
	{
		this.imageboardTags = imageboardTags;
	}
}
