package org.shujito.cartonbox.view.adapter;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.fragment.EmptyFragment;
import org.shujito.cartonbox.view.fragment.SectionPostsFragment;
import org.shujito.cartonbox.view.fragment.SectionTagsFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SiteIndexPageAdapter extends FragmentPagerAdapter
{
	String[] pages = null;
	Context context = null;
	
	public SiteIndexPageAdapter(FragmentManager fm, Context context, String[] pages)
	{
		super(fm);
		this.context = context;
		this.pages = pages;
		//this.pages = this.context.getResources().getStringArray(R.array.danbooru_sections);
	}
	
	@Override
	public Fragment getItem(int pos)
	{
		try
		{
			if(this.context.getResources().getString(R.string.section_posts).equals(this.pages[pos]))
				return SectionPostsFragment.create();
			if(this.context.getResources().getString(R.string.section_tags).equals(this.pages[pos]))
				return SectionTagsFragment.create();
		}
		catch(Exception ex)
		{
			Logger.e("SiteIndexPageAdapter::getItem", ex.getMessage(), ex);
		}
		
		return new EmptyFragment();
	}
	
	@Override
	public int getCount()
	{
		if(this.pages != null)
			return this.pages.length;
		return 0;
	}
	
	@Override
	public float getPageWidth(int pos)
	{
		if(this.context.getResources().getString(R.string.section_tags).equals(this.pages[pos]))
		{
			return context.getResources().getFraction(R.dimen.tags_page_width, 1, 1);
		}
		
		return 1f;
	}
}
