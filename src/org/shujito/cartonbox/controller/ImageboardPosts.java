package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.listener.OnRequestListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.util.ConcurrentTask;
import org.shujito.cartonbox.util.URLEncoder;

import android.util.SparseArray;

/* posts logic */
// step 0: config (username, password, tags, etc)
// step 1: request posts
// step 2: process request
// step 2a: build query
// step 2b: download
// step 2c: filters
// step 3: give result
// step 4: gather posts
public abstract class ImageboardPosts extends Imageboard
{
	/* Listener */
	List<OnPostsFetchedListener> onPostsFetchedListeners = null;
	
	public void addOnPostsFetchedListener(OnPostsFetchedListener l)
	{
		if(this.onPostsFetchedListeners == null)
			this.onPostsFetchedListeners = new ArrayList<OnPostsFetchedListener>();
		this.onPostsFetchedListeners.add(l);
	}
	
	public void removeOnPostsFetchedListener(OnPostsFetchedListener l)
	{
		if(this.onPostsFetchedListeners == null)
			this.onPostsFetchedListeners = new ArrayList<OnPostsFetchedListener>();
		this.onPostsFetchedListeners.remove(l);
	}
	
	/* Fields */
	
	protected SparseArray<Post> posts = null;
	protected ArrayList<String> tags = null;
	
	boolean doneDownloadingPosts = false;
	
	protected int postsPerPage = 20;
	protected int page = 1;
	
	/* Constructor */
	
	protected ImageboardPosts(Site site)
	{
		//this.siteUrl = siteUrl;
		//this.site = site;
		super(site);
		
		this.posts = new SparseArray<Post>();
		this.tags = new ArrayList<String>();
	}
	
	/* Getters */
	
	public SparseArray<Post> getPosts()
	{
		return this.posts;
	}
	
	public int getPostsPerPage()
	{
		return this.postsPerPage;
	}
	
	public String[] getTags()
	{
		return (String[])this.tags.toArray(new String[this.tags.size()]);
	}
	
	/* Setters */
	
	public void setPostsPerPage(int postsPerPage)
	{
		this.postsPerPage = postsPerPage;
	}
	
	/* Meth */
	
	public void putTag(String tag)
	{
		this.tags.add(tag);
	}
	
	@Override
	public void clear()
	{
		if(this.working && this.downloader != null)
			this.downloader.cancel(true);
		
		this.tags.clear();
		this.posts.clear();
		this.doneDownloadingPosts = false;
		this.working = false;
		this.page = 1;
	}
	
	@Override
	public void request()
	{
		if(!this.doneDownloadingPosts)
		{
			if(!this.working)
			{
				if(this.onRequestListeners != null)
				{
					for(OnRequestListener l : this.onRequestListeners)
					{
						l.onRequest();
					}
				}
				
				Logger.i("Imageboard::requestPosts", "Request job, page " + this.page);
				this.downloader = this.createDownloader();
				//this.downloader.setOnErrorListener(this);
				ConcurrentTask.execute(this.downloader);
				this.working = true;
			}
		}
	}
	
	public String buildTags()
	{
		String tags = null;
		for(String s : this.tags)
		{
			if(tags == null)
				tags = URLEncoder.encode(s);
			else
			{
				tags = tags.concat("+");
				tags = tags.concat(URLEncoder.encode(s));
			}
		}
		
		return tags;
	}
	
	protected abstract String buildPostsUrl();
}
