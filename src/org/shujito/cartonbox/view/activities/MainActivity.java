package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.adapters.SitesAdapter;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements OnItemClickListener
{
	GridView mGridView = null;
	SitesAdapter mSitesAdapter = null;
	String[] mSites = null;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		
		this.mSites = this.getResources().getStringArray(R.array.danbooru_site_urls);
		this.mSitesAdapter = new SitesAdapter(this);
		
		this.mGridView = new GridView(this);
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.mGridView.setLayoutParams(lparams);
		this.mGridView.setGravity(Gravity.CENTER);
		
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			this.mGridView.setNumColumns(2);
		else
			this.mGridView.setNumColumns(4);
		
		this.mGridView.setAdapter(this.mSitesAdapter);
		this.mGridView.setOnItemClickListener(this);
		
		this.setContentView(this.mGridView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_main_addsite:
			return true;
		case R.id.menu_main_settings:
			Intent ntnPrefs = new Intent(this, GeneralPreferencesActivity.class);
			this.startActivity(ntnPrefs);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/* OnItemClickListener methods */
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
		Intent ntn = new Intent(this, SiteIndexActivity.class);
		ntn.putExtra(SiteIndexActivity.EXTRA_SITEURL, this.mSites[pos]);
		ntn.putExtra(SiteIndexActivity.EXTRA_SECTIONPAGE, R.string.section_pools);
		this.startActivity(ntn);
	}
	/* OnItemClickListener methods */
}
