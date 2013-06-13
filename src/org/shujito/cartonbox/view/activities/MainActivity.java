package org.shujito.cartonbox.view.activities;

import java.util.List;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.DanbooruImageBoardPosts;
import org.shujito.cartonbox.controller.DanbooruImageBoardTags;
import org.shujito.cartonbox.controller.DanbooruOldImageBoardPosts;
import org.shujito.cartonbox.controller.DanbooruOldImageBoardTags;
import org.shujito.cartonbox.controller.ImageboardApis;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;
import org.shujito.cartonbox.utils.ImageDownloader;
import org.shujito.cartonbox.utils.Preferences;
import org.shujito.cartonbox.view.adapters.SitesAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements OnItemClickListener
{
	GridView mGridView = null;
	SitesAdapter mSitesAdapter = null;
	List<Site> sites = null;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.main);
		
		// get sites stored on the db
		SitesDB sitesdb = new SitesDB(this);
		this.sites = sitesdb.getAll();
		
		// init views
		this.mSitesAdapter = new SitesAdapter(this, this.sites);
		this.mGridView = (GridView)this.findViewById(R.id.main_gvsites);
		this.mGridView.setAdapter(this.mSitesAdapter);
		this.mGridView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// get rid of the apis in the application
		if(CartonBox.getInstance() != null && CartonBox.getInstance().getApis() != null)
		{
			CartonBox.getInstance().getApis().setImageboardPosts(null);
			CartonBox.getInstance().getApis().setImageboardTags(null);
			CartonBox.getInstance().setApis(null);
		}
		
		// initial prefs
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean bCacheExternal = globalPrefs.getBoolean(this.getString(R.string.pref_general_cacheexternal_key), false);
		ImageDownloader.setCachingToExternal(bCacheExternal);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menu_main_addsite:
				return true;
			case R.id.menu_main_settings:
				Intent ntnPrefs = new Intent(this, GeneralPreferencesActivity.class);
				this.startActivity(ntnPrefs);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/* OnItemClickListener methods */
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
		Site currentSite = this.sites.get(pos);
		
		// create apis here
		ImageboardApis apis = new ImageboardApis();
		ImageboardPosts postsApi = null;
		ImageboardTags tagsApi = null;
		
		// place it on the application
		CartonBox.getInstance().setApis(apis);
		
		// prefs!
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean bShowSafe = globalPrefs.getBoolean(this.getString(R.string.pref_ratings_todisplay_safe_key), true);
		boolean bShowQuestionable = globalPrefs.getBoolean(this.getString(R.string.pref_ratings_todisplay_questionable_key), false);
		boolean bShowExplicit = globalPrefs.getBoolean(this.getString(R.string.pref_ratings_todisplay_explicit_key), false);
		
		String sPostsPerPage = globalPrefs.getString(this.getString(R.string.pref_content_postsperpage_key), "20");
		int postsPerPage = Integer.parseInt(sPostsPerPage);
		
		//globalPrefs.getInt(this.getString(R.string.pref_content_postsperpage_key), 20);
		//int poolsPerPage = globalPrefs.getInt(this.getString(R.string.pref_content_poolsperpage_key), 20);
		//int poolPostsPerPage = globalPrefs.getInt(this.getString(R.string.pref_content_poolpostsperpage_key), 20);
		
		if(currentSite.getType() == Site.Type.Danbooru1)
		{
			postsApi = new DanbooruOldImageBoardPosts(currentSite);
			tagsApi =new DanbooruOldImageBoardTags(currentSite);
		}
		else if(currentSite.getType() == Site.Type.Danbooru2)
		{
			SharedPreferences sitePrefs = this.getSharedPreferences(String.valueOf(currentSite.getId()), 0);
			String username = sitePrefs.getString(Preferences.SITE_USERNAME, null);
			String password = sitePrefs.getString(Preferences.SITE_PASSWORD, null);
			
			postsApi = new DanbooruImageBoardPosts(currentSite);
			postsApi.setUsername(username);
			postsApi.setPassword(password);
			postsApi.setPostsPerPage(postsPerPage);
			postsApi.setShowSafePosts(bShowSafe);
			postsApi.setShowQuestionablePosts(bShowQuestionable);
			postsApi.setShowExplicitPosts(bShowExplicit);
			
			tagsApi = new DanbooruImageBoardTags(currentSite);
			tagsApi.setUsername(username);
			tagsApi.setPassword(password);
		}
		
		// set apis on the apis class
		if(postsApi != null)
			apis.setImageboardPosts(postsApi);
		if(tagsApi != null)
			apis.setImageboardTags(tagsApi);
		
		Intent ntn = new Intent(this, SiteIndexActivity.class);
		ntn.putExtra(SiteIndexActivity.EXTRA_SECTIONPAGE, R.string.section_posts);
		this.startActivity(ntn);
	}
	/* OnItemClickListener methods */
}
