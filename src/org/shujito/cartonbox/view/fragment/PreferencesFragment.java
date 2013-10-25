package org.shujito.cartonbox.view.fragment;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.activity.GeneralPreferencesActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PreferencesFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		this.addPreferencesFromResource(R.xml.generalpreferences);
		// initialize on the activity
		((GeneralPreferencesActivity)this.getActivity()).initializeFromFragment(this);
	}
}
