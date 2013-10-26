package org.shujito.cartonbox.view.fragment.dialog;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.task.JsonDownloader;
import org.shujito.cartonbox.controller.task.SitesJsonDownloader;
import org.shujito.cartonbox.util.ConcurrentTask;
import org.shujito.cartonbox.view.adapter.DefaultSitesAdapter;

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
	public static final String TAG = "org.shujito.cartonbox.view.fragments.dialogs.AddDefaultSiteDialogFragment";
	
	ListView lvSites = null;
	DefaultSitesAdapter defaultSitesAdapter = null;
	JsonDownloader downloader = null;
	
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
		// create the adapter
		this.defaultSitesAdapter = new DefaultSitesAdapter(this.getActivity());
		// download sites with this
		this.downloader = new SitesJsonDownloader();
		// make the adapter to listen for the request
		this.downloader.setOnErrorListener(this.defaultSitesAdapter);
		this.downloader.setOnResponseReceivedListener(this.defaultSitesAdapter);
		ConcurrentTask.execute(this.downloader);
		// get the listview
		this.lvSites = (ListView)v.findViewById(R.id.lvList);
		this.lvSites.setOnItemClickListener(this);
		this.lvSites.setAdapter(this.defaultSitesAdapter);
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
