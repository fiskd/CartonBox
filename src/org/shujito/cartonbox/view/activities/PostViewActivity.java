package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.Imageboard;
import org.shujito.cartonbox.view.adapters.PostsPagerAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PostViewActivity extends SherlockFragmentActivity
	implements OnPageChangeListener
{
	public static String EXTRA_POST_INDEX = "org.shujito.cartonbox.POST_INDEX";
	
	Imageboard api = null;
	
	ViewPager mVpPosts = null;
	PostsPagerAdapter mPostsAdapter = null;
	boolean
		bChildren,
		bParent,
		bPools;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.posts_view);
		
		this.mPostsAdapter = new PostsPagerAdapter(this.getSupportFragmentManager());
		
		this.mVpPosts = (ViewPager)this.findViewById(R.id.postview_vpposts);
		this.mVpPosts.setAdapter(this.mPostsAdapter);
		this.mVpPosts.setOnPageChangeListener(this);
		
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		CartonBox carton = (CartonBox)this.getApplication();
		this.api = carton.getImageboard();
		
		if(this.api == null)
			this.finish();
		
		this.api.addOnPostsFetchedListener(this.mPostsAdapter);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// XXX: hacky...
		if(this.mPostsAdapter != null)
			this.mPostsAdapter.onPostsFetched(this.api);
		
		int page = this.getIntent().getIntExtra(EXTRA_POST_INDEX, 0);
		this.mVpPosts.setCurrentItem(page);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		this.getIntent().putExtra(EXTRA_POST_INDEX, this.mVpPosts.getCurrentItem());
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		// remove this listener
		this.api.removeOnPostsFetchedListener(this.mPostsAdapter);
		
		CartonBox carton = (CartonBox)this.getApplication();
		carton.setImageboard(this.api);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			// handle the event
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.getSupportMenuInflater().inflate(R.menu.postview, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// DEMO: hide or show optionsmenu items
		menu.findItem(R.id.menu_postview_viewchildren).setVisible(this.bChildren);
		menu.findItem(R.id.menu_postview_viewparent).setVisible(this.bParent);
		menu.findItem(R.id.menu_postview_viewpools).setVisible(this.bPools);
		return true;
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
		// DEMO: hide or show optionsmenu items
		//this.bChildren = (pos % 2) == 0;
		//this.bParent = (pos % 3) == 0;
		//this.bPools = (pos % 4) == 0;
	}
	/* OnPageChangeListener methods */
}
