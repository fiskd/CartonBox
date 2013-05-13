package org.shujito.cartonbox.controller;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.URLEncoder;
import org.shujito.cartonbox.controller.listeners.OnAccessDeniedListener;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnInternalServerErrorListener;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnPostsRequestListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.JsonParser;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;
import org.shujito.cartonbox.model.Response;
import org.shujito.cartonbox.model.Site;

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
	OnErrorListener,
	OnAccessDeniedListener,
	OnInternalServerErrorListener,
	OnResponseReceivedListener
{
	/* Listeners */
	
	List<OnErrorListener> onErrorListeners = null;
	List<OnPostsFetchedListener> onPostsFetchedListeners = null;
	List<OnPostsRequestListener> onPostsRequestListeners = null;
	
	public void addOnErrorListener(OnErrorListener l)
	{
		if(this.onErrorListeners == null)
			this.onErrorListeners = new ArrayList<OnErrorListener>();
		this.onErrorListeners.add(l);
	}
	public void addOnPostsFetchedListener(OnPostsFetchedListener l)
	{
		if(this.onPostsFetchedListeners == null)
			this.onPostsFetchedListeners = new ArrayList<OnPostsFetchedListener>();
		this.onPostsFetchedListeners.add(l);
	}
	public void addOnPostsRequestListener(OnPostsRequestListener l)
	{
		if(this.onPostsRequestListeners == null)
			this.onPostsRequestListeners = new ArrayList<OnPostsRequestListener>();
		this.onPostsRequestListeners.add(l);
	}
	
	public void removeOnErrorListener(OnErrorListener l)
	{
		if(this.onErrorListeners == null)
			this.onErrorListeners = new ArrayList<OnErrorListener>();
		this.onErrorListeners.remove(l);
	}
	public void removeOnPostsFetchedListener(OnPostsFetchedListener l)
	{
		if(this.onPostsFetchedListeners == null)
			this.onPostsFetchedListeners = new ArrayList<OnPostsFetchedListener>();
		this.onPostsFetchedListeners.remove(l);
	}
	public void removeOnPostsRequestListener(OnPostsRequestListener l)
	{
		if(this.onPostsRequestListeners == null)
			this.onPostsRequestListeners = new ArrayList<OnPostsRequestListener>();
		this.onPostsRequestListeners.remove(l);
	}
	
	/* Fields */
	
	Downloader<?> downloader = null;
	SparseArray<Post> posts = null;
	ArrayList<String> tags = null;
	//String siteUrl = null;
	
	boolean working = false;
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
	
	protected abstract Downloader<?> createDownloader();
	
	public void putTag(String tag)
	{
		this.tags.add(tag);
	}
	
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
	
	public void request()
	{
		if(!this.doneDownloadingPosts)
		{
			if(!this.working)
			{
				if(this.onPostsRequestListeners != null)
				{
					for(OnPostsRequestListener l : this.onPostsRequestListeners)
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
	public void onError(int errCode, String message)
	{
		if(this.onErrorListeners != null)
		{
			for(OnErrorListener l : this.onErrorListeners)
			{
				l.onError(errCode, message);
			}
		}
		
		this.working = false;
	}
	
	@Override
	public void onAccessDenied()
	{
		if(this.onErrorListeners != null)
		{
			for(OnErrorListener l : this.onErrorListeners)
			{
				l.onError(HttpURLConnection.HTTP_FORBIDDEN, "Access denied");
			}
		}
		
		this.working = false;
	}
	
	@Override
	public void onInternalServerError(JsonParser<?> jarr)
	{
		Response response = (Response)jarr.getAtIndex(0);
		
		if(this.onErrorListeners != null)
		{
			for(OnErrorListener l : this.onErrorListeners)
			{
				l.onError(HttpURLConnection.HTTP_INTERNAL_ERROR, response.getReason());
			}
		}
		
		this.working = false;
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
