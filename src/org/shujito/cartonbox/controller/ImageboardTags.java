package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.controller.listener.OnRequestListener;
import org.shujito.cartonbox.controller.listener.OnTagsFetchedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.util.ConcurrentTask;

/* tags logic */
// step 0: config
// step 1: request tags
// step 2: process request
// step 2a: build query
// step 2b: download
// step 3: give result
// step 4: gather tags
public abstract class ImageboardTags extends Imageboard
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
	
	protected ImageboardTags(Site site)
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
	
	
	public abstract String buildTagsUrl();
	
	// XXX: hacky
	public abstract List<Tag> requestSynchronous(String query);
}
