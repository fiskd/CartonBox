package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class PoolsSectionFragment extends Fragment
{
	ProgressBar mPbProgress = null;
	
	@Override
	public void onActivityCreated(Bundle cirno)
	{
		super.onActivityCreated(cirno);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.pools_section, dad, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle cirno)
	{
		super.onViewCreated(view, cirno);
		
		this.mPbProgress = (ProgressBar)view.findViewById(R.id.pools_pbloading);
		this.mPbProgress.setVisibility(View.GONE);
	}
}
