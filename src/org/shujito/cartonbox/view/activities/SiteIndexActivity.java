package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnFragmentAttachedListener;
import org.shujito.cartonbox.utils.Preferences;
import org.shujito.cartonbox.view.adapters.SiteIndexPageAdapter;
import org.shujito.cartonbox.view.fragments.LoginDialogFragment;
import org.shujito.cartonbox.view.fragments.LoginDialogFragment.LoginDialogCallback;
import org.shujito.cartonbox.view.fragments.PostsSectionFragment;
import org.shujito.cartonbox.view.fragments.TagsSectionFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;

public class SiteIndexActivity extends SherlockFragmentActivity implements
	OnPageChangeListener, TabListener, OnActionExpandListener, OnEditorActionListener,
	OnFragmentAttachedListener, OnErrorListener, LoginDialogCallback
{
	public static String EXTRA_SECTIONPAGE = "org.shujito.cartonbox.SECTIONPAGE";
	
	SiteIndexPageAdapter mPageAdapter = null;
	ViewPager mVpSections = null;
	MenuItem mMenuItemSearch = null;
	MultiAutoCompleteTextView mMactvQueryPosts = null;
	// tab titles
	String[] tabs = null;
	ImageboardPosts postsApi = null;
	ImageboardTags tagsApi = null;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.siteindex);
		
		//String siteUrl = this.getIntent().getStringExtra(EXTRA_SITEURL);
		//String username = this.getIntent().getStringExtra(EXTRA_USERNAME);
		//String password = this.getIntent().getStringExtra(EXTRA_PASSWORD);
		//Site site = (Site)this.getIntent().getSerializableExtra(EXTRA_SITE);
		
		this.tabs = this.getResources().getStringArray(R.array.danbooru_sections);
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
		
		//Site site = new Site()
			//.setUrl("http://danbooru.donmai.us")
			//.setPostsApi("%s/posts.json?");
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// get the imageboard
		if(this.postsApi == null)
			this.postsApi = CartonBox.getInstance().getApis().getImageboardPosts();
		
		if(this.postsApi == null)
		{
			// close the activity
			this.finish();
		}
		else
		{
			this.postsApi.addOnErrorListener(this);
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		this.postsApi.removeOnErrorListener(this);
		// TODO: save the query on the search box when changing to landscape/portrait
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// get rid of this
		this.postsApi = null;
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
		// TODO: save this search somewhere for later use (might fix task at line 133)
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
		// this might not be used
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
			// collapse search view
			if(this.mMenuItemSearch != null)
				this.mMenuItemSearch.collapseActionView();
			
			if(this.postsApi != null)
			{
				this.postsApi.clear();
				this.getSupportActionBar().setSubtitle(v.getText());
				String[] tags = v.getText().toString().split("\\s+");
				for(String tag : tags)
				{
					this.postsApi.putTag(tag);
				}
				this.postsApi.request();
			}
			return true;
		}
		return false;
	}
	/* OnEditorActionListener methods */
	
	/* OnFragmentAttachedListener methods */
	@Override
	public Object onFragmentAttached(Fragment f)
	{
		if(f instanceof PostsSectionFragment)
		{
			// this should fix the NPE caused when rotating the device
			if(this.postsApi == null)
				this.postsApi = CartonBox.getInstance().getApis().getImageboardPosts();
			return this.postsApi;
		}
		
		if(f instanceof TagsSectionFragment)
		{
		}
		
		return null;
	}
	/* OnFragmentAttachedListener methods */
	
	/* OnErrorListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		//Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		if(errCode == 403)
		{
			Logger.e("SiteIndexActivity::onError", message);
			LoginDialogFragment login = new LoginDialogFragment();
			login.show(this.getSupportFragmentManager(), LoginDialogFragment.TAG);
		}
	}
	/* OnErrorListener methods */
	
	/* LoginDialogCallback methods */
	@Override
	public void onFinishEditLogin(String username, String password)
	{
		//Toast.makeText(this, String.format("username:%s password:%s", username, password), Toast.LENGTH_SHORT).show();
		if(this.postsApi != null)
		{
			this.postsApi.setUsername(username);
			this.postsApi.setPassword(password);
			this.postsApi.request();
			
			String prefsName = String.valueOf(this.postsApi.getSite().getId());
			
			SharedPreferences sitePrefs = this.getSharedPreferences(prefsName, 0);
			
			SharedPreferences.Editor sitePrefsEdit = sitePrefs.edit();
			sitePrefsEdit.putString(Preferences.SITE_USERNAME, username);
			sitePrefsEdit.putString(Preferences.SITE_PASSWORD, password);
			sitePrefsEdit.commit();
		}
	}
	/* LoginDialogCallback methods */
}

