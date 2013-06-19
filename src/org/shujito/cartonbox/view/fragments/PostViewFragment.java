package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.listeners.OnDownloadProgressListener;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.utils.ConcurrentTask;
import org.shujito.cartonbox.utils.ImageDownloader;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PostViewFragment extends Fragment
	implements OnDownloadProgressListener
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
	ImageDownloader thumbDownloader = null;
	ImageDownloader sampleDownloader = null;
	Post post = null;
	// components
	ProgressBar pbprogress = null;
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
		
		return inf.inflate(R.layout.post_item_pager, null);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void onViewCreated(View view, Bundle cirno)
	{
		this.post = (Post)this.getArguments().getSerializable(EXTRA_POST);
		// get out early
		if(this.post == null)
			return;
		
		//Logger.i("POST ID", String.valueOf(this.post.getId()));
		
		this.pbprogress = (ProgressBar)view.findViewById(R.id.post_item_pager_pbprogress);
		//this.pbloading.setVisibility(View.VISIBLE);
		
		//Point size = new Point();
		//int width = this.getActivity().getWindowManager().getDefaultDisplay().getWidth();
		//int height = this.getActivity().getWindowManager().getDefaultDisplay().getHeight();
		
		int width = 0;
		int height = 0;
		
		Display display = this.getActivity().getWindowManager().getDefaultDisplay();
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		}
		else
		{
			width = display.getWidth();
			height = display.getHeight();
		}
		
		this.tvmessage = (TextView)view.findViewById(R.id.post_item_pager_tvmessage);
		this.tvmessage.setVisibility(View.VISIBLE);
		
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
		
		this.thumbDownloader = new ImageDownloader(this.getActivity(), this.post.getPreviewUrl());
		this.thumbDownloader.setOnDownloadProgressListener(this);
		this.thumbDownloader.setWidth(width);
		this.thumbDownloader.setHeight(height);
		this.thumbDownloader.setOnImageFetchedListener(new OnImageFetchedListener()
		{
			@Override
			public void onImageFetched(Bitmap b)
			{
				if(b == null)
				{
					tvmessage.setVisibility(View.VISIBLE);
					tvmessage.setText(R.string.download_failed);
				}
				else
				{
					ivpreview.setImageBitmap(b);
					tvmessage.setText(R.string.loading_sample);
					// start downloading sample
					if(sampleDownloader != null && !sampleDownloader.isAlreadyExecuted())
					{
						ConcurrentTask.execute(sampleDownloader);
					}
				}
			}
		});
		
		ConcurrentTask.execute(this.thumbDownloader);
		
		//this.thumbDownloader.execute();
		//ConcurrentTask.execute(this.thumbDownloader);
		
		this.sampleDownloader = new ImageDownloader(this.getActivity(), this.post.getSampleUrl());
		this.sampleDownloader.setOnDownloadProgressListener(this);
		this.sampleDownloader.setWidth(width);
		this.sampleDownloader.setHeight(height);
		this.sampleDownloader.setOnImageFetchedListener(new OnImageFetchedListener()
		{
			@Override
			public void onImageFetched(Bitmap b)
			{
				pbprogress.setVisibility(View.GONE);
				
				if(b == null)
				{
					tvmessage.setVisibility(View.VISIBLE);
					tvmessage.setText(R.string.download_failed);
				}
				else
				{
					ivpreview.setImageBitmap(b);
					tvmessage.setVisibility(View.GONE);
				}
			}
		});
	}
	
	@Override
	public void onPause()
	{
		// why, ask your mother
		if(this.thumbDownloader != null)
		{
			this.thumbDownloader.setOnDownloadProgressListener(null);
			this.thumbDownloader.setOnImageFetchedListener(null);
			this.thumbDownloader.cancel(true);
		}
		this.thumbDownloader = null;
		
		if(this.sampleDownloader != null)
		{
			this.sampleDownloader.setOnDownloadProgressListener(null);
			this.sampleDownloader.setOnImageFetchedListener(null);
			this.sampleDownloader.cancel(true);
		}
		this.sampleDownloader = null;
		
		super.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		Logger.i("PostViewFragment::onDestroy", String.format("Destroyed #%s", this.post.getId()));
		super.onDestroy();
	}
	
	/* meth */
	
	@Override
	public void onDownloadProgress(float progress)
	{
		float percentProgress = progress * 100;
		
		this.pbprogress.setVisibility(View.VISIBLE);
		this.pbprogress.setProgress((int)percentProgress);
	}
}
