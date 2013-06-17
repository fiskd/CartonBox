package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.listeners.OnDownloadProgressListener;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.utils.BitmapCache;
import org.shujito.cartonbox.utils.ConcurrentTask;
import org.shujito.cartonbox.utils.ImageDownloader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PostsGridAdapter extends BaseAdapter implements OnPostsFetchedListener
{
	Context context = null;
	SparseArray<Post> posts = null;
	// put cached bitmaps here
	//SparseArray<Bitmap> bitmaps = null;
	BitmapCache cache = null;
	
	public PostsGridAdapter(Context context)
	{
		this.context = context;
		//this.bitmaps = new SparseArray<Bitmap>();
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
		final int key = this.posts.keyAt(index);
		
		return this.cache.getBitmapFromMemCache(key);
	}
	
	@Override
	public long getItemId(int pos)
	{
		return 0;
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup dad)
	{
		if(v == null)
		{
			LayoutInflater inf = LayoutInflater.from(this.context);
			v = inf.inflate(R.layout.post_item_grid, dad, false);
		}
		
		// should get reverse index
		int size = this.getCount();
		int index = size - pos - 1;
		
		// must get a key from the index, that's how SparseArray works
		final int key = this.posts.keyAt(index);
		Post post = this.posts.get(key);
		// getting out early...
		if(post == null) return v;

		final ImageView ivpreview = (ImageView)v.findViewById(R.id.post_item_grid_ivpreview);
		final ProgressBar pbprogress = (ProgressBar)v.findViewById(R.id.post_item_grid_pbprogress);
		final TextView tvloading = (TextView)v.findViewById(R.id.post_item_grid_tvloading);
		// flagged
		ImageView ivred = (ImageView)v.findViewById(R.id.post_item_grid_ivred);
		// pending
		ImageView ivblue = (ImageView)v.findViewById(R.id.post_item_grid_ivblue);
		// parent (has children)
		ImageView ivgreen = (ImageView)v.findViewById(R.id.post_item_grid_ivgreen);
		// child (belongs to parent)
		ImageView ivyellow = (ImageView)v.findViewById(R.id.post_item_grid_ivyellow);
		
		if(post.isFlagged())
			ivred.setVisibility(View.VISIBLE);
		else
			ivred.setVisibility(View.GONE);
		
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
			// remove the listener, have it to download it but don't display it
			// potential memory waster but whatever
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
			pbprogress.setVisibility(View.GONE);
			pbprogress.setProgress(0);
			
			//tvloading.setText(String.valueOf(one.getId()));
			tvloading.setVisibility(View.VISIBLE);
			tvloading.setText(R.string.loading);
			//tvloading.setText(String.format("w:%s h:%s", v.getWidth(), v.getHeight()));
			//tvloading.setText(String.valueOf(one.getRating()));
			
			downloader.setWidth(150);
			downloader.setHeight(150);
			downloader.setOnDownloadProgressListener(new OnDownloadProgressListener()
			{
				@Override
				public void onDownloadProgress(float progress)
				{
					float percentProgress = progress * 100;
					
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
					if(bitmap == null)
					{
						tvloading.setText("whoops");
						//tvloading.setText(null);
						ivpreview.setImageBitmap(cachedBitmap);
						// XXX: let's hack here...
						notifyDataSetChanged();
					}
					else
					{
						// cache it
						//bitmaps.append(key, b);
						cache.addBitmapToMemCache(key, bitmap);
						ivpreview.setImageBitmap(bitmap);
					}
					
					ivpreview.setBackgroundColor(Color.BLACK);
					pbprogress.setVisibility(View.GONE);
					tvloading.setVisibility(View.GONE);
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
		this.notifyDataSetChanged();
	}
}
