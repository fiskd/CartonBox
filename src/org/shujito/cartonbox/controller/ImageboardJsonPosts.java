package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listener.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.task.listener.OnJsonResponseReceivedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.JsonParser;

public abstract class ImageboardJsonPosts extends ImageboardPosts
	implements OnJsonResponseReceivedListener
{
	protected ImageboardJsonPosts(Site site)
	{
		super(site);
	}
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		Post post = null;
		int index = 0;
		while((post = (Post)jp.getAtIndex(index)) != null)
		{
			index++;
			if(!"swf".equals(post.getFileExt()))
			{
				this.posts.append(post.getId(), post);
				post.setSite(this.site);
			}
		}
		
		if(index < this.postsPerPage)
		{
			this.doneDownloadingPosts = true;
			Logger.i("ImageboardJsonPosts::onResponseReceived", "I'm done downloading, nothing more to load...");
		}
		
		if(this.onPostsFetchedListeners != null)
		{
			for(OnPostsFetchedListener l : this.onPostsFetchedListeners)
			{
				if(l != null)
					l.onPostsFetched(this.posts);
			}
		}
		
		// increase page
		this.page++;
		// I'm done
		this.working= false;
	}
	
	// XXX: Should move to Danbooru board Posts
	@Override
	protected String buildPostsUrl()
	{
		StringBuilder url = new StringBuilder();
		
		url.append(this.site.getUrl());
		url.append(this.site.getPostsApi());
		
		url.append("?");
		
		if(this.username != null && this.password != null)
		{
			url.append(String.format(API_LOGIN, this.username));
			url.append("&");
			url.append(String.format(API_PASSWORD_HASH, this.password));
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
}