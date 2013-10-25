package org.shujito.cartonbox.view.fragment.dialog;

import org.shujito.cartonbox.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AddDefaultSiteDialogFragment extends SherlockDialogFragment
	implements OnItemClickListener
{
	ListView lvSites = null;
	
	public AddDefaultSiteDialogFragment() { }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle cirno)
	{
		LayoutInflater inf = this.getActivity().getLayoutInflater();
		View v = inf.inflate(R.layout.dialog_listview, null);
		// get the listview
		this.lvSites = (ListView)v.findViewById(R.id.lvList);
		this.lvSites.setOnItemClickListener(this);
		return new AlertDialog.Builder(this.getActivity())
			.setTitle(R.string.addsite)
			.setView(v)
			.setNegativeButton(android.R.string.cancel, null)
			.create();
	}
	
	@Override
	public void onItemClick(AdapterView<?> ada, View v, int pos, long id)
	{
		
	}
}
