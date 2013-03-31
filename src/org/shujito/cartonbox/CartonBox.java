package org.shujito.cartonbox;

import org.shujito.cartonbox.controller.Imageboard;

import android.app.Application;

public class CartonBox extends Application
{
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
