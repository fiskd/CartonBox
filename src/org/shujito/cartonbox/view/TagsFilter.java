package org.shujito.cartonbox.view;

import java.util.List;

import org.shujito.cartonbox.model.Tag;

import android.widget.Filter;

public class TagsFilter extends Filter
{
	FilterCallback<List<Tag>> callback = null;
	
	public TagsFilter(FilterCallback<List<Tag>> callback)
	{
		this.callback = callback;
	}
	
	@Override
	protected FilterResults performFiltering(CharSequence constraint)
	{
		FilterResults fr = new FilterResults();
		if(constraint != null)
		{
		}
		return fr;
	}
	
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results)
	{
		if(results != null && this.callback != null)
		{
			@SuppressWarnings("unchecked")
			List<Tag> values = (List<Tag>)results.values;
			this.callback.onFilter(values);
		}
	}
}
