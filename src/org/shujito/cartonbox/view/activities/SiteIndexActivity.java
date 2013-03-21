package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.Danbooru2ImageBoard;
import org.shujito.cartonbox.controller.Imageboard;
import org.shujito.cartonbox.view.adapters.SiteIndexPageAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;

public class SiteIndexActivity extends SherlockFragmentActivity implements
	OnPageChangeListener, TabListener, OnActionExpandListener, OnEditorActionListener
{
	public static String EXTRA_SITEURL = "org.shujito.cartonbox.SITEURL";
	public static String EXTRA_USERNAME = "org.shujito.cartonbox.USERNAME";
	public static String EXTRA_PASSWORD = "org.shujito.cartonbox.PASSWORD";
	public static String EXTRA_SECTIONPAGE = "org.shujito.cartonbox.SECTIONPAGE";
	
	SiteIndexPageAdapter mPageAdapter = null;
	ViewPager mVpSections = null;
	MenuItem mMenuItemSearch = null;
	MultiAutoCompleteTextView mMactvQueryPosts = null;
	
	String[] tabs = null;
	Imageboard api = null;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.siteindex);
		
		String siteUrl = this.getIntent().getStringExtra(EXTRA_SITEURL);
		//String username = this.getIntent().getStringExtra(EXTRA_USERNAME);
		//String password = this.getIntent().getStringExtra(EXTRA_PASSWORD);
		
		this.tabs = this.getResources().getStringArray(R.array.danbooru_sections);
		this.mPageAdapter = new SiteIndexPageAdapter(this.getSupportFragmentManager(), this);
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
		
		int page = 0;
		int sectionid = this.getIntent().getIntExtra(EXTRA_SECTIONPAGE, 0);
		
		if(sectionid != 0)
		{
			String sectionstring = this.getResources().getString(sectionid);
			for(int idx = 0; idx < this.tabs.length; idx++)
			{
				if(sectionstring.equals(this.tabs[idx]))
				{
					page = idx;
					break;
				}
			}
			
			this.mVpSections.setCurrentItem(page);
		}
		//String url = this.getIntent().getStringExtra(EXTRA_SITEURL);
		//Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
		
		this.api = new Danbooru2ImageBoard(siteUrl); // TODO: new
		//this.api.setUsername(null);
		//this.api.setPasswordHash(null);
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.api = null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
 	{
		this.getSupportMenuInflater().inflate(R.menu.siteindex, menu);
		
		this.mMenuItemSearch = menu.findItem(R.id.menu_siteindex_search);
		this.mMenuItemSearch.setOnActionExpandListener(this);
		
		this.mMactvQueryPosts = (MultiAutoCompleteTextView)this.mMenuItemSearch
				.getActionView()
				.findViewById(R.id.actionsearch_mactvqueryposts);
		this.mMactvQueryPosts.setOnEditorActionListener(this);
		
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
			/* what to put here? */
			return true;
		case R.id.menu_siteindex_settings:
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
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		// collapse search view
		if(this.mMenuItemSearch != null)
			this.mMenuItemSearch.collapseActionView();
		
		// clear text
		if(this.mMactvQueryPosts != null)
			this.mMactvQueryPosts.setText(null);
		
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
	}
	/* TabListener methods */
	
	/* OnActionExpandListener methods */
	@Override
	public boolean onMenuItemActionCollapse(MenuItem item)
	{
		InputMethodManager imm = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.mMactvQueryPosts.getWindowToken(), 0);
		return true;
	}
	
	@Override
	public boolean onMenuItemActionExpand(MenuItem item)
	{
		if(this.mMactvQueryPosts != null)
			this.mMactvQueryPosts.post(new Runnable()
			{
				@Override
				public void run()
				{
					mMactvQueryPosts.requestFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					imm.showSoftInput(mMactvQueryPosts, InputMethodManager.SHOW_IMPLICIT);
				}
			});
		
		// should be true, or else the item won't expand
		return true;
	}
	/* OnActionExpandListener methods */
	
	/* OnEditorActionListener methods */
	@Override
	public boolean onEditorAction(TextView v, int action, KeyEvent e)
	{
		if(action == EditorInfo.IME_ACTION_SEARCH)
		{
			Toast.makeText(this, v.getText().toString(), Toast.LENGTH_SHORT).show();
			if(this.api != null)
			{
				// TODO: api things
				this.api.clear();
				String[] tags = v.getText().toString().split("\\s+");
				for(String tag : tags)
				{
					this.api.putTag(tag);
				}
				this.api.requestPosts();
			}
			return true;
		}
		return false;
	}
	/* OnEditorActionListener methods */
}
