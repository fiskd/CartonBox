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
import android.os.Handler;
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
	/* static */
	public static Fragment create()
	{
		Fragment f = new SectionPostsFragment();
		return f;
	}
	
	/* Listeners */
	OnFragmentAttachedListener onFragmentAttachedListener = null;
	
	/* Fields */
	ImageboardPosts postsApi = null;
	
	GridView mGvPosts = null;
	ProgressBar mPbLoading = null;
	TextView mTvMessage = null;
	ProgressBar mPbLoadingMore = null;
	
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
		return inf.inflate(R.layout.section_posts, dad, false);
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
		
		this.mGvPosts = (GridView)view.findViewById(R.id.gvPosts);
		this.mGvPosts.setAdapter(this.mPostsAdapter);
		this.mGvPosts.setOnItemClickListener(this);
		this.mGvPosts.setOnScrollListener(this);
		
		this.mPbLoading = (ProgressBar)view.findViewById(R.id.pbLoading);
		this.mPbLoading.setVisibility(View.VISIBLE);
		
		this.mTvMessage = (TextView)view.findViewById(R.id.tvMessage);
		this.mTvMessage.setVisibility(View.GONE);
		
		this.mPbLoadingMore = (ProgressBar)view.findViewById(R.id.pbLoadingPend);
		this.mPbLoadingMore.setVisibility(View.GONE);
		
		if(this.postsApi != null)
		{
			this.postsApi.addOnErrorListener(this);
			this.postsApi.addOnPostsFetchedListener(this.mPostsAdapter);
			this.postsApi.addOnPostsFetchedListener(this);
			this.postsApi.addOnRequestListener(this);
			
			if(this.postsApi.getPosts().size() > 0)
			{
				this.mGvPosts.setVisibility(View.VISIBLE);
				this.mPbLoading.setVisibility(View.GONE);
				this.mTvMessage.setVisibility(View.GONE);
				// XXX hacky!
				this.mPostsAdapter.onPostsFetched(this.postsApi.getPosts());
			}
		}
	}
	
	@Override
	public void onDestroy()
	{
		// remove these listeners
		if(this.postsApi != null)
		{
			// I'm scared of leaving listerens alive since their assignation
			this.postsApi.removeOnErrorListener(this);
			this.postsApi.removeOnPostsFetchedListener(this.mPostsAdapter);
			this.postsApi.removeOnPostsFetchedListener(this);
			this.postsApi.removeOnRequestListener(this);
		}
		// get rid of this, quick
		this.postsApi = null;
		this.onFragmentAttachedListener = null;
		// destroy this now
		super.onDestroy();
		Logger.i("SectionPostsFragment::onDestroy", "fragment destroyed");
	}
	
	/* OnErrorListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		if(errCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT && this.mPostsAdapter != null && this.mPostsAdapter.getCount() != 0)
		{
			Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
			// it failed, request again
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if(postsApi != null)
						postsApi.request();
				}
			}, 3000);
		}
		else
		{
			this.mGvPosts.setVisibility(View.GONE);
			this.mPbLoading.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.VISIBLE);
			this.mTvMessage.setText(message);
		}
		
		if(errCode == HttpURLConnection.HTTP_NOT_FOUND)
		{
			Toast.makeText(this.getActivity(), this.getText(R.string.notbooru), Toast.LENGTH_LONG).show();
			this.getActivity().finish();
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
		//int key = (int)this.mPostsAdapter.getItemId(pos);
		
		Intent ntn = new Intent(this.getActivity(), PostViewActivity.class);
		ntn.putExtra(PostViewActivity.EXTRA_POST_INDEX, pos);
		//ntn.putExtra(PostViewActivity.EXTRA_POST_KEY, key);
		
		Post post = this.postsApi.getPosts().get((int)id);
		if((post.getUrl() != null && post.getUrl().length() > 0) && (post.getSampleUrl() != null && post.getSampleUrl().length() > 0) && (post.getPreviewUrl() != null && post.getPreviewUrl().length() > 0))
		{
			// zoom animation!!
			// aid used: https://www.youtube.com/watch?v=XNF8pXr6whU
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			{
				Bundle b = null;
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
		else
		{
			Toast.makeText(this.getActivity(), this.getText(R.string.configureit), Toast.LENGTH_SHORT).show();
		}
	}
	/* OnItemClickListener methods */
	
	/* OnPostsFetchedListener */
	@Override
	public void onPostsFetched(SparseArray<Post> posts)
	{
		this.mGvPosts.setVisibility(View.VISIBLE);
		this.mPbLoading.setVisibility(View.GONE);
		this.mTvMessage.setVisibility(View.GONE);
		this.mPbLoadingMore.setVisibility(View.GONE);
		
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
			if(this.mPbLoading != null)
				this.mPbLoading.setVisibility(View.VISIBLE);
			if(this.mTvMessage != null)
				this.mTvMessage.setVisibility(View.GONE);
		}
		else
		{
			this.mPbLoadingMore.setVisibility(View.VISIBLE);
		}
	}
	/* OnPostsRequestedListener methods */
}
