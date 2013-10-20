package org.shujito.cartonbox.view.activities;

import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;
import org.shujito.cartonbox.utils.ConcurrentTask;
import org.shujito.cartonbox.utils.Formatters;
import org.shujito.cartonbox.utils.io.ClearDirectoryTask;
import org.shujito.cartonbox.utils.io.DirectorySizeTask;
import org.shujito.cartonbox.utils.io.DiskUtils;
import org.shujito.cartonbox.utils.io.listeners.DirectorySizeCallback;
import org.shujito.cartonbox.utils.io.listeners.OnDirectoryClearedListener;
import org.shujito.cartonbox.utils.io.listeners.OnDiskTaskProgressListener;
import org.shujito.cartonbox.view.BlogNewsDialog;
import org.shujito.cartonbox.view.fragments.PreferencesFragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class GeneralPreferencesActivity extends SherlockPreferenceActivity
	implements OnSharedPreferenceChangeListener, OnPreferenceClickListener
{
	// things
	PreferenceScreen prefScreen = null;
	// the rest
	PreferenceCategory prefSites = null;
	ListPreference prefCacheSize = null;
	Preference prefClearCache = null;
	CheckBoxPreference prefRatingSafe = null;
	CheckBoxPreference prefRatingQuestionable = null;
	CheckBoxPreference prefRatingExplicit = null;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void initializeFromFragment(PreferenceFragment f)
	{
		this.prefScreen = f.getPreferenceScreen();
		
		this.prefSites = (PreferenceCategory)f.findPreference(this.getString(R.string.pref_sites_key));
		this.prefCacheSize = (ListPreference)f.findPreference(this.getString(R.string.pref_general_cachesize_key));
		this.prefClearCache = (Preference)f.findPreference(this.getString(R.string.pref_general_clearcache_key));
		this.prefRatingSafe = (CheckBoxPreference)f.findPreference(this.getString(R.string.pref_ratings_todisplay_safe_key));
		this.prefRatingQuestionable = (CheckBoxPreference)f.findPreference(this.getString(R.string.pref_ratings_todisplay_questionable_key));
		this.prefRatingExplicit = (CheckBoxPreference)f.findPreference(this.getString(R.string.pref_ratings_todisplay_explicit_key));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			this.addPreferencesFromResource(R.xml.generalpreferences);
			this.prefScreen = this.getPreferenceScreen();
			
			this.prefSites = (PreferenceCategory)this.findPreference(this.getString(R.string.pref_sites_key));
			this.prefCacheSize = (ListPreference)this.findPreference(this.getString(R.string.pref_general_cachesize_key));
			this.prefClearCache = (Preference)this.findPreference(this.getString(R.string.pref_general_clearcache_key));
			this.prefRatingSafe = (CheckBoxPreference)this.findPreference(this.getString(R.string.pref_ratings_todisplay_safe_key));
			this.prefRatingQuestionable = (CheckBoxPreference)this.findPreference(this.getString(R.string.pref_ratings_todisplay_questionable_key));
			this.prefRatingExplicit = (CheckBoxPreference)this.findPreference(this.getString(R.string.pref_ratings_todisplay_explicit_key));
		}
		else
		{
			PreferencesFragment pfrag = new PreferencesFragment();
			this.getFragmentManager().beginTransaction().replace(android.R.id.content, pfrag).commit();
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		// stop listening for changed prefs
		this.prefScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		
		// remove listeners (it's safer)
		this.prefClearCache.setOnPreferenceClickListener(null);
		
		//this.getFragmentManager().beginTransaction().remove(null).commit();
	}
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onResume()
	{
		super.onResume();
		// listen for changed prefs
		this.prefScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		this.prefSites.removeAll();
		
		SitesDB sitesdb = new SitesDB(this);
		List<Site> sites = sitesdb.getAll();
		if(sites.size() != 0)
		{
			for(Site site : sites)
			{
				Preference sitePref = new Preference(this);
				sitePref.setTitle(site.getName());
				sitePref.setSummary(site.getUrl());
				sitePref.setEnabled(false);
				// TODO: launch site prefs here
				//sitePref.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(site.getUrl())));
				this.prefSites.addPreference(sitePref);
			}
		}
		else
		{
			this.prefScreen.removePreference(this.prefSites);
		}
		
		// put listeners
		this.prefClearCache.setOnPreferenceClickListener(this);
		
		DirectorySizeTask sizeTask = new DirectorySizeTask(DiskUtils.getCacheDirectory(this));
		sizeTask.setDirectorySizeCallback(new DirectorySizeCallback()
		{
			@Override
			public void directorySize(long size)
			{
				if(size > 0)
				{
					String calculatedBytes = Formatters.humanReadableByteCount(size);
					prefClearCache.setSummary(getString(R.string.pref_general_clearcache_desc, calculatedBytes));
				}
				else
				{
					prefClearCache.setSummary(getString(R.string.pref_general_clearcache_desc_empty));
				}
				
				prefClearCache.setEnabled(size > 0);
			}
		});
		ConcurrentTask.execute(sizeTask);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.getSupportMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				return true;
			case R.id.googleplay:
				Intent market = new Intent(Intent.ACTION_VIEW);
				String marketLink = String.format("market://details?id=%s", this.getPackageName());
				market.setData(Uri.parse(marketLink));
				this.startActivity(market);
				return true;
			case R.id.news:
				BlogNewsDialog newsDialog = new BlogNewsDialog(this);
				newsDialog.createDialog(false).show();
				return true;
			case R.id.privacy:
				Intent privacy = new Intent(Intent.ACTION_VIEW);
				privacy.setData(Uri.parse("http://www.shujito.org/cartonbox/privacy"));
				this.startActivity(privacy);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		if(this.getString(R.string.pref_ratings_todisplay_safe_key).equals(key))
		{
			if(!this.prefRatingQuestionable.isChecked() && !this.prefRatingExplicit.isChecked())
				this.prefRatingSafe.setChecked(true);
		}
		if(this.getString(R.string.pref_ratings_todisplay_questionable_key).equals(key))
		{
			if(!this.prefRatingSafe.isChecked() && !this.prefRatingExplicit.isChecked())
				this.prefRatingQuestionable.setChecked(true);
		}
		if(this.getString(R.string.pref_ratings_todisplay_explicit_key).equals(key))
		{
			if(!this.prefRatingSafe.isChecked() && !this.prefRatingQuestionable.isChecked())
				this.prefRatingExplicit.setChecked(true);
		}
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		if(this.prefClearCache.equals(preference))
		{
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setCancelable(false);
			pd.show();
			
			ClearDirectoryTask clearDirectory = new ClearDirectoryTask(DiskUtils.getCacheDirectory(this));
			clearDirectory.setOnDiskTaskProgress(new OnDiskTaskProgressListener()
			{
				@Override
				public void onDiskTaskProgress(int total, int complete)
				{
					pd.setMax(total);
					pd.setProgress(complete);
				}
			});
			clearDirectory.setOnDirectoryClearedListener(new OnDirectoryClearedListener()
			{
				@Override
				public void onDirectoryCleared()
				{
					// conceal dialog
					pd.dismiss();
					// make it say that it is empty
					prefClearCache.setSummary(getString(R.string.pref_general_clearcache_desc_empty));
					// disable the preference
					prefClearCache.setEnabled(false);
				}
			});
			//clearDirectory.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			ConcurrentTask.execute(clearDirectory);
		}
		
		return false;
	}
}
