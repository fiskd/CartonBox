package org.shujito.cartonbox.view.activity;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listener.OnErrorListener;
import org.shujito.cartonbox.util.io.ScheduledCachePurger;
import org.shujito.cartonbox.view.adapter.SiteIndexPageAdapter;
import org.shujito.cartonbox.view.fragment.SectionPostsFragment;
import org.shujito.cartonbox.view.fragment.SectionTagsFragment;
import org.shujito.cartonbox.view.fragment.dialog.LoginDialogFragment;
import org.shujito.cartonbox.view.fragment.dialog.LoginDialogFragment.LoginDialogCallback;
import org.shujito.cartonbox.view.listener.OnFragmentAttachedListener;
import org.shujito.cartonbox.view.listener.TagListItemSelectedCallback;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SiteIndexActivity extends SherlockFragmentActivity implements
	OnPageChangeListener, ActionBar.TabListener, OnFragmentAttachedListener,
	TagListItemSelectedCallback, OnErrorListener, LoginDialogCallback
{
	public static String EXTRA_SECTIONPAGE = "org.shujito.cartonbox.SECTIONPAGE";
	public static String EXTRA_DIALOGSHOWING = "org.shujito.cartonbox.DIALOGSHOWING";
	
	SiteIndexPageAdapter mPageAdapter = null;
	ViewPager mVpSections = null;
	// tab titles
	String[] tabs = null;
	// retain queries here
	ImageboardPosts mPostsApi = null;
	ImageboardTags mTagsApi = null;
	//
	boolean dialogShowing = false;
	// purges cache every X time
	ScheduledCachePurger schedPurge = null;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.siteindex);
		
		this.tabs = this.getResources().getStringArray(R.array.danbooru_sections);
		
		this.mPageAdapter = new SiteIndexPageAdapter(this.getSupportFragmentManager(), this, this.tabs);
		this.mVpSections = (ViewPager)this.findViewById(R.id.vpSections);
		this.mVpSections.setAdapter(this.mPageAdapter);
		this.mVpSections.setOnPageChangeListener(this);
		
		this.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		for(int idx = 0; idx < tabs.length; idx++)
		{
			ActionBar.Tab tab = this.getSupportActionBar().newTab();
			tab.setText(tabs[idx]);
			tab.setTabListener(this);
			this.getSupportActionBar().addTab(tab);
		}
		
		int sectionid = this.getIntent().getIntExtra(EXTRA_SECTIONPAGE, 0);
		int page = this.findPage(sectionid);
		if(page > 0)
			this.mVpSections.setCurrentItem(page);
		
		this.schedPurge = new ScheduledCachePurger();
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			// get what we want to search for...
			String searchQuery = intent.getStringExtra(SearchManager.QUERY);
			
			// let's do the search here
			if(searchQuery != null)
			{
				this.getSupportActionBar().setSubtitle(searchQuery);
				//Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
				// find current section page, do searches depending on the page
				int currentPage = this.mVpSections.getCurrentItem();
				if(currentPage == this.findPage(R.string.section_tags))
				{
					if(this.mTagsApi != null)
					{
						this.mTagsApi.clear();
						this.mTagsApi.setQuery(searchQuery);
						this.mTagsApi.request();
					}
				}
				else if(currentPage == this.findPage(R.string.section_posts))
				{
					if(this.mPostsApi != null)
					{
						this.mPostsApi.clear();
						String[] tags = searchQuery.split("\\s+");
						for(String tag : tags)
						{
							this.mPostsApi.putTag(tag);
						}
						this.mPostsApi.request();
					}
				}
			}
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Logger.i("SiteIndexActivity::onResume", "activity resumed");
		
		// get the imageboard
		if(this.mPostsApi == null)
			this.mPostsApi = CartonBox.getInstance().getApis().getImageboardPosts();
		if(this.mTagsApi == null)
			this.mTagsApi = CartonBox.getInstance().getApis().getImageboardTags();
		
		this.getSupportActionBar().setTitle(this.mPostsApi.getSite().getName());
		
		if(this.mPostsApi == null || this.mTagsApi == null)
		{
			// close the activity
			this.finish();
		}
		else
		{
			this.mPostsApi.addOnErrorListener(this);
			this.mTagsApi.addOnErrorListener(this);
			
			// retrieve dialog showing
			this.dialogShowing = this.getIntent().getBooleanExtra(EXTRA_DIALOGSHOWING, false);
		}
		
		if(this.mPostsApi != null)
		{
			String[] alltags = this.mPostsApi.getTags();
			if(alltags.length > 0)
			{
				StringBuilder builder = new StringBuilder();
				for(String tag : alltags)
				{
					builder.append(tag);
					builder.append(" ");
				}
				this.getSupportActionBar().setSubtitle(builder.toString());
			}
		}
		// check if the user had already rated the app
		boolean rated = Preferences.getBool(R.string.pref_app_rated, false);
		// check how many times has the user visited this activity
		int timesVisited = Preferences.getInt(R.string.pref_times_index_visited);
		// increase
		Preferences.setInt(R.string.pref_times_index_visited, timesVisited + 1);
		// more than ten?
		if(!rated && timesVisited > 10)
		{
			//Preferences.setBool(R.string.pref_app_rated, true);
			// reset
			Preferences.setInt(R.string.pref_times_index_visited, 0);
			// start creating the dialog
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.ratingdialogtitle);
			dialog.setMessage(R.string.ratingdialogmessage);
			// TODO: events
			dialog.setPositiveButton(android.R.string.yes, null);
			dialog.setNegativeButton(R.string.later, null);
			dialog.show();
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Logger.i("SiteIndexActivity::onPause", "activity paused");
		// save dialog showing
		this.getIntent().putExtra(EXTRA_DIALOGSHOWING, this.dialogShowing);
		
		if(this.mPostsApi != null)
			this.mPostsApi.removeOnErrorListener(this);
		if(this.mTagsApi != null)
			this.mTagsApi.removeOnErrorListener(this);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.schedPurge.stop();
		// get rid of this
		this.mPostsApi = null;
		this.mTagsApi = null;
		Logger.i("SiteIndexActivity::onDestroy", "activity destroyed");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
 	{
		this.getSupportMenuInflater().inflate(R.menu.siteindex, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				Intent ntnMain = new Intent(this, MainActivity.class);
				ntnMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(ntnMain);
				//this.finish();
				// event handled
				return true;
			case R.id.search:
				// search dialog
				this.onSearchRequested();
				return true;
			case R.id.refresh:
				if(this.mVpSections.getCurrentItem() == this.findPage(R.string.section_tags))
				{
					String backupQuery = this.mTagsApi.getQuery();
					this.mTagsApi.clear();
					this.mTagsApi.setQuery(backupQuery);
					this.mTagsApi.request();
				}
				if(this.mVpSections.getCurrentItem() == this.findPage(R.string.section_posts))
				{
					String[] alltags = this.mPostsApi.getTags();
					this.mPostsApi.clear();
					// put the tags back again...
					for(String tag : alltags)
					{
						this.mPostsApi.putTag(tag);
					}
					this.mPostsApi.request();
				}
				break;
			case R.id.reset:
				this.mTagsApi.clear();
				this.mTagsApi.request();
				this.mPostsApi.clear();
				this.mPostsApi.request();
				this.getSupportActionBar().setSubtitle(null);
				this.mVpSections.setCurrentItem(this.findPage(R.string.section_posts));
				return true;
			case R.id.settings:
				Intent ntnPrefs = new Intent(this, GeneralPreferencesActivity.class);
				this.startActivity(ntnPrefs);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/* OnPageChangeListener methods */
	@Override
	public void onPageScrollStateChanged(int state)
	{
	}
	
	@Override
	public void onPageScrolled(int pos, float offset, int pixels)
	{
	}
	
	@Override
	public void onPageSelected(int pos)
	{
		//this.getSupportActionBar().getTabAt(pos).select();
		this.getSupportActionBar().setSelectedNavigationItem(pos);
	}
	/* OnPageChangeListener methods */
	
	/* TabListener methods */
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
	{
		//Logger.i("SiteIndexActivity::onTabUnselected", String.format("unsel %s", tab.getPosition()));
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
	{
		//Logger.i("SiteIndexActivity::onTabSelected", String.format("sel %s", tab.getPosition()));
		this.mVpSections.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
	{
		//Logger.i("SiteIndexActivity::onTabReselected", String.format("resel %s", tab.getPosition()));
		if(tab.getPosition() == this.findPage(R.string.section_posts))
		{
			// TODO: scroll to top
		}
	}
	/* TabListener methods */
	
	/* OnFragmentAttachedListener methods */
	@Override
	public Object onFragmentAttached(Fragment f)
	{
		Logger.i("SiteIndexActivity::onFragmentAttached", String.format("fragment %s attached", f.toString()));
		int section = 0;
		if(f instanceof SectionPostsFragment)
		{
			section = R.string.section_posts;
			// this should fix the NPE caused when rotating the device
			if(this.mPostsApi == null)
				this.mPostsApi = CartonBox.getInstance().getApis().getImageboardPosts();
			return this.mPostsApi;
		}
		
		if(f instanceof SectionTagsFragment)
		{
			section = R.string.section_tags;
			if(this.mTagsApi == null)
				this.mTagsApi = CartonBox.getInstance().getApis().getImageboardTags();
			return this.mTagsApi;
		}
		
		ActionBar.Tab tab = this.getSupportActionBar().getTabAt(this.findPage(section));
		tab.setTag(f);
		
		return null;
	}
	/* OnFragmentAttachedListener methods */
	
	/* TagListItemSelectedCallback methods */
	@Override
	public void tagListItemSelected(String tag)
	{
		this.mPostsApi.clear();
		this.mPostsApi.putTag(tag);
		this.mPostsApi.request();
		int page = this.findPage(R.string.section_posts);
		if(page > 0)
			this.mVpSections.setCurrentItem(page);
		
		this.getSupportActionBar().setSubtitle(tag);
	}
	/* TagListItemSelectedCallback methods */
	
	/* OnErrorListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		//Toast.makeText(this, String.format("%s: %s", errCode, message), Toast.LENGTH_LONG).show();
		if(errCode == 403)
		{
			if(!this.dialogShowing)
			{
				Logger.e("SiteIndexActivity::onError", message);
				LoginDialogFragment login = new LoginDialogFragment();
				login.show(this.getSupportFragmentManager(), LoginDialogFragment.TAG);
				this.dialogShowing = true;
			}
		}
	}
	/* OnErrorListener methods */
	
	/* LoginDialogCallback methods */
	@Override
	public void onFinishEditLogin(String username, String password)
	{
		//Toast.makeText(this, String.format("username:%s password:%s", username, password), Toast.LENGTH_SHORT).show();
		if(this.mPostsApi != null)
		{
			this.mPostsApi.setUsername(username);
			this.mPostsApi.setPassword(password);
			this.mPostsApi.request();
			
			this.mTagsApi.setUsername(username);
			this.mTagsApi.setPassword(password);
			this.mTagsApi.request();
			
			String prefsName = String.valueOf(this.mPostsApi.getSite().getId());
			
			SharedPreferences sitePrefs = this.getSharedPreferences(prefsName, 0);
			
			SharedPreferences.Editor sitePrefsEdit = sitePrefs.edit();
			sitePrefsEdit.putString(Preferences.SITE_USERNAME, username);
			sitePrefsEdit.putString(Preferences.SITE_PASSWORD, password);
			sitePrefsEdit.commit();
			this.dialogShowing = false;
		}
	}
	
	@Override
	public void onCancel()
	{
		// you can't login or don't want to login, no index for you then...
		this.finish();
	}
	/* LoginDialogCallback methods */
	
	protected int findPage(int resid)
	{
		String sectionString = null;
		try
		{
			sectionString = this.getResources().getString(resid);
		}
		catch(Exception e)
		{
			return -1; // nothing
		}
		
		for(int idx = 0; idx < this.tabs.length; idx++)
		{
			if(sectionString.equals(this.tabs[idx]))
			{
				return idx;
			}
		}
		
		return -1;
	}
}