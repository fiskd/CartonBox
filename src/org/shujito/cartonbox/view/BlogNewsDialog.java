package org.shujito.cartonbox.view;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.BlogRssXmlDownloader;
import org.shujito.cartonbox.controller.XmlDownloader;
import org.shujito.cartonbox.model.BlogEntry;
import org.shujito.cartonbox.utils.ConcurrentTask;
import org.shujito.cartonbox.view.adapters.BlogListAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

/**
 * is not a fragment so it doesn't belong to
 * 'org.shujito.cartonbox.view.fragments.dialogs'
 * @author Alberto
 *
 */
public class BlogNewsDialog implements OnItemClickListener
{
	static final String BLOG_ADDRESS = "http://www.shujito.org/tagged/cartonbox/rss";
	
	Context context;
	ListView mLvEntries = null;
	CheckBox mCbxNoshowagain = null;
	BlogListAdapter mBlogListAdapter = null;
	XmlDownloader downloader = null;
	
	public BlogNewsDialog(Context context)
	{
		this.context = context;
	}
	
	public Dialog createDialog()
	{
		return this.createDialog(true);
	}
	
	public Dialog createDialog(boolean showCheckbox)
	{
		LayoutInflater inf = LayoutInflater.from(this.context);
		
		View v = inf.inflate(R.layout.dialog_blognews, null);
		
		this.mBlogListAdapter = new BlogListAdapter(this.context);
		this.downloader = new BlogRssXmlDownloader(BLOG_ADDRESS);
		this.downloader.setOnResponseReceivedListener(this.mBlogListAdapter);
		this.downloader.setOnErrorListener(this.mBlogListAdapter);
		ConcurrentTask.execute(this.downloader);
		
		this.mLvEntries = (ListView)v.findViewById(R.id.dialog_blognews_lventries);
		this.mLvEntries.setAdapter(this.mBlogListAdapter);
		this.mLvEntries.setOnItemClickListener(this);
		
		this.mCbxNoshowagain = (CheckBox)v.findViewById(R.id.dialog_blognews_cbxnoshow);
		this.mCbxNoshowagain.setChecked(false);
		if(!showCheckbox)
			this.mCbxNoshowagain.setVisibility(View.GONE);
		
		return new AlertDialog.Builder(this.context)
			.setTitle(R.string.app_name)
			//.setMessage(null)
			.setPositiveButton(android.R.string.ok, null)
			//.setNegativeButton(R.string.noshowagain, this)
			.setView(v)
			.create();
	}
	
	@Override
	public void onItemClick(AdapterView<?> dad, View v, int idx, long id)
	{
		String url = ((BlogEntry)this.mBlogListAdapter.getItem(idx)).getLink();
		Intent ntn = new Intent(Intent.ACTION_VIEW);
		// I will trust you...
		ntn.setData(Uri.parse(url));
		this.context.startActivity(ntn);
	}
}
