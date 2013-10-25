package org.shujito.cartonbox.view.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DefaultSitesAdapter extends BaseAdapter
{
	private Context context = null;
	
	public DefaultSitesAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount()
	{
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
		return null;
	}
}
