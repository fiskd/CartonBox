package org.shujito.cartonbox.view.activities;

import java.util.List;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.DanbooruImageBoard;
import org.shujito.cartonbox.controller.DanbooruOldImageBoard;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;
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
		
		/*
		this.mGridView = new GridView(this);
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.mGridView.setLayoutParams(lparams);
		this.mGridView.setGravity(Gravity.CENTER);
		this.mGridView.setNumColumns(2);
		this.mGridView.setAdapter(this.mSitesAdapter);
		this.mGridView.setOnItemClickListener(this);
		
		this.setContentView(this.mGridView);
		//*/
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// get rid of the api in the application
		CartonBox.getInstance().setImageboard(null);
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
		
		// create api here
		ImageboardPosts postsApi = null; // new DanbooruImageBoard(currentSite);
		
		
		if(currentSite.getType() == Site.Type.Danbooru1)
			postsApi = new DanbooruOldImageBoard(currentSite);
		else if(currentSite.getType() == Site.Type.Danbooru2)
		{
			SharedPreferences sitePrefs = this.getSharedPreferences(String.valueOf(currentSite.getId()), 0);
			String username = sitePrefs.getString(Preferences.SITE_USERNAME, null);
			String password = sitePrefs.getString(Preferences.SITE_PASSWORD, null);
			
			SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			boolean bShowSafe = globalPrefs.getBoolean(this.getString(R.string.pref_ratings_todisplay_safe_key), true);
			boolean bShowQuestionable = globalPrefs.getBoolean(this.getString(R.string.pref_ratings_todisplay_questionable_key), false);
			boolean bShowExplicit = globalPrefs.getBoolean(this.getString(R.string.pref_ratings_todisplay_explicit_key), false);
			
			postsApi = new DanbooruImageBoard(currentSite);
			postsApi.setUsername(username);
			postsApi.setPassword(password);
			postsApi.setPostsPerPage(20);
			postsApi.setShowSafePosts(bShowSafe);
			postsApi.setShowQuestionablePosts(bShowQuestionable);
			postsApi.setShowExplicitPosts(bShowExplicit);
		}
		
		if(postsApi != null)
		{
			// place it on the application
			CartonBox.getInstance().setImageboard(postsApi);
			Intent ntn = new Intent(this, SiteIndexActivity.class);
			ntn.putExtra(SiteIndexActivity.EXTRA_SECTIONPAGE, R.string.section_posts);
			this.startActivity(ntn);
		}
	}
	/* OnItemClickListener methods */
}
