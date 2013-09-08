package org.shujito.cartonbox.view.adapters;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnXmlResponseReceivedListener;
import org.shujito.cartonbox.model.BlogEntry;
import org.shujito.cartonbox.model.parser.XmlParser;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlogListAdapter extends BaseAdapter implements OnXmlResponseReceivedListener, OnErrorListener
{
	Context context = null;
	List<BlogEntry> entries = null;
	
	public BlogListAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount()
	{
		if(this.entries != null)
			return entries.size();
		return 0;
	}
	
	@Override
	public Object getItem(int pos)
	{
		if(this.entries != null)
		{
			return this.entries.get(pos);
		}
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
			v = inf.inflate(R.layout.blog_item, dad, false);
		}
		
		BlogEntry entry = this.entries.get(pos);
		if(entry != null)
		{
			String blogContent = entry.getContent();
			Spanned spannedContent = Html.fromHtml(blogContent);
			//String dur = spannedContent.toString().trim();
			
			((TextView)v.findViewById(R.id.tvTitle)).setText(entry.getTitle());
			((TextView)v.findViewById(R.id.tvContent)).setText(spannedContent);
			((TextView)v.findViewById(R.id.tvDate)).setText(entry.getDate());
			//((TextView)v.findViewById(R.id.blogitem_tvdate)).setText(Locale.getDefault().getDisplayLanguage());
		}
		else
		{
			//String datefmt = DateFormat.getDateTimeInstance().format(new Date());
			((TextView)v.findViewById(R.id.tvTitle)).setText(R.string.couldnotblog);
			((TextView)v.findViewById(R.id.tvContent)).setText(R.string.trylater);
			//((TextView)v.findViewById(R.id.blogitem_tvdate)).setText(datefmt);
			((TextView)v.findViewById(R.id.tvDate)).setText(null);
		}
		
		return v;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onResponseReceived(XmlParser<?> xp)
	{
		// one line
		this.entries = (List<BlogEntry>)xp.get();
		
		/* even more code
		this.entries = new ArrayList<BlogEntry>();
		BlogEntry b = null;
		int index = 0;
		List<?> everything = xp.get();
		// (b = (BlogEntry)xp.get().get(++index)) != null
		while(everything.size() > index && (b = (BlogEntry)everything.get(index++)) != null)
		{
			if(b.getCategories() != null)
			{
				for(String cat : b.getCategories())
				{
					if(cat.contains("cartonbox"))
					{
						this.entries.add(b);
						break;
					}
				}
			}
		}
		//*/
		
		this.notifyDataSetChanged();
	}

	@Override
	public void onError(int code, String result)
	{
		this.entries = new ArrayList<BlogEntry>();
		// add some dummy
		this.entries.add(null);
		this.notifyDataSetChanged();
	}
}
