package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PostViewFragment extends Fragment
{
	ImageView mImageView = null;
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		View v = inf.inflate(R.layout.post_item_pager, dad, false);
		return v;
	}
}
