package org.shujito.cartonbox.view.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageLoader;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.model.Download;
import org.shujito.cartonbox.model.db.DownloadsDB;
import org.shujito.cartonbox.utils.BitmapCache;
import org.shujito.cartonbox.utils.ConcurrentTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadsAdapter extends BaseAdapter
{
	Context context = null;
	List<Download> mLsDownloads = null;
	BitmapCache cache = null;
	
	public DownloadsAdapter(Context context)
	{
		this.context = context;
		this.mLsDownloads = new ArrayList<Download>();
		this.cache = new BitmapCache();
	}
	
	@Override
	public int getCount()
	{
		if(this.mLsDownloads != null)
			return this.mLsDownloads.size();
		return 0;
	}
	
	@Override
	public Object getItem(int pos)
	{
		if(this.mLsDownloads != null)
			return this.mLsDownloads.get(pos);
		return null;
	}
	
	@Override
	public long getItemId(int pos)
	{
		if(this.mLsDownloads != null)
			return this.mLsDownloads.get(pos).getId();
		return 0;
	}
	
	@Override
	public View getView(int pos, View v, ViewGroup dad)
	{
		long now = System.currentTimeMillis();
		
		if(v == null)
		{
			LayoutInflater inf = LayoutInflater.from(this.context);
			v = inf.inflate(R.layout.download_item, dad, false);
		}
		
		final Download one = this.mLsDownloads.get(pos);
		// the id can be used as time
		long ago = now - one.getId();
		int hours = (int)(ago / (60 * 60 * 1000));
		int minutes = (int)(ago / (60 * 1000)) - (hours * 60);
		//int seconds = (int)(ago / (1000)) - (hours * 60 * 60) - (minutes * 60);
		
		String timeAgo = "";
		
		if(hours >= 24)
		{
			timeAgo = new Date(ago).toString();
		}
		else
		{
			if(hours > 0 || minutes > 0)
			{
				StringBuilder sb = new StringBuilder();
				if(hours > 0)
				{
					// TODO: resources
					sb.append(String.format("%s hours", hours));
				}
				if(minutes > 0)
				{
					if(hours > 0)
					{
						sb.append(", ");
					}
					// TODO: resources
					sb.append(String.format("%s minutes", minutes));
				}
				sb.append(" ago");
				timeAgo = sb.toString();
			}
			else
			{
				// TODO: resources
				timeAgo = "Just now.";
			}
		}
		
		((TextView)v.findViewById(R.id.tvName)).setText(one.getName());
		((TextView)v.findViewById(R.id.tvWhen)).setText(timeAgo.toString());
		
		final ImageView iv = ((ImageView)v.findViewById(R.id.ivPic));
		
		Bitmap cached = this.cache.getBitmapFromMemCache(one.getId());
		if(cached != null)
		{
			iv.setBackgroundColor(Color.BLACK);
			iv.setImageBitmap(cached);
		}
		else
		{
			ImageLoader load = new ImageLoader(one.getLocation());
			if(iv.getTag() instanceof ImageLoader)
			{
				ImageLoader attached = (ImageLoader)iv.getTag();
				attached.setOnImageFetchedListener(null);
			}
			
			iv.setTag(load);
			iv.setImageBitmap(null);
			iv.setBackgroundColor(Color.GRAY);
			
			load.setWidth(128);
			load.setHeight(128);
			load.setOnImageFetchedListener(new OnImageFetchedListener()
			{
				@Override
				public void onImageFetched(Bitmap b)
				{
					cache.addBitmapToMemCache(one.getId(), b);
					iv.setBackgroundColor(Color.BLACK);
					iv.setImageBitmap(b);
				}
			});
			ConcurrentTask.execute(load);
		}
		
		return v;
	}
	
	public void refreshDownloads()
	{
		Cleanup c = new Cleanup(this);
		ConcurrentTask.execute(c);
	}
	
	// XXX: I dislike accesing parent's things directly (e.g. no 'this')
	class Cleanup extends AsyncTask<Void, Void, List<Download>>
	{
		DownloadsAdapter adapter = null;
		List<Download> downloads = null;
		
		public Cleanup(DownloadsAdapter adapter)
		{
			this.adapter = adapter;
		}
		
		@Override
		protected List<Download> doInBackground(Void... params)
		{
			DownloadsDB db = new DownloadsDB(context);
			List<Download> all = db.getAll();
			for(Download download : all)
			{
				// locate the file
				File file = new File(download.getLocation());
				if(!file.exists())
				{
					// nothing? delete that
					db.delete(download);
				}
			}
			// get again
			return db.getAll();
		}
		
		@Override
		protected void onPostExecute(List<Download> result)
		{
			// ew
			this.adapter.mLsDownloads = result;
			this.adapter.notifyDataSetChanged();
			if(this.adapter.context instanceof Activity && result.size() == 0)
			{
				((Activity)this.adapter.context).finish();
			}
		}
	}
}