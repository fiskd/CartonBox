package org.shujito.cartonbox.view.adapter;

import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.task.ImageDownloader;
import org.shujito.cartonbox.controller.task.listener.OnImageFetchedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;
import org.shujito.cartonbox.util.BitmapCache;
import org.shujito.cartonbox.util.ConcurrentTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SitesAdapter extends BaseAdapter
{
	Context context = null;
	List<Site> mLsSites = null;
	private BitmapCache cache = null;
	//Map<Long, Drawable> drawables = null;
	//Map<Long, SoftReference<Drawable>> softDrawables = null;
	int width;
	int numCols;
	
	//public SitesAdapter(Context context, List<Site> sites)
	public SitesAdapter(Context context)
	{
		this.context = context;
		
		WindowManager winman = (WindowManager)this.context.getSystemService(Context.WINDOW_SERVICE);
		
		DisplayMetrics metrics = new DisplayMetrics();
		winman.getDefaultDisplay().getMetrics(metrics);
		this.width = metrics.widthPixels;
		
		this.numCols = this.context.getResources().getInteger(R.integer.main_gvsites_numcols);
		
		this.cache = new BitmapCache();
		//this.drawables = new HashMap<Long, Drawable>();
		//this.softDrawables = new HashMap<Long, SoftReference<Drawable>>();
		
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
		
		final Site one = this.mLsSites.get(pos);
		if(one == null)
			return v; // get out quick
		
		final TextView tv = (TextView)v;
		
		tv.setTextAppearance(this.context, android.R.style.TextAppearance_Medium);
		//tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_unknown, 0, 0);
		tv.setText(one.getName());
		//tv.setText(this.sites[pos]);
		tv.setGravity(Gravity.CENTER);
		
		Bitmap cached = this.cache.getBitmapFromMemCache(one.getId());
		if(cached != null)
		{
			Drawable dw = new BitmapDrawable(this.context.getResources(), cached);
			dw.setBounds(0, 0, 256, 256);
			tv.setCompoundDrawables(null, dw, null, null);
		}
		else
		{
			Drawable dw = this.context.getResources().getDrawable(R.drawable.icon_unknown);
			dw.setBounds(0, 0, 256, 256);
			tv.setCompoundDrawables(null, dw, null, null);
			
			if(one.getIconFile() != null && one.getIconFile().startsWith("/"))
			{
				// load from file
			}
			else
			{
				// load from web
				ImageDownloader downloader = new ImageDownloader(this.context, one.getIconFile());
				
				if(tv.getTag() instanceof ImageDownloader)
				{
					ImageDownloader attached = (ImageDownloader)tv.getTag();
					attached.setOnImageFetchedListener(null);
				}
				
				tv.setTag(downloader);
				
				downloader.setWidth(256);
				downloader.setHeight(256);
				downloader.setOnImageFetchedListener(new OnImageFetchedListener()
				{
					@Override
					public void onImageFetched(Bitmap b)
					{
						if(b != null)
						{
							try
							{
								cache.addBitmapToMemCache(one.getId(), b);
								Drawable dw = new BitmapDrawable(context.getResources(), b);
								dw.setBounds(0, 0, 256, 256);
								tv.setCompoundDrawables(null, dw, null, null);
							}
							catch(Exception ex)
							{
								Logger.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
							}
						}
					}
				});
				ConcurrentTask.execute(downloader);
			}
		}
		
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
		//this.drawables.clear();
		this.cache.clear();
	}
}
