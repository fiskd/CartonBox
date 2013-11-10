package org.shujito.cartonbox.controller;

import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.task.listener.OnXmlResponseReceivedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.XmlParser;

public abstract class ImageboardXmlPosts extends ImageboardPosts
	implements OnXmlResponseReceivedListener
{
	public static final String API_LIMIT = "limit=%d";
	public static final String API_PAGE = "pid=%d";
	
	protected ImageboardXmlPosts(Site site)
	{
		super(site);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onResponseReceived(XmlParser<?> xp)
	{
		List<Post> posts = (List<Post>)xp.get();
		for(Post p : posts)
		{
			if(!"swf".equals(p.getFileExt()))
			{
				this.posts.append(p.getId(), p);
				p.setSite(this.site);
			}
		}
		
		if(posts.size() < this.postsPerPage)
		{
			this.doneDownloadingPosts = true;
			Logger.i("ImageboardXmlPosts::onResponseReceived", "I'm done downloading, nothing more to load...");
		}
		
		if(this.onPostsFetchedListeners != null)
		{
			for(OnPostsFetchedListener l : this.onPostsFetchedListeners)
			{
				if(l != null)
				{
					l.onPostsFetched(this.posts);
				}
			}
		}
		
		this.page++;
		this.working = false;
	}
	
	// XXX: Should move to Gelbooru board Posts
	@Override
	protected String buildPostsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(this.site.getUrl());
		url.append(this.site.getPostsApi());
		url.append("&");
		url.append(String.format(API_PAGE, this.page));
		url.append("&");
		url.append(String.format(API_LIMIT, this.postsPerPage));

		if(this.tags.size() > 0)
		{
			url.append("&");
			url.append(String.format(API_TAGS, this.buildTags()));
		}
		
		return url.toString();
	}
}
