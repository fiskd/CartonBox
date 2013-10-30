package org.shujito.cartonbox.view.fragment.dialog;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.task.JsonDownloader;
import org.shujito.cartonbox.controller.task.SitesJsonDownloader;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.util.ConcurrentTask;
import org.shujito.cartonbox.view.adapter.DefaultSitesAdapter;
import org.shujito.cartonbox.view.fragment.dialog.listener.AddSiteDialogCallback;

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
	
	AddSiteDialogCallback callback = null;
	
	ListView lvSites = null;
	DefaultSitesAdapter defaultSitesAdapter = null;
	JsonDownloader downloader = null;
	
	public AddDefaultSiteDialogFragment() { }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if(activity instanceof AddSiteDialogCallback)
		{ this.callback = (AddSiteDialogCallback)activity; }
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
		
		/*
		int lightdarktheme = com.actionbarsherlock.R.style.Theme_Sherlock_Light_DarkActionBar;
		int currentTheme = 0;
		try
		{ currentTheme = this.getActivity().getPackageManager().getActivityInfo(this.getActivity().getComponentName(), 0).theme; }
		catch(Exception ex)
		{ }
		//*/
		
		return new AlertDialog.Builder(this.getActivity())
			.setInverseBackgroundForced(true)
			.setTitle(R.string.addsite)
			.setView(v)
			.setNegativeButton(android.R.string.cancel, null)
			.create();
	}
	
	@Override
	public void onItemClick(AdapterView<?> ada, View v, int pos, long id)
	{
		//Toast.makeText(this.getActivity(), String.format("selected %s", pos), Toast.LENGTH_SHORT).show();
		Site site = (Site)this.defaultSitesAdapter.getItem(pos);
		if(this.callback != null)
		{
			// TODO: download picture
			this.callback.onOk(site);
			this.dismiss();
		}
	}
}
