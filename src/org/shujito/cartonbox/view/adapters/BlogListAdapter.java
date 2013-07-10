package org.shujito.cartonbox.view.adapters;

import java.util.List;

import org.shujito.cartonbox.R;
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

public class BlogListAdapter extends BaseAdapter implements OnXmlResponseReceivedListener
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
		String blogContent = entry.getContent();
		Spanned spannedContent = Html.fromHtml(blogContent);
		//String dur = spannedContent.toString().trim();
		
		((TextView)v.findViewById(R.id.blogitem_tvtitle)).setText(entry.getTitle());
		((TextView)v.findViewById(R.id.blogitem_tvcontent)).setText(spannedContent);
		((TextView)v.findViewById(R.id.blogitem_tvdate)).setText(entry.getDate());
		
		return v;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onResponseReceived(XmlParser<?> xp)
	{
		// one line
		this.entries = (List<BlogEntry>)xp.get();
		this.notifyDataSetChanged();
		
		/* more code
		this.entries.clear();
		BlogEntry b = null;
		int index = 0;
		while((b = (BlogEntry)xp.get().get(index)) != null)
		{
			this.entries.add(b);
		}
		//*/
	}
}
