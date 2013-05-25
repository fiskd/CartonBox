package org.shujito.cartonbox.view.activities;

import org.shujito.cartonbox.view.widget.PostImageView;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class ZoomViewActivity extends SherlockActivity
{
	PostImageView ziv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.ziv = new PostImageView(this);
		this.setContentView(this.ziv);
	}
}
