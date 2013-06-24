package org.shujito.cartonbox.view;

import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;

import android.util.SparseArray;
import android.widget.Filter;

public class PostsFilter extends Filter
{
	FilterCallback<SparseArray<Post>> callback = null;
	SparseArray<Post> unfiltered = null;
	
	public PostsFilter(FilterCallback<SparseArray<Post>> callback)
	{
		this.callback = callback;
		
		boolean bShowSafe = Preferences.getBool(R.string.pref_ratings_todisplay_safe_key, true);
		boolean bShowQuestionable = Preferences.getBool(R.string.pref_ratings_todisplay_questionable_key);
		boolean bShowExplicit = Preferences.getBool(R.string.pref_ratings_todisplay_explicit_key);
		boolean bShowFlagged = Preferences.getBool(R.string.pref_content_showflaggedposts_key);
		boolean bShowDeleted = Preferences.getBool(R.string.pref_content_showdeletedposts_key);
		
	}
	
	// lovely, this works on a separate thread
	@Override
	protected FilterResults performFiltering(CharSequence constraint)
	{
		FilterResults fr = new FilterResults();
		if(this.unfiltered != null)
		{
			SparseArray<Post> filtered = new SparseArray<Post>();
			for(int idx = 0; idx < this.unfiltered.size(); idx++)
			{
				int key = this.unfiltered.keyAt(idx);
				Post post = this.unfiltered.get(key);
				
				if(post.getRating() == Rating.Safe)
				{
					filtered.append(key, post);
				}
			}
			fr.values = filtered;
			fr.count = filtered.size();
		}
		return fr;
	}
	
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results)
	{
		if(results != null && this.callback != null)
		{
			@SuppressWarnings("unchecked")
			SparseArray<Post> values = (SparseArray<Post>)results.values;
			this.callback.onFilter(values);
		}
		else
		{
			// bleh
			this.callback.onFilter(this.unfiltered);
		}
	}
	
	public void filter(SparseArray<Post> posts)
	{
		this.unfiltered = posts;
		this.filter((String)null);
	}
}
