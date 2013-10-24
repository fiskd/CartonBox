package org.shujito.cartonbox.view.adapters;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.ImageDownloader;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnImageFetchedListener;
import org.shujito.cartonbox.controller.listeners.OnXmlResponseReceivedListener;
import org.shujito.cartonbox.model.BlogEntry;
import org.shujito.cartonbox.model.parser.XmlParser;
import org.shujito.cartonbox.utils.ConcurrentTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
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
			v = inf.inflate(R.layout.item_blog, dad, false);
		}
		
		BlogEntry entry = this.entries.get(pos);
		final TextView title = ((TextView)v.findViewById(R.id.tvTitle));
		final TextView content = ((TextView)v.findViewById(R.id.tvContent));
		final TextView date = ((TextView)v.findViewById(R.id.tvDate));
		
		if(entry != null)
		{
			String blogContent = entry.getContent();
			final SpannableString spannedContent = new SpannableString(Html.fromHtml(blogContent));
			//SpannableString spstr = new SpannableString(blogContent);
			//String dur = spannedContent.toString().trim();
			
			ImageSpan[] imageSpans = spannedContent.getSpans(0, spannedContent.length() - 1, ImageSpan.class);
			
			title.setText(entry.getTitle());
			content.setText(spannedContent);
			date.setText(entry.getDate());
			
			for(ImageSpan imgSpan : imageSpans)
			{
				String source = imgSpan.getSource();
				final int spanStart = spannedContent.getSpanStart(imgSpan);
				final int spanEnd = spannedContent.getSpanEnd(imgSpan);
				spannedContent.removeSpan(imgSpan);
				
				ImageDownloader downloader = new ImageDownloader(this.context, source);
				downloader.setWidth(256);
				downloader.setHeight(256);
				downloader.setOnImageFetchedListener(new OnImageFetchedListener()
				{
					@Override
					public void onImageFetched(Bitmap b)
					{
						if(b != null)
						{
							ImageSpan newSpan = new ImageSpan(context, b);
							spannedContent.setSpan(newSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							content.setText(spannedContent);
						}
					}
				});
				ConcurrentTask.execute(downloader);
			}
			
			//((TextView)v.findViewById(R.id.tvTitle)).setText(entry.getTitle());
			//((TextView)v.findViewById(R.id.tvContent)).setText(spannedContent);
			//((TextView)v.findViewById(R.id.tvDate)).setText(entry.getDate());
			//((TextView)v.findViewById(R.id.blogitem_tvdate)).setText(Locale.getDefault().getDisplayLanguage());
		}
		else
		{
			//String datefmt = DateFormat.getDateTimeInstance().format(new Date());
			title.setText(R.string.couldnotblog);
			content.setText(R.string.trylater);
			//((TextView)v.findViewById(R.id.blogitem_tvdate)).setText(datefmt);
			date.setText(null);
		}
		
		return v;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onResponseReceived(XmlParser<?> xp)
	{
		// can be done in one line, it gives a warning
		this.entries = (List<BlogEntry>)xp.get();
		
		/* can be done with the following code, no warnings 
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
