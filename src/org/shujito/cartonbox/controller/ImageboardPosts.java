package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnPostsRequestedListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.JsonParser;
import org.shujito.cartonbox.utils.URLEncoder;

import android.util.SparseArray;

/* posts logic */
// step 0: config (username, password, tags, etc)
// step 1: request posts
// step 2: process request
// step 2a: build query
// step 2b: download
// step 2c: filters
// step 3: give result
// step 4: give posts
public abstract class ImageboardPosts extends Imageboard implements
	OnResponseReceivedListener
{
	/* Listener */
	List<OnPostsRequestedListener> onPostsRequestedListeners = null;
	
	public void addOnPostsRequestListener(OnPostsRequestedListener l)
	{
		if(this.onPostsRequestedListeners == null)
			this.onPostsRequestedListeners = new ArrayList<OnPostsRequestedListener>();
		this.onPostsRequestedListeners.add(l);
	}
	
	public void removeOnPostsRequestListener(OnPostsRequestedListener l)
	{
		if(this.onPostsRequestedListeners == null)
			this.onPostsRequestedListeners = new ArrayList<OnPostsRequestedListener>();
		this.onPostsRequestedListeners.remove(l);
	}
	
	/* Fields */
	
	protected SparseArray<Post> posts = null;
	protected ArrayList<String> tags = null;
	//protected Downloader<?> downloader = null;
	//protected boolean working = false;
	//protected String siteUrl = null;
	
	boolean doneDownloadingPosts = false;
	
	int postsPerPage = 20;
	int page = 1;
	
	boolean showSafePosts = true;
	boolean showQuestionablePosts = false;
	boolean showExplicitPosts = false;
	
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
	
	public boolean getShowSafePosts()
	{
		return this.showSafePosts;
	}
	
	public boolean getShowQuestionablePosts()
	{
		return this.showQuestionablePosts;
	}
	
	public boolean getShowExplicitPosts()
	{
		return this.showExplicitPosts;
	}
	
	/* Setters */
	
	public void setPostsPerPage(int postsPerPage)
	{
		this.postsPerPage = postsPerPage;
	}
	
	public void setShowSafePosts(boolean showSafePosts)
	{
		this.showSafePosts = showSafePosts;
	}
	
	public void setShowQuestionablePosts(boolean showQuestionablePosts)
	{
		this.showQuestionablePosts = showQuestionablePosts;
	}
	
	public void setShowExplicitPosts(boolean showExplicitPosts)
	{
		this.showExplicitPosts = showExplicitPosts;
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
				if(this.onPostsRequestedListeners != null)
				{
					for(OnPostsRequestedListener l : this.onPostsRequestedListeners)
					{
						l.onPostsRequest();
					}
				}
				
				Logger.i("Imageboard::requestPosts", "Request job, page " + this.page);
				this.downloader = this.createDownloader();
				this.downloader.execute();
				//ConcurrentTask.execute(this.downloader);
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
	
	protected String buildPostsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(this.site.getUrl());
		url.append(this.site.getPostsApi());
		
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
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		Post p = null;
		int index = 0;
		while((p = (Post)jp.getAtIndex(index)) != null)
		{
			index++;
			boolean shouldAdd = false;
			
			shouldAdd = (this.showSafePosts && p.getRating() == Rating.Safe) || shouldAdd;
			shouldAdd = (this.showQuestionablePosts && p.getRating() == Rating.Questionable) || shouldAdd;
			shouldAdd = (this.showExplicitPosts && p.getRating() == Rating.Explicit) || shouldAdd;
			shouldAdd = !"swf".equals(p.getFileExt()) && shouldAdd; //!p.getFileExt().equals("swf") && shouldAdd;
			
			if(shouldAdd)
			{
				this.posts.append(p.getId(), p);
				p.setSite(this.site);
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
					l.onPostsFetched(this);
			}
		}
		
		// increase page
		this.page++;
		// I'm done
		this.working= false;
	}
}
