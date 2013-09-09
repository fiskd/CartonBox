package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class PostDetailAdapter extends BaseAdapter
	implements OnItemClickListener
{
	Context context = null;
	Post post = null;
	
	public PostDetailAdapter(Context contex, Post post)
	{
		this.context = contex;
		this.post = post;
		
		
	}
	
	@Override
	public int getCount()
	{
		return 20;
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
			v = inf.inflate(R.layout.detail_item, dad, false);
			((CheckedTextView)v.findViewById(R.id.ctvTag)).setText("Phoenix");
		}
		
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> ada, View v, int pos, long id)
	{
	}
}
