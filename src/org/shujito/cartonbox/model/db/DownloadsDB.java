package org.shujito.cartonbox.model.db;

import java.util.ArrayList;
import java.util.List;

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
		super(context, DB_NAME, VERSION);
	}
	
	/* meth */
	private ContentValues fromRecord(Download record)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_ID, record.getId());
		values.put(KEY_NAME, record.getName());
		values.put(KEY_SOURCE, record.getSource());
		values.put(KEY_LOCATION, record.getLocation());
		return values;
	}

	private Download fromCursor(Cursor cursor)
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

		this.createTable(db, fields, TABLE_DOWNLOADS);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(String.format(SQL_DROP, TABLE_DOWNLOADS));
		this.onCreate(db);
	}
	
	@Override
	public boolean add(Download record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		// insert entry
		long inserted = db.insert(TABLE_DOWNLOADS, null, this.fromRecord(record));
		db.close();
		return inserted > 0;
	}
	
	@Override
	public Download get(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(
				TABLE_DOWNLOADS,
				new String[] { }, // KEY_URL, KEY_NAME, KEY_POSTS_API, KEY_POOLS_API, KEY_COMMENTS_API, KEY_NOTES_API, KEY_ARTISTS_API, KEY_TAGS_API },
				String.format("%s=?", KEY_ID),
				new String[]{ String.valueOf(id) },
				null,
				null,
				null //String.format("%s ASC", KEY_NAME)
			);
		
		if(cursor != null && cursor.moveToFirst())
		{
			Download site = this.fromCursor(cursor);
			return site;
		}
		
		return null;
	}
	
	@Override
	public List<Download> getAll()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		List<Download> sites = new ArrayList<Download>();
		
		Cursor cursor = db.query(
				TABLE_DOWNLOADS,
				new String[] { },
				null,
				null,
				null,
				null,
				null //String.format("%s ASC", KEY_ID)
			);
		
		if(cursor.moveToFirst())
		{
			while(true)
			{
				Download site = this.fromCursor(cursor);
				sites.add(site);
				
				if(!cursor.moveToNext())
					break;
			}
		}
		
		db.close();
		return sites;
	}
	
	@Override
	public int getCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_DOWNLOADS, new String[]{ KEY_NAME }, null, null, null, null, null);
		
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	
	@Override
	public boolean update(Download record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rowsAffected = db.update(
				TABLE_DOWNLOADS,
				this.fromRecord(record),
				String.format("%s=?", KEY_ID),
				new String[]{ String.valueOf(record.getId()) }
			);
		db.close();
		return rowsAffected > 0;
	}
	
	@Override
	public boolean delete(Download record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int affectedRows = db.delete(
				TABLE_DOWNLOADS,
				String.format("%s=?", KEY_ID),
				new String[]{ String.valueOf(record.getId()) }
			);
		db.close();
		return affectedRows > 0;
	}
}
