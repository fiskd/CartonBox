package org.shujito.cartonbox;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

// http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
public class BitmapCache
{
	LruCache<Integer, Bitmap> bitmaps = null;
	
	public BitmapCache()
	{
		int memorySize = (int)(Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = memorySize / 8;
		
		this.bitmaps = new LruCache<Integer, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(Integer key, Bitmap value)
			{
				return (value.getRowBytes() * value.getHeight()) / 1024;
			}
		};
	}
	
	public void addBitmapToMemCache(int key, Bitmap bitmap)
	{
		if(this.bitmaps.get(key) == null)
			this.bitmaps.put(key, bitmap);
	}
	
	public Bitmap getBitmapFromMemCache(int key)
	{
		return this.bitmaps.get(key);
	}
}
