package org.shujito.cartonbox.view.fragments.dialogs;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.view.activities.SiteIndexActivity;
import org.shujito.cartonbox.view.adapters.PostDetailAdapter;
import org.shujito.cartonbox.view.adapters.PostDetailAdapter.Detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class PostDetailDialogFragment extends SherlockDialogFragment
	implements OnItemClickListener, DialogInterface.OnClickListener
{
	public final static String TAG = "org.shujito.cartonbox.view.fragments.dialogs.PostTagsDialogFragment";
	public final static String EXTRA_POSTOBJECT = "org.shujito.cartonbox.POSTOBJECT";
	
	PostDetailAdapter detailAdapter = null;
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
		View v = inf.inflate(R.layout.dialog_listview, null);
		
		this.detailAdapter = new PostDetailAdapter(this.getActivity(), post);
		
		this.lvTags = (ListView)v.findViewById(R.id.lvTags);
		this.lvTags.setAdapter(detailAdapter);
		this.lvTags.setOnItemClickListener(this);
		this.lvTags.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		return new AlertDialog.Builder(this.getActivity())
			.setTitle(String.format("Post #%d", post.getId()))
			.setPositiveButton(android.R.string.search_go, this)
			.setNegativeButton(android.R.string.cancel, null)
			.setView(v)
			.create();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		((AlertDialog)this.getDialog()).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
	}
	
	@Override
	public void onItemClick(AdapterView<?> ada, View v, int pos, long id)
	{
		Detail one = (Detail)detailAdapter.getItem(pos);
		one.setEnabled(!one.isEnabled());
		this.detailAdapter.notifyDataSetChanged();
		
		Button btn = ((AlertDialog)this.getDialog()).getButton(Dialog.BUTTON_POSITIVE);
		btn.setEnabled(false);
		
		for(int idx = 0; idx < this.detailAdapter.getCount(); idx++)
		{
			Detail detail = (Detail)this.detailAdapter.getItem(idx);
			if(detail.isEnabled())
			{
				btn.setEnabled(true);
				break;
			}
		}
		
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int idx = 0; idx < this.detailAdapter.getCount(); idx++)
		{
			Detail detail = (Detail)this.detailAdapter.getItem(idx);
			if(detail.getTag() != null && detail.isEnabled())
			{
				Logger.i(this.getClass().getSimpleName(), detail.getTag().getName());
				if(sb.length() != 0)
					sb.append(' ');
				sb.append(detail.getTag().getName());
			}
		}
		Logger.i(this.getClass().getSimpleName(), sb.toString());
		
		//*
		Intent ntn = new Intent(this.getActivity(), SiteIndexActivity.class);
		ntn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ntn.setAction(Intent.ACTION_SEARCH);
		// XXX: is this hacky?
		ntn.putExtra(SearchManager.QUERY, sb.toString());
		this.getActivity().startActivity(ntn);
		//*/
	}
}
