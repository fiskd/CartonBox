package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageDownloader;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.model.Post;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PostViewFragment extends Fragment
{
	/* static */
	static String EXTRA_POST = "org.shujito.cartonbox.POST";
	
	public static PostViewFragment create(Post post)
	{
		// it's a pun
		Bundle humbleArgs = new Bundle();
		humbleArgs.putSerializable(EXTRA_POST, post);
		
		PostViewFragment fragment = new PostViewFragment();
		fragment.setArguments(humbleArgs);
		
		return fragment;
	}
	
	/* fields */
	
	// logic
	ImageDownloader sampleDownloader = null;
	ImageDownloader thumbDownloader = null;
	Post post = null;
	// components
	ProgressBar pbloading = null;
	TextView tvmessage = null;
	ImageView ivpreview = null;
	ImageView ivred = null;
	ImageView ivblue = null;
	ImageView ivgreen = null;
	ImageView ivyellow = null;
	/* constructor */
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.post_item_pager, dad, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		this.post = (Post)this.getArguments().getSerializable(EXTRA_POST);
		// get out early
		if(this.post == null)
			return;
		
		Logger.i("POST ID", String.valueOf(this.post.getId()));
		
		this.pbloading = (ProgressBar)view.findViewById(R.id.post_item_pager_pbloading);
		//this.pbloading.setVisibility(View.VISIBLE);
		
		this.tvmessage = (TextView)view.findViewById(R.id.post_item_pager_tvmessage);
		this.tvmessage.setVisibility(View.GONE);
		
		// build the view appearance here
		this.ivpreview = (ImageView)view.findViewById(R.id.post_item_pager_ivsample);
		// flagged
		this.ivred = (ImageView)view.findViewById(R.id.post_item_pager_ivred);
		// pending
		this.ivblue = (ImageView)view.findViewById(R.id.post_item_pager_ivblue);
		// parent (has children)
		this.ivgreen = (ImageView)view.findViewById(R.id.post_item_pager_ivgreen);
		// child (belongs to parent)
		this.ivyellow = (ImageView)view.findViewById(R.id.post_item_pager_ivyellow);

		if(this.post.isFlagged())
			this.ivred.setVisibility(View.VISIBLE);
		else
			this.ivred.setVisibility(View.GONE);
		
		if(this.post.isPending())
			this.ivblue.setVisibility(View.VISIBLE);
		else
			this.ivblue.setVisibility(View.GONE);
		
		if(this.post.isHasChildren())
			this.ivgreen.setVisibility(View.VISIBLE);
		else
			this.ivgreen.setVisibility(View.GONE);
		
		if(this.post.getParentId() != 0)
			this.ivyellow.setVisibility(View.VISIBLE);
		else
			this.ivyellow.setVisibility(View.GONE);
		
		this.sampleDownloader = new ImageDownloader(this.getActivity(), this.post.getSampleUrl());
		this.sampleDownloader.setOnImageFetchedListener(new OnImageFetchedListener()
		{
			@Override
			public void onImageFetched(Bitmap b)
			{
				if(b == null)
				{
					tvmessage.setVisibility(View.VISIBLE);
				}
				else
				{
					ivpreview.setImageBitmap(b);
				}
			}
		});
		
		this.thumbDownloader = new ImageDownloader(this.getActivity(), this.post.getPreviewUrl());
		this.thumbDownloader.setOnImageFetchedListener(new OnImageFetchedListener()
		{
			@Override
			public void onImageFetched(Bitmap b)
			{
				pbloading.setVisibility(View.GONE);
				
				if(b == null)
				{
					tvmessage.setVisibility(View.VISIBLE);
				}
				else
				{
					ivpreview.setImageBitmap(b);
					// start downloading sample
					if(sampleDownloader != null)
						sampleDownloader.execute();
				}
			}
		});
		thumbDownloader.execute();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// why, ask your mother
		if(this.thumbDownloader != null)
			this.thumbDownloader.cancel(true);
		this.thumbDownloader = null;
		
		if(this.sampleDownloader != null)
			this.sampleDownloader.cancel(true);
		this.sampleDownloader = null;
	}
}
