package org.shujito.cartonbox.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

// http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
public class BitmapCache
{
	LruCache<Long, Bitmap> bitmaps = null;
	
	public BitmapCache()
	{
		int memorySize = (int)(Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = memorySize / 8;
		
		this.bitmaps = new LruCache<Long, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(Long key, Bitmap value)
			{
				return (value.getRowBytes() * value.getHeight()) / 1024;
			}
		};
	}
	
	public void addBitmapToMemCache(long key, Bitmap bitmap)
	{
		synchronized(bitmap)
		{
			if(this.bitmaps.get(key) == null)
				this.bitmaps.put(key, bitmap);
		}
	}
	
	public Bitmap getBitmapFromMemCache(long key)
	{
		synchronized(bitmaps)
		{
			return this.bitmaps.get(key);
		}
	}
}
