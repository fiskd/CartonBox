package org.shujito.cartonbox.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.shujito.cartonbox.CartonBox;
import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Download;
import org.shujito.cartonbox.model.db.DownloadsDB;
import org.shujito.cartonbox.util.io.SimpleMediaScan;
import org.shujito.cartonbox.view.activity.DownloadsActivity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

public class DownloadService extends IntentService
{
	static final int BUFFER = 8192;
	public static String EXTRA_DISPLAY = "org.shujito.cartonbox.services.EXTRA_DISPLAY";
	public static String EXTRA_DESTINATION = "org.shujito.cartonbox.services.EXTRA_DESTINATION";
	public static String EXTRA_SOURCE = "org.shujito.cartonbox.services.EXTRA_SOURCE";
	public static String EXTRA_DIRECTORY = "org.shujito.cartonbox.services.EXTRA_DIRECTORY";
	
	public DownloadService()
	{
		super("DownloadService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		// background tasks are love
		//int id = (int)System.currentTimeMillis();
		// intent bundle extras
		String destination = null;
		String source = null;
		String directory = Environment.DIRECTORY_PICTURES;
		String display = null;
		
		if(intent.getExtras() != null)
		{
			destination = intent.getExtras().getString(EXTRA_DESTINATION);
			source = intent.getExtras().getString(EXTRA_SOURCE);
			directory = intent.getExtras().getString(EXTRA_DIRECTORY);
			display = intent.getExtras().getString(EXTRA_DISPLAY);
		}
		
		// blank intent
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		// get notifications manager
		NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		// build notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		//builder.setAutoCancel(true);
		builder.setContentIntent(pendingIntent);
		
		File dest = Environment.getExternalStoragePublicDirectory(directory);
		File fileDest = new File(dest.getAbsolutePath(), destination);
		//*
		Intent ntn = new Intent(this, DownloadsActivity.class);
		//ntn.setDataAndType(Uri.fromFile(fileDest), "image/*");
		PendingIntent viewDownloadsIntent = PendingIntent.getActivity(this, 0, ntn, 0);
		//*/
		// file doesn't exist
		if(!fileDest.exists())
		{
			dest.mkdirs();
			// keep the notification there
			builder.setOngoing(true);
			// indeterminate progress
			builder.setProgress(0, 0, true);
			// notification title
			builder.setContentTitle(this.getText(R.string.downloading));
			// the text that goes into the bar
			builder.setTicker(this.getText(R.string.download_started));
			// what to display as content
			builder.setContentText(display != null ? display : destination);
			// icon
			builder.setSmallIcon(android.R.drawable.stat_sys_download);
			// resource id used as notification id
			// TODO: make a pref: should use system millis for multiple notifications
			notificationManager.notify(R.string.app_name, builder.build());
			try
			{
				// have an url
				URL url = new URL(source);
				// open it
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				// hotlinking bypass
				urlConnection.setRequestProperty("Referer", source);
				// connect now
				urlConnection.connect();
				// get stream size
				int size = urlConnection.getContentLength();
				// buffering the network
				InputStream input = new BufferedInputStream(urlConnection.getInputStream());
				// 
				OutputStream output = null;
				try
				{
					// destination path
					
					// create from file
					output = new FileOutputStream(fileDest);
					// hold some data on this buffer
					byte[] data = new byte[BUFFER];
					int total = 0;
					int bytesRead;
					// formatters
					
					// start reading the stream
					while((bytesRead = input.read(data)) > 0)
					{
						total += bytesRead;
						// notify some progress...
						if(size > 0)
						{
							builder.setProgress(size, total, false);
							notificationManager.notify(R.string.app_name, builder.build());
						}
						output.write(data, 0, bytesRead);
					}
				}
				finally
				{
					if(output != null)
					{
						output.flush();
						output.close();
					}
				}
				builder.setContentTitle(this.getText(R.string.download_complete));
				builder.setTicker(this.getText(R.string.download_complete));
			}
			catch(Exception ex)
			{
				Logger.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
				builder.setContentTitle(this.getText(R.string.download_failed));
				builder.setTicker(this.getText(R.string.download_failed));
			}
			
			Download dl = new Download();
			dl.setId(System.currentTimeMillis());
			dl.setLocation(fileDest.getAbsolutePath());
			dl.setName(display);
			dl.setSource(source);
			
			DownloadsDB ddb = new DownloadsDB(this);
			ddb.add(dl);
			
			builder.setOngoing(false);
			builder.setProgress(0, 0, false);
			builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
			builder.setContentIntent(viewDownloadsIntent);
			notificationManager.notify(R.string.app_name, builder.build());
			// this helps scanning the files so they show up on the gallery
			// XXX: sending 'this' will leak, send application context instead
			new SimpleMediaScan(CartonBox.getInstance(), fileDest);
		}
		else
		{
			builder.setContentTitle(this.getText(R.string.already_downloaded));
			builder.setTicker(this.getText(R.string.already_downloaded));
			builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
			builder.setContentIntent(viewDownloadsIntent);
			notificationManager.notify(R.string.app_name, builder.build());
		}
	}
}
