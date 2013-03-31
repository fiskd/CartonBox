package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageDownloader;
import org.shujito.cartonbox.controller.Imageboard;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.model.Post;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PostsGridAdapter extends BaseAdapter implements OnPostsFetchedListener
{
	Context context = null;
	SparseArray<Post> posts = null;
	
	public PostsGridAdapter(Context context)
	{
		this.context = context;
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
		return null;
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
		int key = this.posts.keyAt(index);
		Post one = this.posts.get(key);
		// getting out early...
		if(one == null) return v;
		
		final TextView tvloading = (TextView)v.findViewById(R.id.post_item_grid_tvloading);
		final ImageView ivpreview = (ImageView)v.findViewById(R.id.post_item_grid_ivpreview);
		// flagged
		ImageView ivred = (ImageView)v.findViewById(R.id.post_item_grid_ivred);
		// pending
		ImageView ivblue = (ImageView)v.findViewById(R.id.post_item_grid_ivblue);
		// parent (has children)
		ImageView ivgreen = (ImageView)v.findViewById(R.id.post_item_grid_ivgreen);
		// child (belongs to parent)
		ImageView ivyellow = (ImageView)v.findViewById(R.id.post_item_grid_ivyellow);
		
		if(one.isFlagged())
			ivred.setVisibility(View.VISIBLE);
		else
			ivred.setVisibility(View.GONE);
		
		if(one.isPending())
			ivblue.setVisibility(View.VISIBLE);
		else
			ivblue.setVisibility(View.GONE);
		
		if(one.isHasChildren())
			ivgreen.setVisibility(View.VISIBLE);
		else
			ivgreen.setVisibility(View.GONE);
		
		if(one.getParentId() != 0)
			ivyellow.setVisibility(View.VISIBLE);
		else
			ivyellow.setVisibility(View.GONE);
		
		tvloading.setText(String.valueOf(one.getId()));
		
		ImageDownloader downloader = new ImageDownloader(this.context, one.getPreviewUrl());
		
		if(ivpreview.getTag() instanceof ImageDownloader)
		{
			// get the downloader attached to the imageview so it can be cancelled
			((ImageDownloader)ivpreview.getTag()).cancel(true);
		}
		
		ivpreview.setTag(downloader);
		ivpreview.setImageBitmap(null);
		ivpreview.setBackgroundColor(Color.TRANSPARENT);
		
		downloader.setOnImageFetchedListener(new OnImageFetchedListener()
		{
			@Override
			public void onImageFetched(Bitmap b)
			{
				if(b == null)
				{
					tvloading.setText("whoops");
				}
				else
				{
					ivpreview.setImageBitmap(b);
					ivpreview.setBackgroundColor(Color.BLACK);
				}
			}
		});
		downloader.execute();
		
		return v;
	}
	
	@Override
	public void onPostsFetched(Imageboard api)
	{
		this.posts = api.getPosts();
		this.notifyDataSetChanged();
	}
}
