package org.shujito.cartonbox.model.db;

import org.shujito.cartonbox.model.Download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DownloadsDB extends DB<Download>
{
	static final String DB_NAME = "downloads.db3";
	static final int VERSION = 0x00000001;
	
	static final String TABLE_DOWNLOADS = "downloads";

	static final String KEY_ID = "id";
	static final String KEY_NAME = "name";
	static final String KEY_SOURCE = "source";
	static final String KEY_LOCATION = "location";
	
	/* constructor */
	public DownloadsDB(Context context)
	{
		super(context, DB_NAME, VERSION, TABLE_DOWNLOADS, KEY_ID, KEY_ID, true);
	}
	
	/* meth */
	@Override
	protected ContentValues fromRecord(Download record)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_ID, record.getId());
		values.put(KEY_NAME, record.getName());
		values.put(KEY_SOURCE, record.getSource());
		values.put(KEY_LOCATION, record.getLocation());
		return values;
	}
	
	@Override
	protected Download fromCursor(Cursor cursor)
	{
		Download download = new Download()
			.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)))
			.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)))
			.setSource(cursor.getString(cursor.getColumnIndex(KEY_SOURCE)))
			.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
		return download;
	}

	/* override */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String[][] fields =
			{
				{ SQL_PK, KEY_ID },
				{ SQL_TEXT, KEY_NAME },
				{ SQL_TEXT, KEY_SOURCE },
				{ SQL_TEXT, KEY_LOCATION }
			};
		
		this.createTable(db, fields);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}
	
	public boolean delete(Download download)
	{
		return this.delete(download, download.getId());
	}
}
