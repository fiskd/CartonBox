package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listeners.OnFragmentAttachedListener;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class TagsSectionFragment extends Fragment
{
	/* Listeners */
	OnFragmentAttachedListener onFragmentAttachedListener = null;
	ImageboardTags tagsApi = null;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			this.onFragmentAttachedListener = (OnFragmentAttachedListener)activity;
		}
		catch(Exception ex)
		{
			Logger.e("TagsSectionFragment::onAttach", "Couldn't get listener from the activity this is attached to");
		}
	}
}
