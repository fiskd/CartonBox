package org.shujito.cartonbox.view.adapters;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.listeners.OnTagsFetchedListener;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.view.FilterCallback;
import org.shujito.cartonbox.view.TagsFilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class TagsAdapter extends BaseAdapter
	implements OnTagsFetchedListener, Filterable, FilterCallback<List<Tag>>
{
	Filter filter = null;
	Context context = null;
	List<Tag> tags = null;
	
	public TagsAdapter(Context context)
	{
		this.context = context;
		this.tags = new ArrayList<Tag>();
	}
	
	@Override
	public int getCount()
	{
		// number of entries
		if(this.tags != null)
			return this.tags.size();
		return 0;
	}
	
	@Override
	public Object getItem(int pos)
	{
		Tag tag = this.tags.get(pos);
		if(tag != null)
		{
			// returns the tag name instead of the tags object's tostring value
			return tag.getName();
		}
		return null;
	}
	
	@Override
	public long getItemId(int pos)
	{
		// why do we need this?
		return this.tags.get(pos).hashCode();
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup dad)
	{
		if(v == null)
		{
			LayoutInflater inf = LayoutInflater.from(this.context);
			v = inf.inflate(R.layout.tag_item, dad, false);
		}
		
		// ah, there we go...
		Tag tag = this.tags.get(pos);
		
		((TextView)v.findViewById(R.id.tag_item_tvname)).setText(tag.getName());
		((TextView)v.findViewById(R.id.tag_item_tvcount)).setText(String.valueOf(tag.getCount()));
		
		return v;
	}
	
	@Override
	public Filter getFilter()
	{
		// when a filter is absent, create one
		if(this.filter == null)
			this.filter = new TagsFilter(this);
		return this.filter;
	}
	
	@Override
	public void onFilter(List<Tag> result)
	{
		this.tags = result;
		this.notifyDataSetChanged();
	}
	
	@Override
	public void onTagsFetchedListener(List<Tag> tags)
	{
		this.tags = tags;
		this.notifyDataSetChanged();
	}
}
