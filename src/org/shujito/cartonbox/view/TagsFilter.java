package org.shujito.cartonbox.view;

import java.util.ArrayList;
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
			try
			{
				Thread.sleep(1000);
			}
			catch(Exception ex)
			{ }
			
			List<Tag> ls = new ArrayList<Tag>();
			ls.add(new Tag().setName(constraint.toString()).setCount(constraint.length()));
			ls.add(new Tag().setName("touhou").setCount(1));
			ls.add(new Tag().setName("test1").setCount(3));
			ls.add(new Tag().setName("test2").setCount(5));
			ls.add(new Tag().setName("test3").setCount(7));
			
			fr.values = ls;
			fr.count = ls.size();
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
