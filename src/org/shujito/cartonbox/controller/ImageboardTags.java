package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.controller.listeners.OnRequestListener;
import org.shujito.cartonbox.controller.listeners.OnResponseReceivedListener;
import org.shujito.cartonbox.controller.listeners.OnTagsFetchedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.parser.JsonParser;
import org.shujito.cartonbox.utils.ConcurrentTask;

/* tags logic */
// step 0: config
// step 1: request tags
// step 2: process request
// step 2a: build query
// step 2b: download
// step 3: give result
// step 4: gather tags
public abstract class ImageboardTags extends Imageboard implements
	OnResponseReceivedListener
{
	/* listener */
	List<OnTagsFetchedListener> onTagsFetchedListeners = null;
	
	public void addOnTagsFetchedListeners( OnTagsFetchedListener l)
	{
		if(this.onTagsFetchedListeners == null)
			this.onTagsFetchedListeners = new ArrayList<OnTagsFetchedListener>();
		this.onTagsFetchedListeners.add(l);
	}
	public void removeOnTagsFetchedListeners( OnTagsFetchedListener l)
	{
		if(this.onTagsFetchedListeners == null)
			this.onTagsFetchedListeners = new ArrayList<OnTagsFetchedListener>();
		this.onTagsFetchedListeners.remove(l);
	}
	
	/* Fields */
	String query = null;
	
	/* Constructor */
	
	public ImageboardTags(Site site)
	{
		super(site);
	}
	
	/* Getters */
	
	public void setQuery(String query)
	{
		if(query != null && query.length() > 0)
		{
			// replace spaces with asterisks
			query = query.replace(' ', '*');
			// prepend an asterisk
			query = "*".concat(query);
		}
		else query = "";
		// set it
		this.query = query.concat("*");
	}
	
	/* Setters */
	
	public String getQuery()
	{
		return this.query;
	}
	
	/* Meth */
	
	@Override
	public void clear()
	{
		if(this.working && this.downloader != null)
			this.downloader.cancel(true);
		
		this.working = false;
	}
	
	@Override
	public void request()
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
			
			this.downloader = this.createDownloader();
			//this.downloader.setOnErrorListener(this);
			ConcurrentTask.execute(this.downloader);
			this.working = true;
		}
	}
	
	public String buildTagsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(this.site.getUrl());
		url.append(this.site.getTagsApi());
		
		url.append("?");
		
		if(this.username != null && this.password != null)
		{
			url.append(String.format(API_LOGIN, this.username));
			url.append("&");
			url.append(String.format(API_PASSWORD_HASH, this.password));
			url.append("&");
		}
		
		return url.toString();
	}
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		Tag tag = null;
		// place tags here
		List<Tag> tags = new ArrayList<Tag>();
		// iterate...
		while((tag = (Tag)jp.getAtIndex(tags.size())) != null)
		{
			// we don't need anything complex here, we're just looking for tags
			tags.add(tag);
		}
		
		// talk to the listeners
		for(OnTagsFetchedListener l : this.onTagsFetchedListeners)
		{
			if(l != null)
				l.onTagsFetchedListener(tags);
		}
		
		// not working anymore
		this.working = false;
	}
}
