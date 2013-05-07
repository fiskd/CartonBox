package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.controller.Imageboard;
import org.shujito.cartonbox.controller.listeners.OnPostsFetchedListener;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.fragments.PostViewFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

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
		PostViewFragment fragment = null;
		// get reverse index, like the grid adapter
		int size = this.getCount();
		int index = size - pos - 1;
		
		// get a key from the index
		int key = this.posts.keyAt(index);
		// and a post from the key, that's how SparseArray works
		Post one = this.posts.get(key);
		// create the fragment here
		fragment = PostViewFragment.create(one);
		
		return fragment;
	}
	
	@Override
	public int getCount()
	{
		if(this.posts != null)
			return this.posts.size();
		return 0;
	}
	
	@Override
	public void onPostsFetched(Imageboard api)
	{
		this.posts = api.getPosts();
		this.notifyDataSetChanged();
	}
}
