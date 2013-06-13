package org.shujito.cartonbox.view.widget;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

// backported from android source
public class UpdateableListPreference extends ListPreference
{
	String mSummary = null;
	
	public UpdateableListPreference(Context context)
	{
		super(context);
	}
	
	public UpdateableListPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		// backup the value
		String value = this.getValue();
		this.setValue(null);
		// get the unformatted summary
		CharSequence rawSummary = this.getSummary();
		this.setValue(value);
		
		if(rawSummary != null)
			this.mSummary = rawSummary.toString();
	}
	
	@Override
	public void setValue(String value)
	{
		super.setValue(value);
		// update the preference
		this.notifyChanged();
	}
	
	@Override
	public CharSequence getSummary()
	{
		CharSequence entry = this.getEntry();
		
		if(this.mSummary == null || entry == null)
			return super.getSummary();
		else
			return String.format(this.mSummary, entry);
	}
	
	@Override
	public void setSummary(CharSequence summary)
	{
		super.setSummary(summary);
		if(summary == null & this.mSummary == null)
			this.mSummary = null;
		else if(summary != null && !summary.equals(this.mSummary))
			this.mSummary = summary.toString();
	}
}
