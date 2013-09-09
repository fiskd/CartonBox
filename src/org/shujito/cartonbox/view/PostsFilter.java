package org.shujito.cartonbox.view;

import java.util.Locale;

import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;

import android.util.SparseArray;
import android.widget.Filter;

public class PostsFilter extends Filter
{
	public interface FilterCallback<T>
	{
		public void onFilter(T result);
	}
	
	public interface OnAfterPostsFilterListener
	{
		public void onPostFilter();
	}
	
	FilterCallback<SparseArray<Post>> callback = null;
	SparseArray<Post> unfiltered = null;
	// stuff
	boolean bShowSafe = true;
	boolean bShowQuestionable = false;
	boolean bShowExplicit = false;
	boolean bShowFlagged = false;
	boolean bShowDeleted = false;
	boolean bEnableBlacklist = false;
	boolean bBlacklistExactWord = true;
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
				
				if(this.isPostFilterable(post))
				{
					filtered.append(key, post);
				}
			}
			fr.values = filtered;
			fr.count = filtered.size();
		}
		return fr;
	}
	
	// going back to the ui thread
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
	
	public boolean isPostFilterable(Post post)
	{
		// flash posts are not worth showing on mobile devices
		// (damn you adobe/google/etc)
		if("swf".equals(post.getFileExt()))
			return false;
		// TODO: make gifs work
		
		boolean bFilterable = false;
		// filter safe if safe posts are disabled
		bFilterable = bFilterable || (this.bShowSafe && post.getRating() == Rating.Safe);
		// same but with questionable posts
		bFilterable = bFilterable || (this.bShowQuestionable && post.getRating() == Rating.Questionable);
		// same but with explicit posts
		bFilterable = bFilterable || (this.bShowExplicit && post.getRating() == Rating.Explicit);
		// hide flagged posts if flagged posts are disabled
		bFilterable = bFilterable && !(!this.bShowFlagged && post.isFlagged());
		// same but with deleted posts
		bFilterable = bFilterable && !(!this.bShowDeleted && post.isDeleted());
		
		// apply blacklist if the post is filterable at this point
		if(bFilterable && this.bEnableBlacklist)
		{
			// tag groups! hides posts containing all of these tags
			String[] groups = this.sBlacklistedTags.toLowerCase(Locale.US).split("\\n");
			for(String group : groups)
			{
				// separate each tag from the group
				String[] tags = group.split("\\s+");
				boolean blacklisted = false;
				for(String tag : tags)
				{
					// TODO: make it a preference
					if(this.bBlacklistExactWord)
					{
						String[] postTags = post.getTagString().toLowerCase(Locale.US).split("\\s+");
						for(String postTag : postTags)
						{
							if(postTag.equals(tag))
							{
								// it has one
								blacklisted = true;
								// get out
								break;
							}
						}
					}
					else
					{
						if(post.getTagString().toLowerCase(Locale.US).contains(tag))
						{
							// it has one
							blacklisted = true;
						}
						else
						{
							// it doesn't has this one
							blacklisted = false;
							// get out
							break;
						}
					}
				}
				
				// it is blacklisted
				if(blacklisted)
					return false;
			}
		}
		
		return bFilterable;
	}
}
