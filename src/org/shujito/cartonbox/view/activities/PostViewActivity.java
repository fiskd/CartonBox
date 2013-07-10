package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.adapters.PostsPagerAdapter;
import org.shujito.cartonbox.view.fragments.dialogs.PostTagsDialogFragment;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PostViewActivity extends SherlockFragmentActivity implements OnPageChangeListener
{
	public static String EXTRA_POST_INDEX = "org.shujito.cartonbox.POST_INDEX";
	public static String EXTRA_POST_KEY = "org.shujito.cartonbox.POST_KEY";
	
	ImageboardPosts postsApi = null;
	
	// current post
	Post selectedPost = null;
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
		// Even more HAX!!
		this.mPostsAdapter.onPostsFetched(this.postsApi.getPosts());
		
		//int page = this.getIntent().getIntExtra(EXTRA_POST_INDEX, 0);
		int key = this.getIntent().getIntExtra(EXTRA_POST_KEY, -1);
		int keypage = this.mPostsAdapter.findPageFromKey(key);
		
		//Toast.makeText(this, String.format("k:%s p:%s kp:%s", key, page, keypage), Toast.LENGTH_SHORT).show();
		this.mVpPosts.setOnPageChangeListener(this);
		this.mVpPosts.setCurrentItem(keypage);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		//this.getIntent().putExtra(EXTRA_POST_INDEX, this.mVpPosts.getCurrentItem());
		int page = this.mVpPosts.getCurrentItem();
		int key = (int)this.mPostsAdapter.getItemId(page);
		this.getIntent().putExtra(EXTRA_POST_KEY, key);
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
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				// handle the event
				return true;
			case R.id.menu_postview_save:
				// pan de gengibre?
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
				{
					Toast.makeText(this, this.getString(R.string.download_started), Toast.LENGTH_SHORT).show();
					DownloadManager downman = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);
					Uri uri = Uri.parse(this.selectedPost.getUrl());
					String filename = String.format("%s.%s", this.selectedPost.getMd5(), this.selectedPost.getFileExt());
					DownloadManager.Request request = new DownloadManager.Request(uri)
						.setAllowedOverRoaming(false)
						.setTitle(this.getString(R.string.downloading))
						.setDescription(filename)
						.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
					downman.enqueue(request);
				}
				else
				{
					Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				}
				
				return true;
			case R.id.menu_postview_preferences:
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_postview_details:
				PostTagsDialogFragment dialog = new PostTagsDialogFragment();
				// it's a pun!
				Bundle humble = new Bundle();
				dialog.setArguments(humble);
				dialog.show(this.getSupportFragmentManager(), PostTagsDialogFragment.TAG);
				return true;
			case R.id.menu_postview_browser:
				if(this.selectedPost != null)
				{
					String postUrl = this.selectedPost.toString();
					Uri uri = Uri.parse(postUrl);
					Intent site = new Intent(Intent.ACTION_VIEW, uri);
					this.startActivity(site);
				}
				return true;
			case R.id.menu_postview_viewparent:
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_postview_viewchildren:
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_postview_viewpools:
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// onPrepareOptionsMenu doesn't run the first time the button is pressed
		this.getSupportMenuInflater().inflate(R.menu.postview, menu);
		
		this.itemViewChildren = menu.findItem(R.id.menu_postview_viewchildren).setVisible(false);
		this.itemViewParent = menu.findItem(R.id.menu_postview_viewparent).setVisible(false);
		this.itemViewPools = menu.findItem(R.id.menu_postview_viewpools).setVisible(false);
		
		// this HAX refreshes visible menu items
		//int page = this.getIntent().getIntExtra(EXTRA_POST_INDEX, 0);
		//this.onPageSelected(page);
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
		this.selectedPost = this.postsApi.getPosts().get(key);
		Logger.i("PostViewActivity::onPageSelected", String.valueOf(this.selectedPost.getId()));
		
		// now this works, I'm happy again (:
		// now improved
		
		if(this.itemViewChildren != null)
			this.itemViewChildren.setVisible(this.selectedPost.isHasChildren());
		if(this.itemViewParent != null)
			this.itemViewParent.setVisible(this.selectedPost.getParentId() > 0);
		
		// oh hey it became natural! and I didn't had to do much!
		if(pos + 1 >= size)
		{
			this.postsApi.request();
		}
	}
	/* OnPageChangeListener methods */
}
