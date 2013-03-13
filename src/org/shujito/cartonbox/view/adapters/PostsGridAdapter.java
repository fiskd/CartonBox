package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PostsGridAdapter extends BaseAdapter
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
		return 50;
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
		
		return v;
	}
}
