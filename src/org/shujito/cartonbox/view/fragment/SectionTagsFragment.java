package org.shujito.cartonbox.view.fragment;

import java.net.HttpURLConnection;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.controller.listener.OnErrorListener;
import org.shujito.cartonbox.controller.listener.OnRequestListener;
import org.shujito.cartonbox.controller.listener.OnTagsFetchedListener;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.view.adapter.TagsAdapter;
import org.shujito.cartonbox.view.listener.OnFragmentAttachedListener;
import org.shujito.cartonbox.view.listener.TagListItemSelectedCallback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SectionTagsFragment extends Fragment implements
	OnItemClickListener, OnErrorListener, OnTagsFetchedListener,
	OnRequestListener
{
	/* static */
	public static Fragment create()
	{
		Fragment f = new SectionTagsFragment();
		return f;
	}
	
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
		Logger.i("SectionTagsFragment::onAttach", "overly attached fragment");
		super.onAttach(activity);
		
		if(activity instanceof OnFragmentAttachedListener)
		{ this.onFragmentAttachedListener = (OnFragmentAttachedListener)activity; }
		
		if(activity instanceof TagListItemSelectedCallback)
		{ this.tagListItemSelectedCallback = (TagListItemSelectedCallback)activity; }
	}
	
	@Override
	public void onActivityCreated(Bundle cirno)
	{
		super.onActivityCreated(cirno);
	}
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup dad, Bundle cirno)
	{
		return inf.inflate(R.layout.section_tags, dad, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle cirno)
	{
		Logger.i("SectionTagsFragment::onViewCreated", "fragment created");
		super.onViewCreated(view, cirno);
		
		this.mTagsAdapter = new TagsAdapter(this.getActivity());
		
		if(this.onFragmentAttachedListener != null)
		{
			this.tagsApi = (ImageboardTags)this.onFragmentAttachedListener.onFragmentAttached(this);
		}
		
		this.mLvTags = (ListView)view.findViewById(R.id.lvTags);
		this.mLvTags.setAdapter(this.mTagsAdapter);
		this.mLvTags.setOnItemClickListener(this);
		this.mLvTags.setVisibility(View.GONE);
		
		this.mPbProgress = (ProgressBar)view.findViewById(R.id.pbProgress);
		this.mPbProgress.setVisibility(View.VISIBLE);
		
		this.mTvMessage = (TextView)view.findViewById(R.id.tvMessage);
		this.mTvMessage.setVisibility(View.GONE);
		
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
	public void onDestroy()
	{
		// get rid of these things
		this.tagsApi = null;
		this.onFragmentAttachedListener = null;
		this.tagListItemSelectedCallback = null;
		// remove these listeners
		if(this.tagsApi != null)
		{
			// I'm scared of leaving listerens alive since their assignation
			this.tagsApi.removeOnRequestListener(this);
			this.tagsApi.removeOnErrorListener(this);
			this.tagsApi.removeOnTagsFetchedListeners(this.mTagsAdapter);
			this.tagsApi.removeOnTagsFetchedListeners(this);
		}
		// this can now be destroyed
		super.onDestroy();
		Logger.i("SectionTagsFragment::onDestroy", "fragment destroyed");
	}
	
	/* OnTagsFetchedListener methods */
	@Override
	public void onError(int errCode, String message)
	{
		//Toast.makeText(this.getActivity(), String.format("%s: %s", errCode, message), Toast.LENGTH_LONG).show();
		if(errCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT && this.mTagsAdapter != null && this.mTagsAdapter.getCount() != 0)
		{
			Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
			// request again...
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if(tagsApi != null)
						tagsApi.request();
				}
			}, 5000);
		}
		else
		{
			this.mLvTags.setVisibility(View.GONE);
			this.mPbProgress.setVisibility(View.GONE);
			this.mTvMessage.setVisibility(View.VISIBLE);
			this.mTvMessage.setText(message);
		}
	}
	/* OnTagsFetchedListener methods */
	
	/* OnItemClickListener methods */
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
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
		
		if(tags != null && tags.size() > 0)
		{
			this.mLvTags.smoothScrollToPosition(0);
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
