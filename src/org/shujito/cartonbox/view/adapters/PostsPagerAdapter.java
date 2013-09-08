package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.PostsFilter;
import org.shujito.cartonbox.view.PostsFilter.FilterCallback;
import org.shujito.cartonbox.view.PostsFilter.OnAfterPostsFilterListener;
import org.shujito.cartonbox.view.fragments.PostViewFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

public class PostsPagerAdapter extends FragmentPagerAdapter
	implements OnPostsFetchedListener, Filterable, FilterCallback<SparseArray<Post>>
{
	/* listeners */
	OnAfterPostsFilterListener onAfterPostsFilterListener = null;
	public OnAfterPostsFilterListener getonafAfterPostsFilterListener()
	{ return this.onAfterPostsFilterListener; }
	public void setOnAfterPostsFilterListener(OnAfterPostsFilterListener l)
	{ this.onAfterPostsFilterListener = l; }
	
	SparseArray<Post> posts = null;
	PostsFilter filter = null;
	
	public PostsPagerAdapter(FragmentManager fm)
	{
		super(fm);
		this.filter = new PostsFilter(this);
	}
	
	@Override
	public Fragment getItem(int pos)
	{
		// get reverse index, like the grid adapter
		int size = this.getCount();
		int index = size - pos - 1;
		
		// get a key from the index
		int key = this.posts.keyAt(index);
		// and a post from the key, that's how SparseArray works
		Post post = this.posts.get(key);
		// create the fragment here
		return PostViewFragment.create(post);
	}
	
	@Override
	public long getItemId(int pos)
	{
		// get reverse index, like the grid adapter
		int size = this.getCount();
		int index = size - pos - 1;
		
		// get a key from the index
		int key = this.posts.keyAt(index);
		// and return it
		return key;
	}
	
	@Override
	public int getCount()
	{
		if(this.posts != null)
			return this.posts.size();
		return 0;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		// don't do this
		//super.destroyItem(container, position, object);
		// do this instead:
		Fragment frag = (Fragment)object;
		FragmentManager fragman = frag.getFragmentManager();
		// sometimes...
		if(fragman != null)
		{
			FragmentTransaction fragtrans = fragman.beginTransaction();
			fragtrans.remove(frag);
			fragtrans.commit();
			Logger.i("PostsPagerAdapter::destroyItem", String.format("Destroyed #%s", position));
		}
	}
	
	@Override
	public void onPostsFetched(SparseArray<Post> posts)
	{
		//this.posts = posts;
		this.notifyDataSetChanged();
		if(this.getFilter() != null)
		{
			((PostsFilter)this.getFilter()).filter(posts);
		}
		else
		{
			this.posts = posts;
			this.notifyDataSetChanged();
		}
	}
	
	@Override
	public Filter getFilter()
	{
		return this.filter;
	}
	
	@Override
	public void onFilter(SparseArray<Post> result)
	{
		if(result != null)
			this.posts = result;
		this.notifyDataSetChanged();
		Logger.i("PostsPagerAdapter::onFilter", String.format("Filtered, count: %s", this.getCount()));
		// tells the activity to do things (in this case it changes the page)
		// after filtering for the first time
		if(this.onAfterPostsFilterListener != null)
			this.onAfterPostsFilterListener.onPostFilter();
	}
	
}
