package org.shujito.cartonbox.view.activity;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.service.DownloadService;
import org.shujito.cartonbox.view.PostsFilter.OnAfterPostsFilterListener;
import org.shujito.cartonbox.view.adapter.PostsPagerAdapter;
import org.shujito.cartonbox.view.fragment.dialog.PostDetailDialogFragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PostViewActivity extends SherlockFragmentActivity
	implements OnPageChangeListener, OnAfterPostsFilterListener
{
	public static String EXTRA_POST_INDEX = "org.shujito.cartonbox.POST_INDEX";
	//public static String EXTRA_POST_KEY = "org.shujito.cartonbox.POST_KEY";
	
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
		this.mPostsAdapter.setOnAfterPostsFilterListener(this);
		this.mVpPosts = (ViewPager)this.findViewById(R.id.vpPosts);
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
		// a hack, this makes the posts adapter think it has fetched something
		this.mPostsAdapter.onPostsFetched(this.postsApi.getPosts());
		// pager listener
		this.mVpPosts.setOnPageChangeListener(this);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		// get current pager item and put it into extras so we don't lose
		// our current pager position when changing the orientation
		int position = this.mVpPosts.getCurrentItem();
		this.getIntent().putExtra(EXTRA_POST_INDEX, position);
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
			case R.id.save:
				if(this.selectedPost != null)
				{
					Intent ntn = new Intent(this, DownloadService.class);
					
					String defaultSaveDir = this.getString(R.string.app_name);
					String saveDir = Preferences.getString(R.string.pref_general_download_folder_key, defaultSaveDir);
					String where = this.selectedPost.getSite().getName();
					where = where.concat(" ");
					where = where.concat(this.selectedPost.getMd5());
					where = where.concat(".");
					where = where.concat(this.selectedPost.getFileExt());
					
					// TODO: pref, download stuff by folder rather than booru_md5.ext
					// this if condition is a palceholder, it does nothing actually
					if(ntn.equals(null))
					{
						saveDir = saveDir.concat("/");
						saveDir = saveDir.concat(this.selectedPost.getSite().getName());
						where = this.selectedPost.getMd5();
						where = where.concat(".");
						where = where.concat(this.selectedPost.getFileExt());
					}
					
					// where is the web resource located
					ntn.putExtra(DownloadService.EXTRA_SOURCE, this.selectedPost.getUrl());
					// the name to save the file with
					ntn.putExtra(DownloadService.EXTRA_DESTINATION, where);
					// what directory to save into
					ntn.putExtra(DownloadService.EXTRA_DIRECTORY, saveDir);
					// display this while downloading
					ntn.putExtra(DownloadService.EXTRA_DISPLAY, this.selectedPost.getMd5());
					this.startService(ntn);
				}
				return true;
			case R.id.preferences:
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
			case R.id.details:
				PostDetailDialogFragment dialog = new PostDetailDialogFragment();
				// it's a pun!
				Bundle humble = new Bundle();
				humble.putSerializable(PostDetailDialogFragment.EXTRA_POSTOBJECT, this.selectedPost);
				dialog.setArguments(humble);
				dialog.show(this.getSupportFragmentManager(), PostDetailDialogFragment.TAG);
				return true;
			case R.id.browser:
				if(this.selectedPost != null)
				{
					String postUrl = this.selectedPost.toString();
					Uri uri = Uri.parse(postUrl);
					Intent site = new Intent(Intent.ACTION_VIEW, uri);
					this.startActivity(site);
				}
				return true;
			case R.id.viewparent:
				// TODO: make it work
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
			case R.id.viewchildren:
				// TODO: make it work
				Toast.makeText(this, this.getString(R.string.notavailable), Toast.LENGTH_SHORT).show();
				return true;
			case R.id.viewpools:
				// TODO: make it work
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
		
		this.itemViewChildren = menu.findItem(R.id.viewchildren).setVisible(false);
		this.itemViewParent = menu.findItem(R.id.viewparent).setVisible(false);
		this.itemViewPools = menu.findItem(R.id.viewpools).setVisible(false);
		
		// fuck haxes...
		// I don't remember what was the hax here...
		if(this.selectedPost != null)
		{
			this.itemViewChildren.setVisible(this.selectedPost.isHasChildren());
			this.itemViewParent.setVisible(this.selectedPost.getParentId() > 0);
		}
		
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
		long postId = this.mPostsAdapter.getItemId(pos);
		int postCount = this.mPostsAdapter.getCount();
		
		this.selectedPost = this.postsApi.getPosts().get((int)postId);
		
		if(this.itemViewChildren != null)
			this.itemViewChildren.setVisible(this.selectedPost.isHasChildren());
		if(this.itemViewParent != null)
			this.itemViewParent.setVisible(this.selectedPost.getParentId() > 0);
		
		// that wasn't hard at all...
		if(pos + 1 >= postCount)
		{
			// refresh when
			this.postsApi.request();
		}
	}
	/* OnPageChangeListener methods */
	
	/* OnAfterPostsFilterListener methods */
	@Override
	public void onPostFilter()
	{
		this.mPostsAdapter.setOnAfterPostsFilterListener(null);
		int position = this.getIntent().getIntExtra(EXTRA_POST_INDEX, 0);
		if(this.mVpPosts != null)
			this.mVpPosts.setCurrentItem(position, false);
		if(position == 0)
			this.onPageSelected(0);
	}
	/* OnAfterPostsFilterListener methods */
}
