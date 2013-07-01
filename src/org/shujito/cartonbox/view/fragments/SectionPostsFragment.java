package org.shujito.cartonbox.view.fragments;

import java.net.HttpURLConnection;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnRequestListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.activities.PostViewActivity;
import org.shujito.cartonbox.view.adapters.PostsGridAdapter;
import org.shujito.cartonbox.view.listeners.OnFragmentAttachedListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SectionPostsFragment extends Fragment implements
	OnErrorListener, OnItemClickListener, OnScrollListener,
	OnPostsFetchedListener, OnRequestListener
{
	/* Listeners */
	OnFragmentAttachedListener onFragmentAttachedListener = null;
	
	/* Fields */
	ImageboardPosts postsApi = null;
	
	GridView mGvPosts = null;
	ProgressBar mPbProgress = null;
	TextView mTvMessage = null;
	
	PostsGridAdapter mPostsAdapter = null;
	
	/* Constructor */
	@Override
	public void onAttach(Activity activity)
	{
		Logger.i("SectionPostsFragment::onAttach", "overly attached fragment");
		super.onAttach(activity);
		try
		{
			// try to get the listener from the activity
			this.onFragmentAttachedListener = (OnFragmentAttachedListener)activity;
		}
		catch(Exception ex)
		{
			Logger.e("SectionPostsFragment::onAttach", "Couldn't get listener from the activity this is attached to");
		}
	}
	
	@Override
	public void onActivityCreated(Bundle cirno)
	{
		super.onActivityCreated(cirno);
	}
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.posts_section, dad, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle cirno)
	{
		Logger.i("SectionPostsFragment::onViewCreated", "fragment created");
		super.onViewCreated(view, cirno);
		
		this.mPostsAdapter = new PostsGridAdapter(this.getActivity());
		
		if(this.onFragmentAttachedListener != null)
		{
			try
			{
				this.postsApi = (ImageboardPosts)this.onFragmentAttachedListener.onFragmentAttached(this);
			}
			catch(Exception ex)
			{
				// XXX: holy fuck
				// http://s.modriv.net/abp/images/screencaps/6aff2a0386e9046ed8475b77bc8263ac.jpg
				Logger.wtf("SectionPostsFragment::onViewCreated", ex.toString(), ex);
			}
		}
		
		this.mGvPosts = (GridView)view.findViewById(R.id.posts_gvposts);
		this.mGvPosts.setAdapter(this.mPostsAdapter);
		this.mGvPosts.setOnItemClickListener(this);
		this.mGvPosts.setOnScrollListener(this);
		
		this.mPbProgress = (ProgressBar)view.findViewById(R.id.posts_pbloading);
		this.mPbProgress.setVisibility(View.VISIBLE);
		
		this.mTvMessage = (TextView)view.findViewById(R.id.posts_tvmessage);
		this.mTvMessage.setVisibility(View.GONE);
		
		if(this.postsApi != null && this.postsApi.getPosts().size() > 0)
		{
			this.mGvPosts.setVisibility(View.VISIBLE);
			this.mPbProgress.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Logger.i("SectionPostsFragment::onResume", "fragment resumed");
		// HAX!!
		if(this.mPostsAdapter != null)
			this.mPostsAdapter.onPostsFetched(this.postsApi.getPosts());
		if(this.postsApi != null)
		{
			this.postsApi.addOnErrorListener(this);
			this.postsApi.addOnPostsFetchedListener(this.mPostsAdapter);
			this.postsApi.addOnPostsFetchedListener(this);
			this.postsApi.addOnRequestListener(this);
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Logger.i("SectionPostsFragment::onPause", "fragment paused");
		// remove these listeners
		if(this.postsApi != null)
		{
			this.postsApi.removeOnErrorListener(this);
			this.postsApi.removeOnPostsFetchedListener(this.mPostsAdapter);
			this.postsApi.removeOnPostsFetchedListener(this);
			this.postsApi.removeOnRequestListener(this);
		}
		// disable scroll loading (remove listener)
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// get rid of this, quick
		this.postsApi = null;
		this.onFragmentAttachedListener = null;
	}
	
	/* OnErrorListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		if(errCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT)
		{
			Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
			// it failed, request again
			this.postsApi.request();
		}
		else
		{
			this.mGvPosts.setVisibility(View.GONE);
			this.mPbProgress.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.VISIBLE);
			this.mTvMessage.setText(message);
		}
		
	}
	/* OnErrorListener methods */
	
	/* OnScrollListener methods */
	@Override
	public void onScroll(AbsListView view, int first, int visible, int total)
	{
		if(first + visible >= total)
		{
			this.postsApi.request();
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView v, int state)
	{
	}
	/* OnScrollListener methods */
	
	/* OnItemClickListener methods */
	@Override
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
		int key = (int)this.mPostsAdapter.getItemId(pos);
		
		Intent ntn = new Intent(this.getActivity(), PostViewActivity.class);
		ntn.putExtra(PostViewActivity.EXTRA_POST_INDEX, pos);
		ntn.putExtra(PostViewActivity.EXTRA_POST_KEY, key);
		
		Bundle b = null;
		// zoom animation!!
		// aid used: https://www.youtube.com/watch?v=XNF8pXr6whU
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			Bitmap thumb = (Bitmap)this.mPostsAdapter.getItem(pos);
			
			if(thumb != null)
			{
				// do the fun thing
				int offsetx = (v.getWidth() - thumb.getWidth()) / 2;
				int offsety = (v.getHeight() - thumb.getHeight()) / 2;
				
				b = ActivityOptions.makeThumbnailScaleUpAnimation(v, thumb, offsetx, offsety).toBundle();
			}
			else
			{
				// do the other fun thing
				b = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
			}
			this.getActivity().startActivity(ntn, b);
		}
		else
		{
			// do the boring thing
			this.startActivity(ntn);
		}
	}
	/* OnItemClickListener methods */
	
	/* OnPostsFetchedListener */
	@Override
	public void onPostsFetched(SparseArray<Post> posts)
	{
		this.mGvPosts.setVisibility(View.VISIBLE);
		this.mPbProgress.setVisibility(View.GONE);
		this.mTvMessage.setVisibility(View.GONE);
		
		if(posts != null && posts.size() == 0)
		{
			this.mGvPosts.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.VISIBLE);
			this.mTvMessage.setText(R.string.no_posts);
			
		}
		
		if(posts != null && (this.postsApi.getPostsPerPage() >= posts.size()))
		{
			this.mGvPosts.smoothScrollToPosition(0);
		}
	}
	/* OnPostsFetchedListener methods */
	
	/* OnPostsRequestedListener methods */
	@Override
	public void onRequest()
	{
		Logger.i("SectionPostsFragment::onPostsRequest", "Posts requested");
		if(this.postsApi != null && this.postsApi.getPosts().size() == 0)
		{
			if(this.mGvPosts != null)
				this.mGvPosts.setVisibility(View.GONE);
			if(this.mPbProgress != null)
				this.mPbProgress.setVisibility(View.VISIBLE);
			if(this.mTvMessage != null)
				this.mTvMessage.setVisibility(View.GONE);
		}
	}
	/* OnPostsRequestedListener methods */
}
