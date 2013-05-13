package org.shujito.cartonbox;

import org.shujito.cartonbox.controller.ImageboardPosts;

import android.app.Application;

public class CartonBox extends Application
{
	// application instance, we can access the context everywhere with this
	private static CartonBox instance = null;
	
	public static CartonBox getInstance()
	{
		return instance;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		
		if(Preferences.isFirstRun())
		{
			Preferences.init();
		}
	}
	
	private ImageboardPosts imageboardPosts = null;
	private BitmapCache bitmapCache = null;
	
	public ImageboardPosts getImageboard()
	{
		return this.imageboardPosts;
	}
	public BitmapCache getBitmapCache()
	{
		return this.bitmapCache;
	}
	
	public void setImageboard(ImageboardPosts imageboard)
	{
		this.imageboardPosts = imageboard;
	}
	public void setBitmapCache(BitmapCache bitmapCache)
	{
		this.bitmapCache = bitmapCache;
	}
}
