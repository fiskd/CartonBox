package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.activities.PostViewActivity;
import org.shujito.cartonbox.view.adapters.PostsGridAdapter;

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

public class PostsGridFragment extends Fragment implements OnItemClickListener, OnScrollListener
{
	GridView mGvPosts = null;
	ProgressBar mPbProgress = null;
	TextView mTvNoposts = null;
	
	PostsGridAdapter mPostsAdapter = null;
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		this.mPostsAdapter = new PostsGridAdapter(this.getActivity());
		
		View v = inf.inflate(R.layout.posts_section, dad, false);
		this.mGvPosts = (GridView)v.findViewById(R.id.posts_gvposts);
		this.mGvPosts.setAdapter(this.mPostsAdapter);
		this.mGvPosts.setOnItemClickListener(this);
		this.mGvPosts.setOnScrollListener(this);
		//this.mGvPosts.setVisibility(View.GONE);
		
		this.mPbProgress = (ProgressBar)v.findViewById(R.id.posts_pbloading);
		this.mPbProgress.setVisibility(View.GONE);
		
		this.mTvNoposts = (TextView)v.findViewById(R.id.posts_tvnoposts);
		this.mTvNoposts.setVisibility(View.GONE);
		
		return v;
	}
	
	/* OnScrollListener methods */
	@Override
	public void onScroll(AbsListView view, int first, int visible, int total)
	{
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
		Intent ntn = new Intent(this.getActivity(), PostViewActivity.class);
		ntn.putExtra(PostViewActivity.EXTRA_POST_INDEX, pos);
		this.startActivity(ntn);
	}
	/* OnClickListener methods */
}
