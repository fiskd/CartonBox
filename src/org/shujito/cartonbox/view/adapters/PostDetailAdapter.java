package org.shujito.cartonbox.view.adapters;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.utils.Formatters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostDetailAdapter extends BaseAdapter
	implements OnItemClickListener
{
	public class Detail
	{
		private String header;
		private String content;
		private Tag tag;
		
		public String getHeader()
		{
			return header;
		}
		public String getContent()
		{
			return content;
		}
		public Tag getTag()
		{
			return tag;
		}
		
		public Detail setHeader(String header)
		{
			this.header = header;
			return this;
		}
		public Detail setContent(String content)
		{
			this.content = content;
			return this;
		}
		public Detail setTag(Tag tag)
		{
			this.tag = tag;
			return this;
		}
	}
	
	Context context = null;
	List<Detail> details = null;
	
	public PostDetailAdapter(Context contex, Post post)
	{
		this.details = new ArrayList<Detail>();
		this.context = contex;
		
		int size = post.getFileSize();
		int width = post.getWidth();
		int height = post.getHeight();
		
		String humanFileSize = Formatters.humanReadableByteCount(size);
		
		String sizesFormat = String.format("%s (%sx%s)", humanFileSize, width, height);
		
		this.details.add(new Detail().setHeader("Rating"));
		this.details.add(new Detail().setContent(post.getRating().toString()));
		
		this.details.add(new Detail().setHeader("Size"));
		this.details.add(new Detail().setContent(sizesFormat));
		
		this.details.add(new Detail().setHeader("Tags"));
		for(Tag t : post.getTags())
		{
			this.details.add(new Detail().setTag(t));
		}
	}
	
	@Override
	public int getCount()
	{
		if(this.details != null)
			return this.details.size();
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
			v = inf.inflate(R.layout.detail_item, dad, false);
		}
		
		Detail one = this.details.get(pos);
		
		LinearLayout llTvHeader = ((LinearLayout)v.findViewById(R.id.llTvHeader));
		TextView tvHeader = ((TextView)v.findViewById(R.id.tvHeader));
		TextView tvDetail = ((TextView)v.findViewById(R.id.tvDetail));
		CheckedTextView ctvTag = ((CheckedTextView)v.findViewById(R.id.ctvTag));
		
		if(one.getHeader() == null)
		{
			llTvHeader.setVisibility(View.GONE);
		}
		else
		{
			llTvHeader.setVisibility(View.VISIBLE);
			tvHeader.setText(one.getHeader());
		}
		
		if(one.getContent() == null)
		{
			tvDetail.setVisibility(View.GONE);
		}
		else
		{
			tvDetail.setVisibility(View.VISIBLE);
			tvDetail.setText(one.getContent());
		}
		
		if(one.getTag() == null)
		{
			ctvTag.setVisibility(View.GONE);
		}
		else
		{
			int color = 0;
			
			switch(one.getTag().getCategory())
			{
				case General:
					color = 0xFF50C0E9;
					break;
				case Artist:
					color = 0xFFFF5F5F;
					break;
				case Copyright:
					color = 0xFFC182E0;
					break;
				case Character:
					color = 0xFF99CC00;
					break;
				case Other1: // circle/model
					color = 0xFFFFD060;
					break;
				case Other2: // faults/photoset
					color = 0xFFCCCCCC;
					break;
				default:
					//color = 0xFFCCCCCC;
					break;
			}
			
			ctvTag.setVisibility(View.VISIBLE);
			ctvTag.setText(one.getTag().getName());
			ctvTag.setTextColor(color);
		}
		
		return v;
	}
	
	@Override
	public boolean isEnabled(int position)
	{
		if(this.details != null && this.details.get(position) != null)
			return this.details.get(position).getHeader() == null;
		return true;
	}
	
	@Override
	public void onItemClick(AdapterView<?> ada, View v, int pos, long id)
	{
	}
}
