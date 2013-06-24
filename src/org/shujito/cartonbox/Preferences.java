package org.shujito.cartonbox;

import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Site.Type;
import org.shujito.cartonbox.model.db.SitesDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences
{
	//public static final String <prefname> = "org.shujito.cartonbox.<prefname>";
	public static final String SITE_USERNAME = "org.shujito.cartonbox.SITE_USERNAME";
	public static final String SITE_PASSWORD = "org.shujito.cartonbox.SITE_PASSWORD";
	
	public static void defaultSites()
	{
		Context context = CartonBox.getInstance();
		
		SitesDB db = new SitesDB(context);
		
		db.add(new Site()
			.setId(1)
			.setIconid(R.drawable.icon_danbooru)
			.setName("Danbooru")
			.setType(Type.Danbooru2)
			.setUrl("http://danbooru.donmai.us/")
			.setPostViewApi("/posts")
			.setPostsApi("/posts.json")
			.setPoolsApi("/pools.json")
			.setCommentsApi("/comments.json")
			.setNotesApi("/notes.json")
			.setArtistsApi("/artists.json")
			.setTagsApi("/tags.json"));
		
		db.add(new Site()
			.setId(2)
			.setIconid(R.drawable.icon_danbooru)
			.setName("Danbooru (hijiribe)")
			.setType(Type.Danbooru2)
			.setUrl("http://hijiribe.donmai.us/")
			.setPostViewApi("/posts")
			.setPostsApi("/posts.json")
			.setPoolsApi("/pools.json")
			.setCommentsApi("/comments.json")
			.setNotesApi("/notes.json")
			.setArtistsApi("/artists.json")
			.setTagsApi("/tags.json"));
		
		db.add(new Site()
			.setId(3)
			.setIconid(R.drawable.icon_behoimi)
			.setName("3DBooru (behoimi)")
			.setType(Type.Danbooru1)
			.setUrl("http://behoimi.org/")
			.setPostViewApi("/post/show/")
			.setPostsApi("/post/index.json")
			.setPoolsApi("/pool/index.json")
			.setCommentsApi("/comment/index.json")
			.setNotesApi("/note/index.json")
			.setArtistsApi("/artist/index.json")
			.setTagsApi("/tag/index.json"));
		// https://yande.re/forum/show/17672
		// I don't want to argue, it's his site, it's my app, so...
		/*
		db.add(new Site()
			.setId(4)
			.setIconid(R.drawable.icon_yande_re)
			.setName("Yande.re")
			.setType(Type.Danbooru1)
			.setUrl("https://yande.re/")
			.setPostViewApi("/post/show/")
			.setPostsApi("/post.json")
			.setPoolsApi("/pool.json")
			.setCommentsApi("/comment.json")
			.setNotesApi("/note.json")
			.setArtistsApi("/artist.json")
			.setTagsApi("/tag.json"));
		//*/
		// ...yeah
	}

	public static boolean getBool(int id)
	{
		return getBool(id, false);
	}
	
	public static boolean getBool(int id, boolean def)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return globalPrefs.getBoolean(context.getString(id), def);
	}
	
	public static int getInt(int id)
	{
		return getInt(id, 0);
	}
	
	public static int getInt(int id, int def)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return globalPrefs.getInt(context.getString(id), def);
	}
	
	public static String getString(int id)
	{
		return getString(id, null);
	}
	
	public static String getString(int id, String def)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return globalPrefs.getString(context.getString(id), def);
	}
	
	public static void setInt(int id, int value)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		globalPrefs
			// start editing
			.edit()
			// collocate
			.putInt(context.getString(id), value)
			// submit changes
			.commit();
	}
	
	public static void setString(int id, String value)
	{
		Context context = CartonBox.getInstance();
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		globalPrefs
			// start editing
			.edit()
			// collocate
			.putString(context.getString(id), value)
			// submit changes
			.commit();
	}
}
