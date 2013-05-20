package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.Formatters;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.utils.io.ClearDirectoryTask;
import org.shujito.cartonbox.utils.io.DirectorySizeTask;
import org.shujito.cartonbox.utils.io.DiskCacheManager;
import org.shujito.cartonbox.utils.io.listeners.DirectorySizeCallback;
import org.shujito.cartonbox.utils.io.listeners.OnDirectoryClearedListener;
import org.shujito.cartonbox.utils.io.listeners.OnDiskTaskProgressListener;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PreferencesFragment extends PreferenceFragment implements
	OnSharedPreferenceChangeListener,
	OnPreferenceClickListener
{
	static String PREF_HEADER = "org.shujito.cartonbox.PREF_HEADER";
	
	//EditTextPreference etpCacheSize = null;
	Preference prefClearCache = null;
	CheckBoxPreference cbpRatingSafe = null;
	CheckBoxPreference cbpRatingQuestionable = null;
	CheckBoxPreference cbpRatingExplicit = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		/*
		String s = this.getArguments().getString(PREF_HEADER);
		if(s.equals(this.getString(R.string.pref_general)))
			this.addPreferencesFromResource(R.xml.preferences_general);
		if(s.equals(this.getString(R.string.pref_content)))
			this.addPreferencesFromResource(R.xml.preferences_content);
		if(s.equals(this.getString(R.string.pref_blacklist)))
			this.addPreferencesFromResource(R.xml.preferences_blacklist);
		if(s.equals(this.getString(R.string.pref_ratings)))
			this.addPreferencesFromResource(R.xml.preferences_ratings);
		//*/
		
		this.addPreferencesFromResource(R.xml.generalpreferences);
		
		Preference pref = new Preference(this.getActivity());
		pref.setTitle("nothing");
		//pref.setSummary("http://site.com/");
		pref.setEnabled(false);
		
		PreferenceCategory sitesCat = (PreferenceCategory)this.findPreference(this.getString(R.string.pref_sites));
		sitesCat.addPreference(pref);
		//sitesCat.setEnabled(false);
		
		this.cbpRatingSafe = (CheckBoxPreference)this.findPreference(this.getString(R.string.pref_ratings_todisplay_safe_key));
		this.cbpRatingQuestionable = (CheckBoxPreference)this.findPreference(this.getString(R.string.pref_ratings_todisplay_questionable_key));
		this.cbpRatingExplicit = (CheckBoxPreference)this.findPreference(this.getString(R.string.pref_ratings_todisplay_explicit_key));
		
		this.displayCacheSizeSummary();
		this.displayClearCacheSummary();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		//Logger.i("PreferencesFragment::onSharedPreferenceChanged", key);
		Preference pref = this.findPreference(key);
		if(pref != null)
		{
			if(this.getString(R.string.pref_general_cachesize_key).equals(key))
			{
				// display value here
				this.displayCacheSizeSummary();
			}
			if(this.getString(R.string.pref_ratings_todisplay_safe_key).equals(key))
			{
				if(!this.cbpRatingQuestionable.isChecked() && !this.cbpRatingExplicit.isChecked())
					this.cbpRatingSafe.setChecked(true);
			}
			if(this.getString(R.string.pref_ratings_todisplay_questionable_key).equals(key))
			{
				if(!this.cbpRatingSafe.isChecked() && !this.cbpRatingExplicit.isChecked())
					this.cbpRatingQuestionable.setChecked(true);
			}
			if(this.getString(R.string.pref_ratings_todisplay_explicit_key).equals(key))
			{
				if(!this.cbpRatingSafe.isChecked() && !this.cbpRatingQuestionable.isChecked())
					this.cbpRatingExplicit.setChecked(true);
			}
		}
	}
	
	void displayCacheSizeSummary()
	{
		EditTextPreference etpCacheSize = (EditTextPreference)this.findPreference(this.getString(R.string.pref_general_cachesize_key));
		etpCacheSize.setSummary(this.getString(R.string.pref_general_cachesize_desc, etpCacheSize.getText()));
	}
	
	void displayClearCacheSummary()
	{
		if(this.prefClearCache == null)
			this.prefClearCache = (Preference)this.findPreference(this.getString(R.string.pref_general_clearcache_key));
		
		this.prefClearCache.setSummary(this.getString(R.string.pref_general_clearcache_calc));
		this.prefClearCache.setOnPreferenceClickListener(this);
		
		DirectorySizeTask getDirSize = new DirectorySizeTask(DiskCacheManager.getCacheDirectory(this.getActivity()));
		getDirSize.setDirectorySizeCallback(new DirectorySizeCallback()
		{
			@Override
			public void directorySize(long sizeInBytes)
			{
				String calculatedSize = Formatters.humanReadableByteCount(sizeInBytes, true);
				if(sizeInBytes > 0)
					prefClearCache.setSummary(getString(R.string.pref_general_clearcache_desc, calculatedSize));
				else
					prefClearCache.setSummary(getString(R.string.pref_general_clearcache_desc_empty));
				prefClearCache.setEnabled(sizeInBytes > 0);
			}
		});
		
		getDirSize.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		Logger.i("PreferencesFragment::onPreferenceClick", preference.getTitle().toString());
		
		if(preference.equals(this.prefClearCache))
		{
			final ProgressDialog pd = new ProgressDialog(this.getActivity());
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setCancelable(false);
			pd.show();
			
			ClearDirectoryTask clearDirectory = new ClearDirectoryTask(DiskCacheManager.getCacheDirectory(this.getActivity()));
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
			clearDirectory.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		
		return true;
	}
}
