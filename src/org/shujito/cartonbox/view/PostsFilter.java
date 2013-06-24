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
	// stuff
	boolean bShowSafe = true;
	boolean bShowQuestionable = false;
	boolean bShowExplicit = false;
	boolean bShowFlagged = false;
	boolean bShowDeleted = false;
	boolean bEnableBlacklist = false;
	String sBlacklistedTags = null;
	
	public PostsFilter(FilterCallback<SparseArray<Post>> callback)
	{
		this.callback = callback;
		//set from preferences
		this.bShowSafe = Preferences.getBool(R.string.pref_ratings_todisplay_safe_key, true);
		this.bShowQuestionable = Preferences.getBool(R.string.pref_ratings_todisplay_questionable_key);
		this.bShowExplicit = Preferences.getBool(R.string.pref_ratings_todisplay_explicit_key);
		this.bShowDeleted = Preferences.getBool(R.string.pref_content_showdeletedposts_key);
		this.bShowFlagged = Preferences.getBool(R.string.pref_content_showflaggedposts_key);
		this.bEnableBlacklist = Preferences.getBool(R.string.pref_blacklist_enabled_key);
		this.sBlacklistedTags = Preferences.getString(R.string.pref_blacklist_tags_key);
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
				
				boolean shouldAdd = false;
				// ratings (hey, a penis, hide that)
				shouldAdd = shouldAdd || (this.bShowSafe && post.getRating() == Rating.Safe);
				shouldAdd = shouldAdd || (this.bShowQuestionable && post.getRating() == Rating.Questionable);
				shouldAdd = shouldAdd || (this.bShowExplicit && post.getRating() == Rating.Explicit);
				// must not show flash files
				shouldAdd = shouldAdd && !"swf".equals(post.getFileExt());
				// statuses (deleted or flagged)
				shouldAdd = shouldAdd && !(post.isDeleted() && !this.bShowDeleted);
				shouldAdd = shouldAdd && !(post.isFlagged() && !this.bShowFlagged);
				// blacklists
				if(this.sBlacklistedTags != null)
				{
					String[] groups = sBlacklistedTags.split("\\n");
					for(String group : groups)
					{
						String[] tags = group.split("\\s+");
						for(String tag : tags)
						{
							//shouldAdd = shouldAdd && post.getTags().contains(tag);
						}
					}
				}
				
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
