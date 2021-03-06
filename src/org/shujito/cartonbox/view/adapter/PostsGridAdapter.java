package org.shujito.cartonbox.view.adapter;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.listener.OnPostsFetchedListener;
import org.shujito.cartonbox.controller.task.ImageDownloader;
import org.shujito.cartonbox.controller.task.listener.OnDownloadProgressListener;
import org.shujito.cartonbox.controller.task.listener.OnImageFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.util.BitmapCache;
import org.shujito.cartonbox.util.ConcurrentTask;
import org.shujito.cartonbox.view.PostsFilter;
import org.shujito.cartonbox.view.PostsFilter.FilterCallback;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PostsGridAdapter extends BaseAdapter
	implements OnPostsFetchedListener, Filterable, FilterCallback<SparseArray<Post>>
{
	Context context = null;
	SparseArray<Post> posts = null;
	PostsFilter filter = null;
	// put cached bitmaps here
	BitmapCache cache = null;
	
	public PostsGridAdapter(Context context)
	{
		this.context = context;
		this.filter = new PostsFilter(this);
		this.cache = new BitmapCache();
	}
	
	public PostsGridAdapter(Context context, PostsFilter filter)
	{
		this.context = context;
		this.filter = filter;
		this.cache = new BitmapCache();
	}
	
	@Override
	public int getCount()
	{
		if(this.posts != null)
			return this.posts.size();
		return 0;
	}
	
	@Override
	public Object getItem(int pos)
	{
		// should get reverse index
		int size = this.getCount();
		int index = size - pos - 1;
		
		// must get a key from the index, that's how SparseArray works
		int key = this.posts.keyAt(index);
		
		return this.cache.getBitmapFromMemCache(key);
	}
	
	@Override
	public long getItemId(int pos)
	{
		// should get reverse index
		int size = this.getCount();
		int index = size - pos - 1;
		
		// must get a key from the index, that's how SparseArray works
		int key = this.posts.keyAt(index);
		
		return key;
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup dad)
	{
		if(v == null)
		{
			LayoutInflater inf = LayoutInflater.from(this.context);
			v = inf.inflate(R.layout.item_post_grid, dad, false);
		}
		
		// should get reverse index
		int size = this.getCount();
		int index = size - pos - 1;
		
		// must get a key from the index, that's how SparseArray works
		final int key = this.posts.keyAt(index);
		Post post = this.posts.get(key);
		// getting out early...
		if(post == null) return v;

		final ImageView ivpreview = (ImageView)v.findViewById(R.id.ivPreview);
		final ProgressBar pbprogress = (ProgressBar)v.findViewById(R.id.pbProgress);
		final TextView tvloading = (TextView)v.findViewById(R.id.tvLoading);
		// flagged
		ImageView ivred = (ImageView)v.findViewById(R.id.ivRed);
		// deleted
		ImageView ivgray = (ImageView)v.findViewById(R.id.ivGray);
		// pending
		ImageView ivblue = (ImageView)v.findViewById(R.id.ivBlue);
		// parent (has children)
		ImageView ivgreen = (ImageView)v.findViewById(R.id.ivGreen);
		// child (belongs to parent)
		ImageView ivyellow = (ImageView)v.findViewById(R.id.ivYellow);
		
		if(post.isFlagged())
			ivred.setVisibility(View.VISIBLE);
		else
			ivred.setVisibility(View.GONE);
		
		if(post.isDeleted())
			ivgray.setVisibility(View.VISIBLE);
		else
			ivgray.setVisibility(View.GONE);
		
		if(post.isPending())
			ivblue.setVisibility(View.VISIBLE);
		else
			ivblue.setVisibility(View.GONE);
		
		if(post.isHasChildren())
			ivgreen.setVisibility(View.VISIBLE);
		else
			ivgreen.setVisibility(View.GONE);
		
		if(post.getParentId() != 0)
			ivyellow.setVisibility(View.VISIBLE);
		else
			ivyellow.setVisibility(View.GONE);
		
		ImageDownloader downloader = new ImageDownloader(this.context, post.getPreviewUrl());
		
		if(ivpreview.getTag() instanceof ImageDownloader)
		{
			// get the downloader attached to the imageview so it can be cancelled (how about no)
			//((ImageDownloader)ivpreview.getTag()).cancel(true);
			ImageDownloader attached = (ImageDownloader)ivpreview.getTag();
			// remove the listener
			attached.setOnDownloadProgressListener(null);
			attached.setOnImageFetchedListener(null);
		}
		
		ivpreview.setTag(downloader);
		ivpreview.setImageBitmap(null);
		ivpreview.setBackgroundColor(Color.TRANSPARENT);
		
		//Bitmap cachedBitmap = this.bitmaps.get(key);
		final Bitmap cachedBitmap = this.cache.getBitmapFromMemCache(key);
		if(cachedBitmap != null)
		{
			ivpreview.setImageBitmap(cachedBitmap);
			ivpreview.setBackgroundColor(Color.BLACK);
			pbprogress.setVisibility(View.GONE);
			tvloading.setVisibility(View.GONE);
		}
		else
		{
			pbprogress.setVisibility(View.VISIBLE);
			pbprogress.setIndeterminate(true);
			pbprogress.setProgress(0);
			
			downloader.setWidth(150);
			downloader.setHeight(150);
			downloader.setOnDownloadProgressListener(new OnDownloadProgressListener()
			{
				@Override
				public void onDownloadProgress(float progress)
				{
					float percentProgress = progress * 100;
					pbprogress.setIndeterminate(false);
					pbprogress.setVisibility(View.VISIBLE);
					pbprogress.setProgress((int)percentProgress);
					tvloading.setVisibility(View.VISIBLE);
					tvloading.setText(String.format("%.2f%%", percentProgress));
				}
			});
			downloader.setOnImageFetchedListener(new OnImageFetchedListener()
			{
				@Override
				public void onImageFetched(Bitmap bitmap)
				{
					Drawable[] overlay = new Drawable[2];
					overlay[0] = new ColorDrawable(0xff000000);
					
					if(bitmap == null)
					{
						tvloading.setText("whoops");
						overlay[1] = new BitmapDrawable(context.getResources(), cachedBitmap);
						// XXX: Let's hack here...
						//notifyDataSetChanged();
					}
					else
					{
						// cache it
						//bitmaps.append(key, b);
						cache.addBitmapToMemCache(key, bitmap);
						overlay[1] = new BitmapDrawable(context.getResources(), bitmap);
					}
					// I went ahead and added some eyecandy
					TransitionDrawable fadeIn = new TransitionDrawable(overlay);
					// put it there
					ivpreview.setImageDrawable(fadeIn);
					// the usual is kept there
					ivpreview.setBackgroundColor(Color.BLACK);
					pbprogress.setVisibility(View.GONE);
					tvloading.setVisibility(View.GONE);
					// animate me!
					fadeIn.startTransition(200);
				}
			});
			
			ConcurrentTask.execute(downloader);
		}
		
		//ConcurrentTask.execute(downloader);
		
		return v;
	}
	
	@Override
	public void onPostsFetched(SparseArray<Post> posts)
	{
		this.posts = posts;
		if(this.getFilter() != null)
		{
			((PostsFilter)this.getFilter()).filter(this.posts);
		}
		else
		{
			this.notifyDataSetChanged();
		}
	}
	
	@Override
	public Filter getFilter()
	{
		return this.filter;
	}
	
	@Override
	public void onFilter(SparseArray<Post> result)
	{
		if(result != null)
			this.posts = result;
		this.notifyDataSetChanged();
	}
}
