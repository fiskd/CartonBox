package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.view.fragments.EmptyFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SiteIndexPageAdapter extends FragmentPagerAdapter
{
	String[] pages = null;
	
	public SiteIndexPageAdapter(FragmentManager fm, String[] pages)
	{
		super(fm);
		this.pages = pages;
	}
	
	@Override
	public Fragment getItem(int pos)
	{
		return new EmptyFragment();
	}
	
	@Override
	public int getCount()
	{
		if(this.pages != null)
			return this.pages.length;
		return 0;
	}
}
