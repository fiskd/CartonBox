package org.shujito.cartonbox.view.fragment;

import org.shujito.cartonbox.controller.task.ImageDownloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

// TODO: handle image downloads here
// source: http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
// source: https://github.com/alexjlockwood/worker-fragments/tree/master/src/com/adp/retaintask
public class ImageDownloaderFragment extends Fragment
{
	public interface ImageDownloaderFragmentCallbacks
	{
		public void onDownloadProgress(ImageDownloader downloader, float percent);
		public void onThumbDownloaded(Bitmap bmp);
		public void onPreviewDownloaded(Bitmap bmp);
	}
	
	ImageDownloaderFragmentCallbacks callbacks = null;
	
	ImageDownloader thumbDownloader = null;
	ImageDownloader sampleDownloader = null;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		Fragment parent = this.getParentFragment();
		if(parent instanceof ImageDownloaderFragmentCallbacks)
		{
			this.callbacks = (ImageDownloaderFragmentCallbacks)parent;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}
}
