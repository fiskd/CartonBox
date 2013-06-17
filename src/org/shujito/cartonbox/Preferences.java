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
	public static final String FIRST_RUN = "org.shujito.cartonbox.FIRST_RUN";
	public static final String FIRST_APP_RUN_OR_UPDATE = "org.shujito.cartonbox.FIRST_APP_RUN_OR_UPDATE";
	public static final String SITE_USERNAME = "org.shujito.cartonbox.SITE_USERNAME";
	public static final String SITE_PASSWORD = "org.shujito.cartonbox.SITE_PASSWORD";

	public static boolean isFirstAppRun()
	{
		return PreferenceManager.getDefaultSharedPreferences(CartonBox.getInstance()).getBoolean(FIRST_RUN, true);
	}
	
	public static boolean isFirstAppRunOrUpdate()
	{
		return PreferenceManager.getDefaultSharedPreferences(CartonBox.getInstance()).getBoolean(FIRST_APP_RUN_OR_UPDATE, true);
	}
	
	public static void init()
	{
		// get prefs
		PreferenceManager.getDefaultSharedPreferences(CartonBox.getInstance())
			// start editing
			.edit()
			// set true (false)
			.putBoolean(FIRST_RUN, false)
			// end editing
			.commit();
	}
	
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
		
		db.add(new Site()
			.setId(4)
			.setIconid(R.drawable.icon_yande_re)
			.setName("Yande.re")
			.setType(Type.Danbooru1)
			.setUrl("https://yande.re/")
			.setPostViewApi("/post/show/")
			.setPostsApi("/post/index.json")
			.setPoolsApi("/pool/index.json")
			.setCommentsApi("/comment/index.json")
			.setNotesApi("/note/index.json")
			.setArtistsApi("/artist/index.json")
			.setTagsApi("/tag/index.json"));
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
}
