package org.shujito.cartonbox.utils.io;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

// http://stackoverflow.com/a/5815005
// cool solution for scanning a downloaded file
public class SimpleMediaScan implements MediaScannerConnectionClient
{
	private MediaScannerConnection mMediaScannerConnection = null;
	private File mFile = null;
	
	public SimpleMediaScan(Context context, File f)
	{
		this.mFile = f;
		this.mMediaScannerConnection = new MediaScannerConnection(context, this);
		this.mMediaScannerConnection.connect();
	}
	
	@Override
	public void onMediaScannerConnected()
	{
		this.mMediaScannerConnection.scanFile(this.mFile.getAbsolutePath(), null);
	}
	
	@Override
	public void onScanCompleted(String path, Uri uri)
	{
		this.mMediaScannerConnection.disconnect();
	}
}
