package org.shujito.cartonbox.view.fragments.dialogs;

import org.shujito.cartonbox.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class BlogNewsDialogFragment extends DialogFragment
{
	ListView lvEntries = null;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle cirno)
	{
		LayoutInflater inf = LayoutInflater.from(this.getActivity());
		
		View v = inf.inflate(R.layout.dialog_blognews, null);
		
		this.lvEntries = (ListView)v.findViewById(R.id.dialog_blognews_lventries);
		
		return null;
	}
}
