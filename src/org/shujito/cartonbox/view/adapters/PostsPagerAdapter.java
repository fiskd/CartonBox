package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.fragments.PostViewFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class PostsPagerAdapter extends FragmentPagerAdapter implements OnPostsFetchedListener
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
		// eh don't?
		//super.destroyItem(container, position, object);
		Logger.i("PostsPagerAdapter::destroyItem", String.format("Destroyed #%s", position));
	}
}
