package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnRequestListener;
import org.shujito.cartonbox.controller.listeners.OnJsonResponseReceivedListener;
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
	OnJsonResponseReceivedListener
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
			// wrap with asterisks
			query = "*".concat(query).concat("*");
		}
		else query = "*";
		// set it
		this.query = query;
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
		
		this.query = null;
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
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		List<Tag> tags = this.processParser(jp);
		
		// talk to the listeners
		for(OnTagsFetchedListener l : this.onTagsFetchedListeners)
		{
			if(l != null)
				l.onTagsFetchedListener(tags);
		}
		
		// not working anymore
		this.working = false;
	}
	
	private List<Tag> processParser(JsonParser<?> jp)
	{
		if(jp == null)
			return null;
		
		Tag tag = null;
		// place tags here
		List<Tag> tags = new ArrayList<Tag>();
		// iterate...
		while((tag = (Tag)jp.getAtIndex(tags.size())) != null)
		{
			// we don't need anything complex here, we're just looking for tags
			tags.add(tag);
		}
		return tags;
	}
	
	// this thing is very hacky, I hope to remove
	// this sometime... (it is unused as of now)
	public List<Tag> requestSynchronous(String query)
	{
		// backup the query so nothing breaks later on
		String backup = this.getQuery();
		this.setQuery(query);
		// make a downloader
		JsonDownloader jdown = (JsonDownloader)this.createDownloader();
		// damn these two troublemakers...
		jdown.setOnErrorListener(null);
		jdown.setOnResponseReceivedListener(null);
		// put the query back in
		this.setQuery(backup);
		ConcurrentTask.execute(jdown);
		JsonParser<?> parser = null;
		try
		{
			parser = jdown.get();
		}
		catch(Exception ex)
		{
			Logger.e("TagsFilter::performFiltering", ex.getMessage(), ex);
			return null;
		}
		
		return processParser(parser);
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
}
