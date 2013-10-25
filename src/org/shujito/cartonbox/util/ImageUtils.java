package org.shujito.cartonbox.util;

import java.io.File;
import java.io.FileInputStream;

import org.shujito.cartonbox.Logger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// I got this code from android documentation
// http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
public class ImageUtils
{
	public static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int width = options.outWidth;
		final int height = options.outHeight;
		int sampleSize = 0;
		
		if((reqWidth > 0 && reqHeight > 0) && (width > reqWidth || height > reqHeight))
		{
			// calculate width and height ratios
			final int widthRatio = Math.round((float)width / (float)reqWidth);
			final int heightRatio = Math.round((float)height / (float)reqHeight);
			
			// select the smallest ratio
			sampleSize = widthRatio >= heightRatio ? widthRatio : heightRatio;
		}
		
		return sampleSize;
	}
	
	public static Bitmap decodeSampledBitmap(File file, int width, int height) throws Exception
	{
		// decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(new FileInputStream(file), null, options);
		options.inSampleSize = calculateSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		
		try
		{
			//return BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			// try decoding once
			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			// failed, let's try another way
			if(bmp == null)
			{
				FileInputStream fis = new FileInputStream(file);
				// try with a byte array instead
				byte[] fileBytes = new byte[(int)file.length()];
				fis.read(fileBytes);
				bmp = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length, options);
				fis.close();
			}
			return bmp;
		}
		catch(Exception ex)
		{
			Logger.e("ImageUtils::decodeSampledBitmap", ex.getMessage(), ex);
			return null;
		}
		catch(OutOfMemoryError ex)
		{
			Logger.e("ImageUtils::decodeSampledBitmap", ex.getMessage(), ex);
			System.gc();
			return null;
		}
	}
}
