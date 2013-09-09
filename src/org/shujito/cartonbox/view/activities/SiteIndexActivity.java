package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.view.adapters.SiteIndexPageAdapter;
import org.shujito.cartonbox.view.fragments.SectionPostsFragment;
import org.shujito.cartonbox.view.fragments.SectionTagsFragment;
import org.shujito.cartonbox.view.fragments.dialogs.LoginDialogFragment;
import org.shujito.cartonbox.view.fragments.dialogs.LoginDialogFragment.LoginDialogCallback;
import org.shujito.cartonbox.view.listeners.OnFragmentAttachedListener;
import org.shujito.cartonbox.view.listeners.TagListItemSelectedCallback;

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
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SiteIndexActivity extends SherlockFragmentActivity implements
	OnPageChangeListener, TabListener, OnFragmentAttachedListener,
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
		
		// TODO: dialog saying "give rating!"
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Give rating!");
		dialog.setMessage("You have been using this app for some time, would you like to give a rating?");
		dialog.setPositiveButton(android.R.string.ok, null);
		dialog.setNegativeButton("Later", null);
		//dialog.show();
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
				int currentPage = this.mVpSections.getCurrentItem();
				if(currentPage == this.findPage(R.string.section_tags))
				{
					String backupQuery = this.mTagsApi.getQuery();
					this.mTagsApi.clear();
					this.mTagsApi.setQuery(backupQuery);
					this.mTagsApi.request();
				}
				if(currentPage == this.findPage(R.string.section_posts))
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
		// select the tab
		this.getSupportActionBar().getTabAt(pos).select();
	}
	/* OnPageChangeListener methods */
	
	/* TabListener methods */
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
	{
		Logger.i("SiteIndexActivity::onTabSelected", tab.toString());
		// move to page
		this.mVpSections.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
	{
		Logger.i("SiteIndexActivity::onTabUnselected", tab.toString());
	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
	{
		Logger.i("SiteIndexActivity::onTabReselected", tab.toString());
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
		//Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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