package org.shujito.cartonbox.view.adapter;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.listener.OnErrorListener;
import org.shujito.cartonbox.controller.task.ImageDownloader;
import org.shujito.cartonbox.controller.task.listener.OnImageFetchedListener;
import org.shujito.cartonbox.controller.task.listener.OnJsonResponseReceivedListener;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.JsonParser;
import org.shujito.cartonbox.util.BitmapCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DefaultSitesAdapter extends BaseAdapter implements OnJsonResponseReceivedListener, OnErrorListener
{
	private Context context = null;
	private List<Site> entries = null;
	private BitmapCache cache = null;
	
	public DefaultSitesAdapter(Context context)
	{
		this.context = context;
		this.entries = new ArrayList<Site>();
		this.cache = new BitmapCache();
	}
	
	@Override
	public int getCount()
	{
		if(this.entries != null)
			return this.entries.size();
		return 0;
	}
	
	@Override
	public Object getItem(int pos)
	{
		if(this.entries != null)
			return this.entries.get(pos);
		return null;
	}
	
	@Override
	public long getItemId(int pos)
	{
		return 0;
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup dad)
	{
		if(v == null)
		{
			LayoutInflater inf = LayoutInflater.from(this.context);
			v = inf.inflate(R.layout.item_site, dad, false);
		}
		
		final Site site = this.entries.get(pos);
		
		final ImageView ivIcon = (ImageView)v.findViewById(R.id.ivIcon);
		final TextView tvName = (TextView)v.findViewById(R.id.tvName);
		final TextView tvDescription = (TextView)v.findViewById(R.id.tvDescription);
		
		tvName.setText(site.getName());
		tvDescription.setText(site.getDescription());
		
		Bitmap cached = this.cache.getBitmapFromMemCache(site.getId());
		if(cached != null)
		{
			ivIcon.setImageBitmap(cached);
		}
		else
		{
			ImageDownloader downloader = new ImageDownloader(this.context, site.getIconWeb());
			if(ivIcon.getTag() instanceof ImageDownloader)
			{
				ImageDownloader attached = (ImageDownloader)ivIcon.getTag();
				attached.setOnImageFetchedListener(null);
			}
			
			ivIcon.setTag(downloader);
			ivIcon.setImageResource(R.drawable.icon_unknown);
			
			downloader.setWidth(ivIcon.getWidth());
			downloader.setHeight(ivIcon.getHeight());
			downloader.setOnImageFetchedListener(new OnImageFetchedListener()
			{
				@Override
				public void onImageFetched(Bitmap b)
				{
					if(b != null)
					{
						try
						{
							cache.addBitmapToMemCache(site.getId(), b);
							ivIcon.setImageBitmap(b);
						}
						catch(Exception ex)
						{
							Logger.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
						}
					}
				}
			});
			//ConcurrentTask.execute(downloader);
			//downloader.execute();
		}
		
		return v;
	}
	
	@Override
	public void onError(int code, String result)
	{
	}
	
	@Override
	public void onResponseReceived(JsonParser<?> jp)
	{
		this.entries.clear();
		Site site = null;
		int index = 0;
		while((site = (Site)jp.getAtIndex(index)) != null)
		{
			//*
			try
			{ Thread.sleep(10); }
			catch(Exception ex) { }
			//*/
			index++;
			this.entries.add(site);
		}
		this.notifyDataSetChanged();
	}
}
