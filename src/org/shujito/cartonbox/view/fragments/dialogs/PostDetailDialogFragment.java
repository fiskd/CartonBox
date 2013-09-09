package org.shujito.cartonbox.view.fragments.dialogs;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.activities.SiteIndexActivity;
import org.shujito.cartonbox.view.adapters.PostDetailAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class PostDetailDialogFragment extends SherlockDialogFragment
{
	public final static String TAG = "org.shujito.cartonbox.view.fragments.dialogs.PostTagsDialogFragment";
	public final static String EXTRA_POSTOBJECT = "org.shujito.cartonbox.POSTOBJECT";
	
	ListView lvTags = null;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle cirno)
	{
		Post post = (Post)this.getArguments().getSerializable(EXTRA_POSTOBJECT);
		LayoutInflater inf = LayoutInflater.from(this.getActivity());
		View v = inf.inflate(R.layout.dialog_posttags, null);
		
		PostDetailAdapter detailAdapter = new PostDetailAdapter(this.getActivity(), post);
		
		this.lvTags = (ListView)v.findViewById(R.id.lvTags);
		this.lvTags.setAdapter(detailAdapter);
		this.lvTags.setOnItemClickListener(detailAdapter);
		this.lvTags.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		return new AlertDialog.Builder(this.getActivity())
			.setTitle(String.format("Post #%d", post.getId()))
			//.setPositiveButton(android.R.string.search_go, null)
			//.setNegativeButton(android.R.string.cancel, null)
			.setView(v)
			.create();
	}
	
	//@Override
	public void onItemClick(AdapterView<?> ada, View v, int idx, long id)
	{
		String item = (String)this.lvTags.getAdapter().getItem(idx);
		Intent ntn = new Intent(this.getActivity(), SiteIndexActivity.class);
		ntn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ntn.setAction(Intent.ACTION_SEARCH);
		// XXX: is this hacky?
		ntn.putExtra(SearchManager.QUERY, item);
		this.getActivity().startActivity(ntn);
	}
}
