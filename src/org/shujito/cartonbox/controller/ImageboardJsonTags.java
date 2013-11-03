package org.shujito.cartonbox.controller;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnTagsFetchedListener;
import org.shujito.cartonbox.controller.task.JsonDownloader;
import org.shujito.cartonbox.controller.task.listener.OnJsonResponseReceivedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.parser.JsonParser;
import org.shujito.cartonbox.util.ConcurrentTask;

public abstract class ImageboardJsonTags extends ImageboardTags
	implements OnJsonResponseReceivedListener
{
	protected ImageboardJsonTags(Site site)
	{
		super(site);
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
	
	// XXX: this thing is very hacky, I hope to remove this sometime...
	@Override
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
	
	// XXX: Should move to Danbooru board tags
	@Override
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
