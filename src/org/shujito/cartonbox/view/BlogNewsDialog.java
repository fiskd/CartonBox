package org.shujito.cartonbox.view;

import org.shujito.cartonbox.R;
import org.shujito.cartonbox.controller.BlogRssXmlDownloader;
import org.shujito.cartonbox.controller.XmlDownloader;
import org.shujito.cartonbox.utils.ConcurrentTask;
import org.shujito.cartonbox.view.adapters.BlogListAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

/**
 * is not a fragment so it doesn't belong to
 * 'org.shujito.cartonbox.view.fragments.dialogs'
 * @author Alberto
 *
 */
public class BlogNewsDialog
{
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
		this.downloader = new BlogRssXmlDownloader(this.context.getString(R.string.sitelink));
		this.downloader.setOnResponseReceivedListener(this.mBlogListAdapter);
		this.downloader.setOnErrorListener(this.mBlogListAdapter);
		ConcurrentTask.execute(this.downloader);
		
		this.mLvEntries = (ListView)v.findViewById(R.id.dialog_blognews_lventries);
		this.mLvEntries.setAdapter(this.mBlogListAdapter);
		
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
}
