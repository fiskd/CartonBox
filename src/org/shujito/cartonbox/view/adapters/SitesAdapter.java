package org.shujito.cartonbox.view.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.db.SitesDB;

import android.content.Context;
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
	Map<Long, Drawable> drawables = null;
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
		
		this.drawables = new HashMap<Long, Drawable>();
		
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
		
		Drawable dw = this.drawables.get(one.getId());
		if(dw == null)
		{
			dw = Drawable.createFromPath(one.getIcon());
			if(dw == null)
			{
				dw = this.context.getResources().getDrawable(R.drawable.icon_unknown);
				/*
				Bitmap b = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(b);
				
				String first = one.getName();
				int color = 0xff000000;
				if(first.length() > 0)
					color |= first.charAt(0) << 16;
				if(first.length() > 1)
					color |= first.charAt(1) << 8;
				if(first.length() > 2)
					color |= first.charAt(2);
				
				Paint paint = new Paint();
				paint.setColor(color);
				paint.setStyle(Paint.Style.FILL);
				c.drawPaint(paint);
				
				paint.setColor(0xff000000);
				
				c.scale(8, 8);
				//c.translate(128, 128);
				
				c.drawText(first.substring(0, 2), 0, 0, paint);
				
				dw = new BitmapDrawable(this.context.getResources(), b);
				//*/
			}
			dw.setBounds(0, 0, this.width / this.numCols, this.width / this.numCols);
			this.drawables.put(one.getId(), dw);
		}
		
		((TextView)v).setTextAppearance(this.context, android.R.style.TextAppearance_Medium);
		//((TextView)v).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_unknown, 0, 0);
		((TextView)v).setCompoundDrawables(null, dw, null, null);
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
		this.drawables.clear();
	}
}
