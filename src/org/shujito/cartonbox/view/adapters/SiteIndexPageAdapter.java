package org.shujito.cartonbox.view.adapters;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.fragments.EmptyFragment;
import org.shujito.cartonbox.view.fragments.PostsGridFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SiteIndexPageAdapter extends FragmentPagerAdapter
{
	String[] pages = null;
	Context context = null;
	
	public SiteIndexPageAdapter(FragmentManager fm, Context context)
	{
		super(fm);
		this.context = context;
		this.pages = this.context.getResources().getStringArray(R.array.danbooru_sections);
	}
	
	@Override
	public Fragment getItem(int pos)
	{
		if(this.context.getResources().getString(R.string.section_posts).equals(this.pages[pos]))
			return new PostsGridFragment();
		
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
