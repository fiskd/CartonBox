package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.fragments.PreferencesFragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class GeneralPreferencesActivity extends SherlockPreferenceActivity
{
	Preference prefSites = null;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void initializeFromFragment(PreferenceFragment f)
	{
		// TODO: the rest...
		this.prefSites = f.findPreference(this.getString(R.string.pref_sites_key));
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
			// TODO: the rest...
			this.prefSites = this.findPreference(this.getString(R.string.pref_sites_key));
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
	}
}
