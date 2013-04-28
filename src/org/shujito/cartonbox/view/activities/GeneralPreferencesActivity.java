package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.view.fragments.PreferencesFragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class GeneralPreferencesActivity extends SherlockPreferenceActivity
{
	@Override
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			this.addPreferencesFromResource(R.xml.generalpreferences);
		}
		else
		{
			this.getFragmentManager().beginTransaction().add(android.R.id.content, new PreferencesFragment()).commit();
		}
	}
}
