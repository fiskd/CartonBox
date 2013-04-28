package org.shujito.cartonbox.view.fragments;

import org.shujito.cartonbox.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class PreferencesFragment extends PreferenceFragment
{
	static String PREF_HEADER = "org.shujito.cartonbox.PREF_HEADER";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.generalpreferences);
		
		Preference pref = new Preference(this.getActivity());
		pref.setTitle("nothing");
		//pref.setSummary("http://site.com/");
		pref.setEnabled(false);
		
		PreferenceCategory sitesCat = (PreferenceCategory)this.findPreference(this.getString(R.string.pref_sites));
		sitesCat.addPreference(pref);
		//sitesCat.setEnabled(false);
		
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
	}
}
