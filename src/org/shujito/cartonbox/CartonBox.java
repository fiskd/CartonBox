package org.shujito.cartonbox;

import org.shujito.cartonbox.controller.Imageboard;

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
		
		//Logger.i("CartonBox::onCreate", "checking if it has run before");
		if(Preferences.isFirstRun())
		{
			Logger.i("CartonBox::onCreate", "first run");
			Preferences.init();
		}
	}
	
	private Imageboard imageboard = null;
	
	public Imageboard getImageboard()
	{
		return imageboard;
	}
	public void setImageboard(Imageboard imageboard)
	{
		this.imageboard = imageboard;
	}
}
