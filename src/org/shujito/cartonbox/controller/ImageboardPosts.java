package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.listener.OnRequestListener;
import org.shujito.cartonbox.controller.task.listener.OnJsonResponseReceivedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.JsonParser;
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
public abstract class ImageboardPosts extends Imageboard implements
	OnJsonResponseReceivedListener
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
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		Post post = null;
		int index = 0;
		while((post = (Post)jp.getAtIndex(index)) != null)
		{
			index++;
			if(!"swf".equals(post.getFileExt()))
			{
				this.posts.append(post.getId(), post);
				post.setSite(this.site);
			}
		}
		
		if(index < this.postsPerPage)
		{
			this.doneDownloadingPosts = true;
			Logger.i("ImageboardApi::onResponseReceived", "I'm done downloading, nothing more to load...");
		}
		
		if(this.onPostsFetchedListeners != null)
		{
			for(OnPostsFetchedListener l : this.onPostsFetchedListeners)
			{
				if(l != null)
					l.onPostsFetched(this.posts);
			}
		}
		
		// increase page
		this.page++;
		// I'm done
		this.working= false;
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
	
	protected String buildPostsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(this.site.getUrl());
		url.append(this.site.getPostsApi());
		
		url.append("?");
		
		if(this.username != null && this.password != null)
		{
			url.append(String.format(API_LOGIN, this.username));
			url.append("&");
			url.append(String.format(API_PASSWORD_HASH, this.password));
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
