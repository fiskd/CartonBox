package org.shujito.cartonbox.view.fragments;

import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnRequestListener;
import org.shujito.cartonbox.controller.listeners.OnTagsFetchedListener;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.view.adapters.TagsAdapter;
import org.shujito.cartonbox.view.listeners.OnFragmentAttachedListener;
import org.shujito.cartonbox.view.listeners.TagListItemSelectedCallback;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TagsSectionFragment extends Fragment implements
	OnItemClickListener, OnErrorListener, OnTagsFetchedListener,
	OnRequestListener
{
	/* Listeners */
	OnFragmentAttachedListener onFragmentAttachedListener = null;
	TagListItemSelectedCallback tagListItemSelectedCallback = null;
	
	/* Fields */
	ImageboardTags tagsApi = null;
	
	ListView mLvTags = null;
	ProgressBar mPbProgress = null;
	TextView mTvMessage = null;
	
	TagsAdapter mTagsAdapter = null;
	
	/* Constructor */
	@Override
	public void onAttach(Activity activity)
	{
		Logger.i("TagsSectionFragment::onAttach", "overly attached fragment");
		super.onAttach(activity);
		
		try
		{ this.onFragmentAttachedListener = (OnFragmentAttachedListener)activity; }
		catch(Exception ex)
		{ Logger.e("TagsSectionFragment::onAttach", "Couldn't get OnFragmentAttachedListener from the activity this fragment is attached to"); }
		
		try
		{ this.tagListItemSelectedCallback = (TagListItemSelectedCallback)activity; }
		catch(Exception ex)
		{ Logger.e("TagsSectionFragment::onAttach", "Couldn't get TagListItemSelectedCallback from the activity this fragment is attached to"); }
	}
	
	@Override
	public void onActivityCreated(Bundle cirno)
	{
		super.onActivityCreated(cirno);
	}
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.tags_section, dad, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle cirno)
	{
		Logger.i("TagsSectionFragment::onViewCreated", "fragment created");
		super.onViewCreated(view, cirno);
		
		this.mTagsAdapter = new TagsAdapter(this.getActivity());
		
		if(this.onFragmentAttachedListener != null)
		{
			this.tagsApi = (ImageboardTags)this.onFragmentAttachedListener.onFragmentAttached(this);
		}
		
		this.mLvTags = (ListView)view.findViewById(R.id.tags_lvtags);
		this.mLvTags.setAdapter(this.mTagsAdapter);
		this.mLvTags.setOnItemClickListener(this);
		this.mLvTags.setVisibility(View.GONE);
		
		this.mPbProgress = (ProgressBar)view.findViewById(R.id.tags_pbprogress);
		this.mPbProgress.setVisibility(View.VISIBLE);
		
		this.mTvMessage = (TextView)view.findViewById(R.id.tags_tvmessage);
		this.mTvMessage.setVisibility(View.GONE);
	}
	
	@Override
	public void onResume()
	{
		Logger.i("TagsSectionFragment::onResume", "fragment resumed");
		super.onResume();
		if(this.tagsApi != null)
		{
			this.tagsApi.addOnRequestListener(this);
			this.tagsApi.addOnErrorListener(this);
			this.tagsApi.addOnTagsFetchedListeners(this.mTagsAdapter);
			this.tagsApi.addOnTagsFetchedListeners(this);
			// is this right?
			this.tagsApi.request();
		}
	}
	
	@Override
	public void onPause()
	{
		Logger.i("TagsSectionFragment::onPause", "fragment paused");
		super.onPause();
		if(this.tagsApi != null)
		{
			this.tagsApi.removeOnRequestListener(this);
			this.tagsApi.removeOnErrorListener(this);
			this.tagsApi.removeOnTagsFetchedListeners(this.mTagsAdapter);
			this.tagsApi.removeOnTagsFetchedListeners(this);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// get rid of these things
		this.tagsApi = null;
		this.onFragmentAttachedListener = null;
		this.tagListItemSelectedCallback = null;
	}
	
	/* OnTagsFetchedListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		this.mLvTags.setVisibility(View.GONE);
		this.mPbProgress.setVisibility(View.GONE);
		this.mTvMessage.setVisibility(View.VISIBLE);
		this.mTvMessage.setText(message);
	}
	/* OnTagsFetchedListener methods */
	
	/* OnItemClickListener methods */
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
		// TODO: talk with the activity
		//Toast.makeText(this.getActivity(), this.mTagsAdapter.getItem(pos).toString(), Toast.LENGTH_SHORT).show();
		String tag = this.mTagsAdapter.getItem(pos).toString();
		if(this.tagListItemSelectedCallback != null)
		{
			this.tagListItemSelectedCallback.tagListItemSelected(tag);
		}
	}
	/* OnItemClickListener methods */
	
	/* OnErrorListener methods */
	@Override
	public void onTagsFetchedListener(List<Tag> tags)
	{
		this.mLvTags.setVisibility(View.VISIBLE);
		this.mPbProgress.setVisibility(View.GONE);
		this.mTvMessage.setVisibility(View.GONE);
		
		if(tags != null && tags.size() == 0)
		{
			this.mLvTags.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.VISIBLE);
			this.mTvMessage.setText(R.string.no_tags);
		}
	}
	/* OnErrorListener methods */

	@Override
	public void onRequest()
	{
		if(this.mLvTags != null)
			this.mLvTags.setVisibility(View.GONE);
		if(this.mPbProgress != null)
			this.mPbProgress.setVisibility(View.VISIBLE);
		if(this.mTvMessage != null)
			this.mTvMessage.setVisibility(View.GONE);
	}
}
