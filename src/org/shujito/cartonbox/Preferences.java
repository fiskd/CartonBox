package org.shujito.cartonbox;

import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Site.Type;
import org.shujito.cartonbox.model.db.SitesDB;

import android.content.Context;

public class Preferences
{
	public static void init(Context context)
	{
		
	}
	
	public static void defaultSites(Context context)
	{
		SitesDB db = new SitesDB(context);
		
		db.add(new Site()
			//.setId(0)
			.setName("Danbooru")
			.setType(Type.Danbooru2)
			.setUrl("http://danbooru.donmai.us/")
			.setPostsApi("/posts.json")
			.setPoolsApi("/pools.json")
			.setCommentsApi("/comments.json")
			.setNotesApi("/notes.json")
			.setArtistsApi("/artists.json")
			.setTagsApi("/tags.json"));
		
		db.add(new Site()
			//.setId(1)
			.setName("Danbooru (hijiribe)")
			.setType(Type.Danbooru2)
			.setUrl("http://hijiribe.donmai.us/")
			.setPostsApi("/posts.json")
			.setPoolsApi("/pools.json")
			.setCommentsApi("/comments.json")
			.setNotesApi("/notes.json")
			.setArtistsApi("/artists.json")
			.setTagsApi("/tags.json"));
		
		
	}
}
