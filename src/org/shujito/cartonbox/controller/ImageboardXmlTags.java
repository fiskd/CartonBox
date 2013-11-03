package org.shujito.cartonbox.controller;

import java.util.List;

import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Tag;

public abstract class ImageboardXmlTags extends ImageboardTags
{
	public ImageboardXmlTags(Site site)
	{
		super(site);
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
		return null;
	}
}
