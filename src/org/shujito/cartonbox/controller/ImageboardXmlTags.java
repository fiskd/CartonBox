package org.shujito.cartonbox.controller;

import java.util.List;

import org.shujito.cartonbox.controller.listener.OnTagsFetchedListener;
import org.shujito.cartonbox.controller.task.listener.OnXmlResponseReceivedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.parser.XmlParser;

public abstract class ImageboardXmlTags extends ImageboardTags
	implements OnXmlResponseReceivedListener
{
	public static final String API_LIMIT = "limit=%d";
	public static final String API_NAME_PATTERN = "name_pattern=%s";
	
	public ImageboardXmlTags(Site site)
	{
		super(site);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onResponseReceived(XmlParser<?> xp)
	{
		List<Tag> tags = (List<Tag>)xp.get();
		
		for(OnTagsFetchedListener l : this.onTagsFetchedListeners)
		{
			if(l != null)
				l.onTagsFetchedListener(tags);
		}
	}
	
	@Override
	public void setQuery(String query)
	{
		this.query = query;
	}
	
	@Override
	public List<Tag> requestSynchronous(String query)
	{
		return null;
	}
	
	// XXX: Should move to Gelbooru board Tags
	@Override
	public String buildTagsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(this.site.getUrl());
		url.append(this.site.getTagsApi());
		
		url.append("&");
		url.append(String.format(API_LIMIT, 20));
		
		if(this.query != null && this.query.length() > 0)
		{
			url.append("&");
			url.append(String.format(API_NAME_PATTERN, this.query));
		}
		
		return url.toString();
	}
}
