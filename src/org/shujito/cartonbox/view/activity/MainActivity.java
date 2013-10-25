package org.shujito.cartonbox.view.activity;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.Preferences;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.DanbooruImageBoardPosts;
import org.shujito.cartonbox.controller.DanbooruImageBoardTags;
import org.shujito.cartonbox.controller.DanbooruOldImageBoardPosts;
import org.shujito.cartonbox.controller.DanbooruOldImageBoardTags;
import org.shujito.cartonbox.controller.ImageboardApis;
import org.shujito.cartonbox.controller.ImageboardPosts;
import org.shujito.cartonbox.controller.ImageboardTags;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.DownloadsDB;
import org.shujito.cartonbox.model.db.SitesDB;
import org.shujito.cartonbox.util.ConcurrentTask;
import org.shujito.cartonbox.util.io.DiskUtils;
import org.shujito.cartonbox.util.io.listener.OnDirectoryClearedListener;
import org.shujito.cartonbox.util.io.listener.OnDiskTaskProgressListener;
import org.shujito.cartonbox.util.io.task.ClearDirectoryTask;
import org.shujito.cartonbox.view.adapter.SitesAdapter;
import org.shujito.cartonbox.view.fragment.dialog.AddOrEditSiteDialogFragment;
import org.shujito.cartonbox.view.fragment.dialog.listener.AddSiteDialogCallback;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity
	implements OnItemClickListener, OnItemLongClickListener, DialogInterface.OnClickListener, AddSiteDialogCallback
{
	GridView mGridView = null;
	TextView mTvNosites = null;
	SitesAdapter mSitesAdapter = null;
	//List<Site> sites = null;
	Site selectedSite = null;
	MenuItem itemDownloads = null;
	boolean anyDownloads = false;
	boolean anyFavorites = false;
	
	@Override
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.setContentView(R.layout.main);
		
		// init views
		this.mTvNosites = (TextView)this.findViewById(R.id.tvNosites);
		this.mSitesAdapter = new SitesAdapter(this);
		
		this.mGridView = (GridView)this.findViewById(R.id.gvSites);
		this.mGridView.setAdapter(this.mSitesAdapter);
		this.mGridView.setOnItemClickListener(this);
		this.mGridView.setOnItemLongClickListener(this);
		this.registerForContextMenu(this.mGridView);
		
		this.mTvNosites.setVisibility(this.mSitesAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
		
		// count adapter size
		//if(this.mSitesAdapter.getCount() == 0)
		//{
			// upgraded or anything?
			//this.addSite();
		//}
		
		//BlogNewsDialog dialog = new BlogNewsDialog(this);
		//dialog.createDialog().show();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// get rid of the apis in the application
		if(CartonBox.getInstance() != null && CartonBox.getInstance().getApis() != null)
		{
			CartonBox.getInstance().getApis().setImageboardPosts(null);
			CartonBox.getInstance().getApis().setImageboardTags(null);
			CartonBox.getInstance().setApis(null);
		}
		
		DownloadsDB downloads = new DownloadsDB(this);
		this.anyDownloads = downloads.getCount() > 0;
		
		if(this.itemDownloads != null)
			this.itemDownloads.setVisible(this.anyDownloads);
		
		if(this.mSitesAdapter != null)
			this.mSitesAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Logger.i(this.getClass().getSimpleName(), "onCreateOptionsMenu");
		this.getSupportMenuInflater().inflate(R.menu.main, menu);
		
		this.itemDownloads = menu.findItem(R.id.downloads).setVisible(this.anyDownloads);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item)
	{
		Logger.i(this.getClass().getSimpleName(), "onOptionsItemSelected");
		switch(item.getItemId())
		{
			case R.id.addsite_template:
				this.chooseDefaultSite();
				return true;
			case R.id.addsite_custom:
				this.addCustomSite();
				return true;
			case R.id.downloads:
				Intent ntn = new Intent(this, DownloadsActivity.class);
				this.startActivity(ntn);
				return true;
			case R.id.settings:
				Intent ntnPrefs = new Intent(this, GeneralPreferencesActivity.class);
				this.startActivity(ntnPrefs);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		this.getMenuInflater().inflate(R.menu.main_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.edit:
				this.editSite();
				return true;
			case R.id.remove:
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setMessage(R.string.surewanttoremove)
					.setPositiveButton(android.R.string.yes, this)
					.setNegativeButton(android.R.string.no, null);
				builder.create().show();
				return true;
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onBackPressed()
	{
		if(Preferences.getBool(R.string.pref_general_clearcacheonexit_key))
		{
			final NotificationManager notman = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
			final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
			// this fixes app crash on 2.3.x
			PendingIntent pending = PendingIntent.getActivity(this, R.string.app_name, new Intent(), 0);
			builder.setSmallIcon(R.drawable.ic_notif);
			builder.setOngoing(true);
			builder.setContentTitle(this.getString(R.string.pref_general_clearcacheonexit_clearing));
			builder.setTicker(this.getString(R.string.pref_general_clearcacheonexit_clearing));
			builder.setProgress(0, 0, true);
			// this fixes app crash on 2.3.x
			builder.setContentIntent(pending);
			// this resource id is used as a notification identifier
			notman.notify(R.string.app_name, builder.build());
			
			ClearDirectoryTask clearDirectory = new ClearDirectoryTask(DiskUtils.getCacheDirectory(this));
			clearDirectory.setOnDiskTaskProgress(new OnDiskTaskProgressListener()
			{
				@Override
				public void onDiskTaskProgress(int total, int complete)
				{
					builder.setProgress(total, complete, false);
					builder.setContentText(String.format("%s/%s", complete, total));
					notman.notify(R.string.app_name, builder.build());
				}
			});
			clearDirectory.setOnDirectoryClearedListener(new OnDirectoryClearedListener()
			{
				@Override
				public void onDirectoryCleared()
				{
					notman.cancel(R.string.app_name);
				}
			});
			ConcurrentTask.execute(clearDirectory);
		}
		
		super.onBackPressed();
	}
	
	/* OnItemClickListener methods */
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int pos, long id)
	{
		Site currentSite = (Site)this.mSitesAdapter.getItem(pos);
		
		// create apis here
		ImageboardApis apis = new ImageboardApis();
		ImageboardPosts postsApi = null;
		ImageboardTags tagsApi = null;
		
		// place it on the application
		CartonBox.getInstance().setApis(apis);
		
		// prefs!
		SharedPreferences globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String sPostsPerPage = globalPrefs.getString(this.getString(R.string.pref_content_postsperpage_key), "20");
		int postsPerPage = Integer.parseInt(sPostsPerPage);
		
		if(currentSite.getType() == Site.Type.Danbooru1)
		{
			postsApi = new DanbooruOldImageBoardPosts(currentSite);
			tagsApi =new DanbooruOldImageBoardTags(currentSite);
		}
		else if(currentSite.getType() == Site.Type.Danbooru2)
		{
			SharedPreferences sitePrefs = this.getSharedPreferences(String.valueOf(currentSite.getId()), 0);
			String username = sitePrefs.getString(Preferences.SITE_USERNAME, null);
			String password = sitePrefs.getString(Preferences.SITE_PASSWORD, null);
			
			postsApi = new DanbooruImageBoardPosts(currentSite);
			postsApi.setUsername(username);
			postsApi.setPassword(password);
			
			tagsApi = new DanbooruImageBoardTags(currentSite);
			tagsApi.setUsername(username);
			tagsApi.setPassword(password);
		}
		
		// set apis on the apis class
		if(postsApi != null)
		{
			postsApi.setPostsPerPage(postsPerPage);
			apis.setImageboardPosts(postsApi);
		}
		if(tagsApi != null)
			apis.setImageboardTags(tagsApi);
		
		Intent ntn = new Intent(this, SiteIndexActivity.class);
		ntn.putExtra(SiteIndexActivity.EXTRA_SECTIONPAGE, R.string.section_posts);
		this.startActivity(ntn);
	}
	/* OnItemClickListener methods */
	
	/* OnItemLongClickListener methods */
	@Override
	public boolean onItemLongClick(AdapterView<?> dad, View v, int pos, long id)
	{
		this.selectedSite = (Site)this.mSitesAdapter.getItem(pos);
		this.openContextMenu(this.mGridView);
		return true;
	}
	/* OnItemLongClickListener methods */
	
	private void addCustomSite()
	{
		AddOrEditSiteDialogFragment asdf = new AddOrEditSiteDialogFragment();
		asdf.show(this.getSupportFragmentManager(), AddOrEditSiteDialogFragment.TAG);
	}
	
	private void chooseDefaultSite()
	{
		
	}
	
	private void editSite()
	{
		AddOrEditSiteDialogFragment asdf = new AddOrEditSiteDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable(AddOrEditSiteDialogFragment.EXTRA_SITE, this.selectedSite);
		asdf.setArguments(args);
		asdf.show(this.getSupportFragmentManager(), AddOrEditSiteDialogFragment.TAG);
	}
	
	/* AddSiteDialogCallback methods */
	@Override
	public void onOk(Site site)
	{
		SitesDB sites = new SitesDB(this);
		if(!sites.add(site))
		{
			sites.update(site);
		}
		
		if(this.mSitesAdapter != null)
			this.mSitesAdapter.notifyDataSetChanged();
		
		this.mTvNosites.setVisibility(this.mSitesAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public void onCancel()
	{
		// not used?
		this.mTvNosites.setVisibility(this.mSitesAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
	}
	/* AddSiteDialogCallback methods */
	
	/* android.content.DialogInterface.OnClickListener methods */
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		SitesDB sitesdb = new SitesDB(this);
		if(sitesdb.delete(this.selectedSite))
		{
			this.mSitesAdapter.notifyDataSetChanged();
			this.mTvNosites.setVisibility(this.mSitesAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
		}
	}
	/* android.content.DialogInterface.OnClickListener methods */
}
