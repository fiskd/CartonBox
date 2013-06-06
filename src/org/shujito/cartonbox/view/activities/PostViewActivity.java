package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.model.Post;
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
	
	ImageboardPosts postsApi = null;
	
	ViewPager mVpPosts = null;
	PostsPagerAdapter mPostsAdapter = null;
	
	MenuItem
		itemViewChildren,
		itemViewParent,
		itemViewPools;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.posts_view);
		
		this.mPostsAdapter = new PostsPagerAdapter(this.getSupportFragmentManager());
		
		this.mVpPosts = (ViewPager)this.findViewById(R.id.postview_vpposts);
		this.mVpPosts.setAdapter(this.mPostsAdapter);
		
		// enable this so we can navigate with the up button
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// get the api from the context (handy, but hacky, should have made this a singleton? nah...)
		//this.postsApi = CartonBox.getInstance().getImageboard();
		
		if(CartonBox.getInstance().getApis() != null)
			this.postsApi = CartonBox.getInstance().getApis().getImageboardPosts();
		// no api, no job
		if(this.postsApi == null)
			this.finish();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// hey listen! (the adapter will listen when the api fetches posts)
		this.postsApi.addOnPostsFetchedListener(this.mPostsAdapter);
		// XXX: hacky...
		if(this.mPostsAdapter != null)
			this.mPostsAdapter.onPostsFetched(this.postsApi.getPosts());
		
		int page = this.getIntent().getIntExtra(EXTRA_POST_INDEX, 0);
		this.mVpPosts.setOnPageChangeListener(this);
		this.mVpPosts.setCurrentItem(page);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		this.getIntent().putExtra(EXTRA_POST_INDEX, this.mVpPosts.getCurrentItem());
		// remove this listener
		this.postsApi.removeOnPostsFetchedListener(this.mPostsAdapter);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// use it or lose it...
		this.postsApi = null;
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
			case R.id.menu_postview_save:
				return true;
			case R.id.menu_postview_preferences:
				return true;
			case R.id.menu_postview_details:
				return true;
			case R.id.menu_postview_browser:
				return true;
			case R.id.menu_postview_viewparent:
				return true;
			case R.id.menu_postview_viewchildren:
				return true;
			case R.id.menu_postview_viewpools:
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// XXX: woes!: for whatever reason, when changing orientation from
		// portrait to landscape, the context menu entries disappear
		// (parent, child, etc)
		// XXX: got it: onPrepareOptionsMenu doesn't run the first time the
		// button is pressed
		this.getSupportMenuInflater().inflate(R.menu.postview, menu);
		
		this.itemViewChildren = menu.findItem(R.id.menu_postview_viewchildren).setVisible(false);
		this.itemViewParent = menu.findItem(R.id.menu_postview_viewparent).setVisible(false);
		this.itemViewPools = menu.findItem(R.id.menu_postview_viewpools).setVisible(false);
		
		// XXX: I had to...
		// this refreshes visible menu items
		int page = this.getIntent().getIntExtra(EXTRA_POST_INDEX, 0);
		this.onPageSelected(page);
		
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
		// reverse index (how could I forget about this)
		int size = this.postsApi.getPosts().size();
		int index = size - pos - 1;
		int key = this.postsApi.getPosts().keyAt(index);
		Post one = this.postsApi.getPosts().get(key);
		Logger.i("PostViewActivity::onPageSelected", String.valueOf(one.getId()));
		
		// now this works, I'm happy again (:
		// now improved
		if(this.itemViewChildren != null)
			this.itemViewChildren.setVisible(one.isHasChildren());
		if(this.itemViewParent != null)
			this.itemViewParent.setVisible(one.getParentId() > 0);
		
		// oh hey it became natural! and I didn't had to do much!
		if(pos + 1 >= size)
		{
			this.postsApi.request();
		}
	}
	/* OnPageChangeListener methods */
}
