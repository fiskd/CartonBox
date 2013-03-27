package org.shujito.cartonbox.controller;

import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.URLEncoder;
import org.shujito.cartonbox.controller.listeners.OnAccessDeniedListener;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnInternalServerErrorListener;
import org.shujito.cartonbox.controller.listeners.OnPoolsFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.model.JsonParser;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Response;

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
public abstract class Imageboard implements
	OnResponseReceivedListener,
	OnErrorListener,
	OnAccessDeniedListener,
	OnInternalServerErrorListener
{
	/* Static */
	public static final String API_DANBOORU_PASSWORD = "choujin-steiner--%s--";
	
	/* Listeners */
	List<OnErrorListener> onErrorListeners = null;
	List<OnPostsFetchedListener> onPostsFetchedListeners = null;
	List<OnPoolsFetchedListener> onPoolsFetchedListeners = null;
	
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
	public void addOnPoolsFetchedListener(OnPoolsFetchedListener l)
	{
		if(this.onPoolsFetchedListeners == null)
			this.onPoolsFetchedListeners = new ArrayList<OnPoolsFetchedListener>();
		this.onPoolsFetchedListeners.add(l);
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
	public void removeOnPoolsFetchedListener(OnPoolsFetchedListener l)
	{
		if(this.onPoolsFetchedListeners == null)
			this.onPoolsFetchedListeners = new ArrayList<OnPoolsFetchedListener>();
		this.onPoolsFetchedListeners.remove(l);
	}
	
	/* Fields */
	Downloader<?> downloader = null;
	//Downloader<?> xmlDownloader = null;
	SparseArray<Post> posts = null;
	ArrayList<String> tags = null;
	//Site site = null;
	String siteUrl = null;
	
	String username = null;
	String password = null;
	
	boolean working = false;
	boolean doneDownloadingPosts = false;
	
	int postsPerPage = 20;
	int page = 1;
	
	/* Constructor */
	
	protected Imageboard(String siteUrl)
	{
		this.siteUrl = siteUrl;
		this.posts = new SparseArray<Post>();
		this.tags = new ArrayList<String>();
	}
	
	/* Getters */
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
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
		MessageDigest msgdig = null;
		
		try
		{ msgdig = MessageDigest.getInstance("SHA-1"); }
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		byte[] buff = String.format(API_DANBOORU_PASSWORD, password).getBytes();
		buff = msgdig.digest(buff);
		
		StringBuffer sbuff = new StringBuffer();
		for(int idx = 0; idx < buff.length; idx++)
		{
			String part = Integer.toHexString(0xff & buff[idx]);
			if(part.length() == 1)
				sbuff.append("0");
			sbuff.append(part);
		}
		this.password = sbuff.toString();
		
		//this.password = password;
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
		if(this.working && this.downloader != null)
			this.downloader.cancel(true);
		
		this.tags.clear();
		this.posts.clear();
		this.doneDownloadingPosts = false;
		this.working = false;
		this.page = 1;
	}
	
	public void requestPosts()
	{
		if(!this.doneDownloadingPosts)
		{
			if(!this.working)
			{
				this.downloader = this.createDownloader();
				this.downloader.execute();
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
	}
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		Post p = null;
		int index = 0;
		while((p = (Post)jp.getAtIndex(index)) != null)
		{
			index++;
			// TODO: rating filters
			this.posts.append(p.getId(), p);
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
