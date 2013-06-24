package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.FilterCallback;
import org.shujito.cartonbox.view.fragments.PostViewFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

public class PostsPagerAdapter extends FragmentPagerAdapter implements
	OnPostsFetchedListener, Filterable, FilterCallback<SparseArray<Post>>
{
	SparseArray<Post> posts = null;
	
	public PostsPagerAdapter(FragmentManager fm)
	{
		super(fm);
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
	public int getCount()
	{
		if(this.posts != null)
			return this.posts.size();
		return 0;
	}
	
	@Override
	public void onPostsFetched(SparseArray<Post> posts)
	{
		this.posts = posts;
		this.notifyDataSetChanged();
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		// don't do this
		//super.destroyItem(container, position, object);
		// do this instead:
		Fragment frag = (Fragment)object;
		FragmentManager fragman = frag.getFragmentManager();
		FragmentTransaction fragtrans = fragman.beginTransaction();
		fragtrans.remove(frag);
		fragtrans.commit();
		
		Logger.i("PostsPagerAdapter::destroyItem", String.format("Destroyed #%s", position));
	}
	
	@Override
	public Filter getFilter()
	{
		return null;
	}
	
	@Override
	public void onFilter(SparseArray<Post> result)
	{
	}
}
