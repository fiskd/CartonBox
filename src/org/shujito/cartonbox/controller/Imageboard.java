package org.shujito.cartonbox.controller;

import java.util.ArrayList;

import org.shujito.cartonbox.model.Post;

import android.util.SparseArray;

/* posts logic */
// step 0: config (username, password, tags, etc)
// step 1: request posts
// step 2: process request
// step 2a: build query
// step 2b: download
// step 2c: filters
// step 3: give posts
public abstract class Imageboard
{
	/* fields */
	Downloader<?> jsonDownloader = null;
	Downloader<?> xmlDownloader = null;
	SparseArray<Post> posts = null;
	ArrayList<String> tags = null;
	
	String hostSiteUrl = null;
	String username = null;
	String password = null;
	String passwordHash = null;
	
	boolean working = false;
	boolean doneDownloadingPosts = false;
	
	int postsPerPage = 20;
	int page = 1;
	
	/* constructor */
	
	protected Imageboard(String hostSiteUrl)
	{
		this.hostSiteUrl = hostSiteUrl;
		this.posts = new SparseArray<Post>();
		this.tags = new ArrayList<String>();
	}
	
	/* Getters */
	
	public String getHostSiteUrl()
	{
		return hostSiteUrl;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getPasswordHash()
	{
		return passwordHash;
	}
	
	public SparseArray<Post> getPosts()
	{
		return posts;
	}
	
	public int getPostsPerPage()
	{
		return postsPerPage;
	}
	
	/* Setters */
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	
	public void setPostsPerPage(int postsPerPage)
	{
		this.postsPerPage = postsPerPage;
	}
	
	/* Meth */
	
	protected abstract Downloader<?> createDownloader();
	
	public void putTag(String tag)
	{
		this.tags.add(tag);
	}
	
	public void clear()
	{
		
	}
	
	public void requestPosts()
	{
		if(!this.doneDownloadingPosts)
		{
			if(!this.working)
			{
				this.jsonDownloader = this.createDownloader();
				this.jsonDownloader.execute();
				this.working = true;
			}
		}
	}
}
