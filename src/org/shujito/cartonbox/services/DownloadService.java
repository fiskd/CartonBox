package org.shujito.cartonbox.services;

import org.shujito.cartonbox.R;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class DownloadService extends IntentService
{
	public static String EXTRA_FILENAME = "org.shujito.cartonbox.services.EXTRA_FILENAME";
	
	public class DownloadProgressReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
		}
	}
	
	DownloadProgressReceiver mDownloadProgressReceiver = null;
	NotificationManager mNotificationManager = null;
	NotificationCompat.Builder mBuilder = null;
	
	public DownloadService()
	{
		super("DownloadService");
		//this.mDownloadProgressReceiver = new DownloadProgressReceiver();
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		this.mNotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		this.mBuilder = new NotificationCompat.Builder(this);
		this.mBuilder.setSmallIcon(R.drawable.ic_action_download);
		this.mBuilder.setOngoing(true);
		this.mBuilder.setContentTitle(this.getText(R.string.downloading));
		this.mBuilder.setTicker(this.getText(R.string.download_started));
		this.mBuilder.setProgress(0, 0, true);
		this.mBuilder.setContentIntent(pendingIntent);
		// resource id used as notification id
		this.mNotificationManager.notify(R.string.downloading, this.mBuilder.build());
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		// background threads <3
		// TODO: stuff
		/* how to do progress:
		this.mBuilder.setProgress(total, progress, false);
		this.mBuilder.setContentText(String.format("%s out of %s", progress, total));
		this.mNotificationManager.notify(R.string.downloading, this.mBuilder.build());
		//*/
		String filename = intent.getExtras().getString(EXTRA_FILENAME);
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.mBuilder.setTicker(this.getText(R.string.download_complete));
		this.mBuilder.setContentText("done");
		this.mBuilder.setAutoCancel(true);
		this.mBuilder.setOngoing(false);
		this.mBuilder.setProgress(0, 0, false);
		this.mNotificationManager.notify(R.string.downloading, this.mBuilder.build());
	}
}
