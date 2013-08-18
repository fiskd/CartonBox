package org.shujito.cartonbox.view.adapters;

import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SitesAdapter extends BaseAdapter
{
	Context context = null;
	List<Site> mLsSites = null;
	
	//public SitesAdapter(Context context, List<Site> sites)
	public SitesAdapter(Context context)
	{
		this.context = context;
		this.refreshSites();
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup dad)
	{
		if(v == null)
		{
			//LayoutInflater inf = LayoutInflater.from(this.context);
			//v = inf.inflate(0, dad, false);
			//v = new ImageView(this.context);
			v = new TextView(this.context);
		}
		
		Site one = this.mLsSites.get(pos);
		if(one == null)
			return v; // get out quick
		
		((TextView)v).setTextAppearance(this.context, android.R.style.TextAppearance_Medium);
		((TextView)v).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_unknown, 0, 0);
		//((TextView)v).setCompoundDrawables(null, null, null, null);
		((TextView)v).setText(one.getName());
		//((TextView)v).setText(this.sites[pos]);
		((TextView)v).setGravity(Gravity.CENTER);
		
		return v;
	}
	
	@Override
	public int getCount()
	{
		if(this.mLsSites != null)
			return this.mLsSites.size();
		return 0;
	}
	
	@Override
	public Object getItem(int pos)
	{
		if(this.mLsSites != null)
			return this.mLsSites.get(pos);
		return null;
	}
	
	@Override
	public long getItemId(int pos)
	{
		if(this.mLsSites != null)
		{
			Site s = this.mLsSites.get(pos);
			if(s != null)
				return s.getId();
		}
		
		return -1;
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		this.refreshSites();
		super.notifyDataSetChanged();
	}
	
	private void refreshSites()
	{
		SitesDB sites = new SitesDB(this.context);
		this.mLsSites = sites.getAll();
	}
}
