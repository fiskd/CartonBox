package org.shujito.cartonbox.view.fragment;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.task.ImageDownloader;
import org.shujito.cartonbox.controller.task.listener.OnDownloadProgressListener;
import org.shujito.cartonbox.controller.task.listener.OnImageFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;
import org.shujito.cartonbox.util.ConcurrentTask;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PostViewFragment extends Fragment
	implements OnDownloadProgressListener, OnClickListener
{
	/* static */
	public interface PostViewCallback
	{
		public LruCache<Long, Bitmap> getCache();
	}
	
	public static String EXTRA_POST = "org.shujito.cartonbox.POST";
	
	public static Fragment create(Post post)
	{
		// it's a pun
		Bundle humbleArgs = new Bundle();
		humbleArgs.putSerializable(EXTRA_POST, post);
		
		Fragment fragment = new PostViewFragment();
		fragment.setArguments(humbleArgs);
		//fragment.setRetainInstance(true);
		
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
	PhotoView ivpreview = null;
	ImageView ivred = null;
	ImageView ivgray = null;
	ImageView ivblue = null;
	ImageView ivgreen = null;
	ImageView ivyellow = null;
	// it's a linear layout
	View llStatusDots = null;
	
	/* constructor */
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.item_post_pager, null);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public void onViewCreated(View view, Bundle cirno)
	{
		// get post
		this.post = (Post)this.getArguments().getSerializable(EXTRA_POST);
		// build the view appearance here
		this.pbprogress = (ProgressBar)view.findViewById(R.id.pbProgress);
		// message
		this.tvmessage = (TextView)view.findViewById(R.id.tvMessage);
		// image preview
		this.ivpreview = (PhotoView)view.findViewById(R.id.ivSample);
		// dots container
		this.llStatusDots = view.findViewById(R.id.llStatusdots);
		// flagged
		this.ivred = (ImageView)view.findViewById(R.id.ivRed);
		// deleted
		this.ivgray = (ImageView)view.findViewById(R.id.ivGray);
		// pending
		this.ivblue = (ImageView)view.findViewById(R.id.ivBlue);
		// parent (has children)
		this.ivgreen = (ImageView)view.findViewById(R.id.ivGreen);
		// child (belongs to parent)
		this.ivyellow = (ImageView)view.findViewById(R.id.ivYellow);
		// display things
		
		this.pbprogress.setIndeterminate(true);
		this.pbprogress.setVisibility(View.VISIBLE);
		//this.pbloading.setVisibility(View.VISIBLE);
		this.ivpreview.setImageDrawable(null);
		
		// get out early, this can't work without a post!
		if(this.post == null)
			return;
		
		//DisplayMetrics displayMetrics = new DisplayMetrics();
		//this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		//int width = displayMetrics.widthPixels;
		//int height = displayMetrics.heightPixels;
		
		//int whatToUse = width > height ? width : height;
		int whatToUse = 1280;
		
		this.tvmessage.setVisibility(View.VISIBLE);
		
		if(this.post.isFlagged())
			this.ivred.setVisibility(View.VISIBLE);
		else
			this.ivred.setVisibility(View.GONE);
		
		if(this.post.isDeleted())
			this.ivgray.setVisibility(View.VISIBLE);
		else
			this.ivgray.setVisibility(View.GONE);
		
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
		
		if(this.ivred.getVisibility() == View.GONE && this.ivgray.getVisibility() == View.GONE && this.ivblue.getVisibility() == View.GONE && this.ivgreen.getVisibility() == View.GONE && this.ivyellow.getVisibility() == View.GONE)
		{
			this.llStatusDots.setVisibility(View.GONE);
		}
		
		/* TODO: handle image downloads there
		ImageDownloaderFragment wf = (ImageDownloaderFragment)this.getChildFragmentManager().findFragmentByTag("Tagg");
		if(wf == null)
		{
			wf = new ImageDownloaderFragment();
			//this.getFragmentManager().beginTransaction().add(wf, "Tagg").commit();
			this.getChildFragmentManager().beginTransaction().add(wf, "Tagg").commit();
		}
		//*/
		
		this.thumbDownloader = new ImageDownloader(this.getActivity(), this.post.getPreviewUrl());
		this.thumbDownloader.setOnDownloadProgressListener(this);
		this.thumbDownloader.setWidth(150);
		this.thumbDownloader.setHeight(150);
		//*
		this.thumbDownloader.setOnImageFetchedListener(new OnImageFetchedListener()
		{
			@Override
			public void onImageFetched(Bitmap b)
			{
				pbprogress.setIndeterminate(true);
				
				if(b == null)
				{
					tvmessage.setVisibility(View.VISIBLE);
					tvmessage.setText(R.string.download_failed);
				}
				else
				{
					// I went ahead and added some eyecandy
					Drawable[] overlay = new Drawable[2];
					overlay[0] = new ColorDrawable(Color.TRANSPARENT);
					overlay[1] = new BitmapDrawable(getResources(), b);
					
					TransitionDrawable fadeIn = new TransitionDrawable(overlay);
					ivpreview.setImageDrawable(fadeIn);
					fadeIn.startTransition(200);
					
					//ivpreview.setImageBitmap(b);
					tvmessage.setText(R.string.loading_sample);
					// start downloading sample
					if(sampleDownloader != null && !sampleDownloader.isAlreadyExecuted())
					{
						ConcurrentTask.execute(sampleDownloader);
					}
				}
			}
		});
		
		if(Preferences.getBool(R.string.pref_content_warning_key, true))
		{
			if(post.getRating() == Rating.Safe)
			{
				// is it safe? load it then
				ConcurrentTask.execute(this.thumbDownloader);
			}
			else
			{
				// it is not!
				this.tvmessage.setText(R.string.postblocked);
				this.tvmessage.setOnClickListener(this);
				this.pbprogress.setVisibility(View.GONE);
			}
		}
		else
		{
			// just load it...
			ConcurrentTask.execute(this.thumbDownloader);
		}
		
		//this.thumbDownloader.execute();
		//ConcurrentTask.execute(this.thumbDownloader);
		
		this.sampleDownloader = new ImageDownloader(this.getActivity(), this.post.getSampleUrl());
		this.sampleDownloader.setOnDownloadProgressListener(this);
		this.sampleDownloader.setWidth(whatToUse);
		this.sampleDownloader.setHeight(whatToUse);
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
					// more visual sweets
					Drawable[] overlay = new Drawable[2];
					overlay[0] = ivpreview.getDrawable();
					overlay[1] = new BitmapDrawable(getResources(), b);
					
					TransitionDrawable fadeIn = new TransitionDrawable(overlay);
					ivpreview.setImageDrawable(fadeIn);
					fadeIn.startTransition(200);
					
					//ivpreview.setImageBitmap(b);
					tvmessage.setVisibility(View.GONE);
				}
			}
		});
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onDestroy()
	{
		// why, ask your mother
		// that was rude, kill tasks when the fragment dies
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
		Logger.i("PostViewFragment::onDestroy", String.format("Destroyed #%s", this.post.getId()));
		super.onDestroy();
	}
	
	/* meth */
	
	@Override
	public void onDownloadProgress(float progress)
	{
		float percentProgress = progress * 100;
		this.pbprogress.setIndeterminate(false);
		this.pbprogress.setVisibility(View.VISIBLE);
		this.pbprogress.setProgress((int)percentProgress);
	}
	
	@Override
	public void onClick(View v)
	{
		// manual download
		if(this.thumbDownloader.getStatus() == Status.PENDING)
		{
			ConcurrentTask.execute(this.thumbDownloader);
		}
	}
}
