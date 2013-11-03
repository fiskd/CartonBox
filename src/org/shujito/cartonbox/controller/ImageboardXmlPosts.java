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
		return null;
	}
}
