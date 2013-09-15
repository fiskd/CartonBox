package org.shujito.cartonbox.view.activities;

import java.io.File;

import org.shujito.cartonbox.model.Download;
import org.shujito.cartonbox.view.adapters.DownloadsAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;

public class DownloadsActivity extends SherlockListActivity
{
	DownloadsAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.adapter = new DownloadsAdapter(this);
		this.setListAdapter(this.adapter);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		this.adapter.refreshDownloads();
		//this.adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// do things
		Download download = (Download)this.getListAdapter().getItem(position);
		Intent ntn = new Intent(Intent.ACTION_VIEW);
		ntn.setDataAndType(Uri.fromFile(new File(download.getLocation())), "image/*");
		this.startActivity(ntn);
	}
}
