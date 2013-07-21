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

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
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
	//MenuItem mMenuItemSearch = null;
	//MultiAutoCompleteTextView mMactvQueryPosts = null;
	//ImageButton mBtnClearQuery = null;
	// tab titles
	String[] tabs = null;
	// retain queries here
	String[] queries = null;
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
		// create with same amount of tabs
		this.queries = new String[this.tabs.length];
		
		this.mPageAdapter = new SiteIndexPageAdapter(this.getSupportFragmentManager(), this, this.tabs);
		this.mVpSections = (ViewPager)this.findViewById(R.id.siteindex_vpsections);
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
		//Intent intent = this.getIntent();
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			this.getSupportActionBar().setSubtitle(query);
			//Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
			// find current section page, do searches depending on the page
			int currentPage = this.mVpSections.getCurrentItem();
			if(currentPage == this.findPage(R.string.section_tags))
			{
				if(this.mTagsApi != null)
				{
					this.mTagsApi.clear();
					this.mTagsApi.setQuery(query);
					this.mTagsApi.request();
				}
			}
			else if(currentPage == this.findPage(R.string.section_posts))
			{
				if(this.mPostsApi != null)
				{
					this.mPostsApi.clear();
					String[] tags = query.split("\\s+");
					for(String tag : tags)
					{
						this.mPostsApi.putTag(tag);
					}
					this.mPostsApi.request();
				}
			}
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
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
		}
		// retrieve dialog showing
		this.dialogShowing = this.getIntent().getBooleanExtra(EXTRA_DIALOGSHOWING, false);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
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
			case R.id.menu_siteindex_search:
				// search dialog
				this.onSearchRequested();
				return true;
			case R.id.menu_siteindex_refresh:
				/*
				this.mPostsApi.clear();
				this.mPostsApi.request();
				int page = this.findPage(R.string.section_posts);
				this.mVpSections.setCurrentItem(page);
				//*/
				this.getSupportActionBar().setSubtitle(null);
				int currentPage = this.mVpSections.getCurrentItem();
				if(currentPage == this.findPage(R.string.section_tags))
				{
					this.mTagsApi.clear();
					this.mTagsApi.request();
				}
				if(currentPage == this.findPage(R.string.section_posts))
				{
					this.mPostsApi.clear();
					this.mPostsApi.request();
				}
				return true;
			case R.id.menu_siteindex_settings:
				//Intent ntnPrefs = new Intent(this, GeneralPreferencesActivity.class);
				//this.startActivity(ntnPrefs);
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
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		// move to page
		this.mVpSections.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
		// this might not be used
	}
	/* TabListener methods */
	
	/* OnFragmentAttachedListener methods */
	@Override
	public Object onFragmentAttached(Fragment f)
	{
		if(f instanceof SectionPostsFragment)
		{
			// this should fix the NPE caused when rotating the device
			if(this.mPostsApi == null)
				this.mPostsApi = CartonBox.getInstance().getApis().getImageboardPosts();
			return this.mPostsApi;
		}
		
		if(f instanceof SectionTagsFragment)
		{
			if(this.mTagsApi == null)
				this.mTagsApi = CartonBox.getInstance().getApis().getImageboardTags();
			return this.mTagsApi;
		}
		
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