package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.fragments.PreferencesFragment;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class GeneralPreferencesActivity extends SherlockPreferenceActivity
	implements OnSharedPreferenceChangeListener
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
			// oh, this fixes the orientation crash
			pfrag.setRetainInstance(true);
			this.getFragmentManager().beginTransaction().replace(android.R.id.content, pfrag).commit();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Preference dummy = new Preference(this);
		dummy.setTitle("nothing");
		dummy.setEnabled(false);
		
		this.prefSites.addPreference(dummy);
		
		this.prefScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		this.prefScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
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
}
