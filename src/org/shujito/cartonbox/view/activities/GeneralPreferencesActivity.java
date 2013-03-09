package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.R;

import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class GeneralPreferencesActivity extends SherlockPreferenceActivity
{
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle cirno)
	{
		super.onCreate(cirno);
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			this.addPreferencesFromResource(R.xml.generalpreferences);
		}
	}
}
