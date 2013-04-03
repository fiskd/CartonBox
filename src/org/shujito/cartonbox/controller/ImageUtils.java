package org.shujito.cartonbox.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils
{
	public static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int width = options.outWidth;
		final int height = options.outHeight;
		int sampleSize = 1;
		
		if((reqWidth > 0 && reqHeight > 0) && (width > reqWidth || height > reqHeight))
		{
			// calculate width and height ratios
			final int widthRatio = Math.round((float)width / (float)reqWidth);
			final int heightRatio = Math.round((float)height / (float)reqHeight);
			
			// select the smallest ratio
			sampleSize = widthRatio > heightRatio ? widthRatio : heightRatio;
		}
		
		return sampleSize;
	}
	
	public static Bitmap decodeSampledBitmap(byte[] imageData, int width, int height)
	{
		// decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
		
		// calculate sample size
		options.inSampleSize = calculateSampleSize(options, width, height);
		
		// actually decode the bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
	}
}
