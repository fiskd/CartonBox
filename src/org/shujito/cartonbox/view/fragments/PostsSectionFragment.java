package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.Imageboard;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnFragmentAttachedListener;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.view.activities.PostViewActivity;
import org.shujito.cartonbox.view.adapters.PostsGridAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class PostsSectionFragment extends Fragment implements OnErrorListener, OnItemClickListener, OnScrollListener, OnPostsFetchedListener
{
	/* Listeners */
	OnFragmentAttachedListener onFragmentAttachedListener = null;
	
	/* Fields */
	Imageboard api = null;
	
	GridView mGvPosts = null;
	ProgressBar mPbProgress = null;
	TextView mTvMessage = null;
	
	PostsGridAdapter mPostsAdapter = null;
	
	/* Constructor */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			// try to get the listener from the activity
			this.onFragmentAttachedListener = (OnFragmentAttachedListener)activity;
		}
		catch(Exception ex)
		{
			Logger.e("PostsSectionFragment::onAttach", "Couldn't get listener from the activity this attached to");
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
		super.onViewCreated(view, cirno);
		
		this.mPostsAdapter = new PostsGridAdapter(this.getActivity());
		
		if(this.onFragmentAttachedListener != null)
		{
			this.api = (Imageboard)this.onFragmentAttachedListener.onFragmentAttached(this);
			this.api.addOnErrorListener(this);
			this.api.addOnPostsFetchedListener(this.mPostsAdapter);
			this.api.addOnPostsFetchedListener(this);
		}
		
		this.mGvPosts = (GridView)view.findViewById(R.id.posts_gvposts);
		this.mGvPosts.setAdapter(this.mPostsAdapter);
		this.mGvPosts.setOnItemClickListener(this);
		this.mGvPosts.setOnScrollListener(this);
		
		this.mPbProgress = (ProgressBar)view.findViewById(R.id.posts_pbloading);
		this.mPbProgress.setVisibility(View.VISIBLE);
		
		this.mTvMessage = (TextView)view.findViewById(R.id.posts_tvmessage);
		this.mTvMessage.setVisibility(View.GONE);
		
		if(this.api != null && this.api.getPosts().size() > 0)
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
		// XXX: HACKY!!
		if(this.mPostsAdapter != null)
			this.mPostsAdapter.onPostsFetched(this.api);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// get rid of this, quick
		this.api = null;
	}
	
	/* OnErrorListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		this.mGvPosts.setVisibility(View.GONE);
		this.mPbProgress.setVisibility(View.GONE);
		this.mTvMessage.setVisibility(View.VISIBLE);
		this.mTvMessage.setText(message);
	}
	/* OnErrorListener methods */
	
	/* OnScrollListener methods */
	@Override
	public void onScroll(AbsListView view, int first, int visible, int total)
	{
		if(first + visible >= total)
		{
			this.api.requestPosts();
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView v, int state)
	{
	}
	/* OnScrollListener methods */
	
	/* OnClickListener methods */
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
		// TODO: add some nice-looking zoom animation
		Intent ntn = new Intent(this.getActivity(), PostViewActivity.class);
		ntn.putExtra(PostViewActivity.EXTRA_POST_INDEX, pos);
		this.startActivity(ntn);
	}
	/* OnClickListener methods */
	
	/* OnPostsFetchedListener */
	@Override
	public void onPostsFetched(Imageboard api)
	{
		this.mGvPosts.setVisibility(View.VISIBLE);
		this.mPbProgress.setVisibility(View.GONE);
		this.mTvMessage.setVisibility(View.GONE);
		
		if(api.getPosts().size() == 0)
		{
			this.mGvPosts.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.VISIBLE);
			this.mTvMessage.setText(R.string.no_posts);
		}
	}
	/* OnPostsFetchedListener */
}