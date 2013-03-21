package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.view.fragments.PostViewFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PostsPagerAdapter extends FragmentPagerAdapter
{
	public PostsPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}
	
	@Override
	public Fragment getItem(int pos)
	{
		return new PostViewFragment();
	}

	@Override
	public int getCount()
	{
		return 0;
	}
}
