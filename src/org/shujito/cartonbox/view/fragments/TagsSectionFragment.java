package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listeners.OnFragmentAttachedListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TagsSectionFragment extends Fragment
{
	/* Listeners */
	OnFragmentAttachedListener onFragmentAttachedListener = null;
	
	/* Fields */
	ImageboardTags tagsApi = null;
	
	/* Constructor */
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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.tags_section, dad, false);
	}
}
