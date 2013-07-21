package org.shujito.cartonbox.view.fragments.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class PostTagsDialogFragment extends SherlockDialogFragment
	implements OnItemClickListener, OnClickListener
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
		// put derr posts here
		List<String> things = new ArrayList<String>();
		
		Post post = (Post)this.getArguments().getSerializable(EXTRA_POSTOBJECT);
		if(post != null)
		{
			String[] tags = post.getTags().split("\\s+");
			for(String t : tags)
			{
				things.add(t);
			}
		}
		
		LayoutInflater inf = LayoutInflater.from(this.getActivity());
		View v = inf.inflate(R.layout.dialog_posttags, null);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, things);
		
		this.lvTags = (ListView)v.findViewById(R.id.dialog_posttags_lvtags);
		this.lvTags.setAdapter(adapter);
		this.lvTags.setOnItemClickListener(this);
		this.lvTags.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		return new AlertDialog.Builder(this.getActivity())
			.setTitle(String.format("Post #%d", post.getId()))
			//.setPositiveButton(android.R.string.search_go, null)
			//.setNegativeButton(android.R.string.cancel, null)
			.setView(v)
			.create();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		((AlertDialog)this.getDialog())
			.getButton(DialogInterface.BUTTON_POSITIVE)
			.setOnClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
	}
	
	@Override
	public void onClick(View v)
	{
		//long[] ids = this.lvTags.getCheckedItemIds();
		//int count = ids.length;
		//Toast.makeText(this.getActivity(), String.valueOf(count), Toast.LENGTH_SHORT).show();
	}
}
