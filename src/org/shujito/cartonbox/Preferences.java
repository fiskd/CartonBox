package org.shujito.cartonbox;

import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.Site.Type;
import org.shujito.cartonbox.model.db.SitesDB;

import android.content.Context;
import android.preference.PreferenceManager;

public class Preferences
{
	//public static String <prefname> = "org.shujito.cartonbox.<prefname>";
	public static String FIRST_RUN = "org.shujito.cartonbox.FIRST_RUN";
	
	public static boolean isFirstRun()
	{
		return PreferenceManager.getDefaultSharedPreferences(CartonBox.getInstance()).getBoolean(FIRST_RUN, true);
	}
	
	public static void init()
	{
		// get prefs
		PreferenceManager.getDefaultSharedPreferences(CartonBox.getInstance())
			// start editing
			.edit()
			// set true
			.putBoolean(FIRST_RUN, false)
			// end editing
			.commit();
		
		// now let's do some stuff like uh...
		// oh I know! let's set some default sites!
		defaultSites();
		// what else?
	}
	
	public static void defaultSites()
	{
		Context context = CartonBox.getInstance();
		
		SitesDB db = new SitesDB(context);
		
		db.add(new Site()
			//.setId(1)
			.setIconid(R.drawable.icon_danbooru)
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
			//.setId(2)
			.setIconid(R.drawable.icon_danbooru)
			.setName("Danbooru (hijiribe)")
			.setType(Type.Danbooru2)
			.setUrl("http://hijiribe.donmai.us/")
			.setPostsApi("/posts.json")
			.setPoolsApi("/pools.json")
			.setCommentsApi("/comments.json")
			.setNotesApi("/notes.json")
			.setArtistsApi("/artists.json")
			.setTagsApi("/tags.json"));
		
		db.add(new Site()
			//.setId(3)
			.setIconid(R.drawable.icon_behoimi)
			.setName("3DBooru (behoimi)")
			.setType(Type.Danbooru1)
			.setUrl("http://behoimi.org/")
			.setPostsApi("/post/index.json")
			.setPoolsApi("/pool/index.json")
			.setCommentsApi("/comment/index.json")
			.setNotesApi("/note/index.json")
			.setArtistsApi("/artist/index.json")
			.setTagsApi("/tag/index.json"));
		
		db.add(new Site()
			//.setId(4)
			.setIconid(R.drawable.icon_yande_re)
			.setName("Yande.re")
			.setType(Type.Danbooru1)
			.setUrl("https://yande.re/")
			.setPostsApi("/post/index.json")
			.setPoolsApi("/pool/index.json")
			.setCommentsApi("/comment/index.json")
			.setNotesApi("/note/index.json")
			.setArtistsApi("/artist/index.json")
			.setTagsApi("/tag/index.json"));
	}
	
	public static boolean getBoolean(int id)
	{
		return getBoolean(id, false);
	}
	
	public static boolean getBoolean(int id, boolean def)
	{
		Context context = CartonBox.getInstance();
		String key = context.getString(id);
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, def);
	}
	
	public static String getString(int id, String def)
	{
		Context context = CartonBox.getInstance();
		String key = context.getString(id);
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, def);
	}
	
	public static int getInt(int id, int def)
	{
		Context context = CartonBox.getInstance();
		String key = context.getString(id);
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, def);
	}
}
