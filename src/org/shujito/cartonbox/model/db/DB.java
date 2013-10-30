package org.shujito.cartonbox.model.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DB<T> extends SQLiteOpenHelper
{
	/* static */
	//static final String TABLE_NAME = null;
	
	// DO NOT EDIT
	static final String SQL_CREATE = "CREATE TABLE %s(%s);";
	static final String SQL_DROP = "DROP TABLE IF EXISTS %s;";
	static final String SQL_TEXT = "%s TEXT";
	static final String SQL_INTEGER = "%s INTEGER";
	static final String SQL_BLOB = "%s BLOB";
	static final String SQL_PK = "%s INTEGER PRIMARY KEY AUTOINCREMENT";
	
	private String tableName = null;
	private String fieldId = null;
	private String orderBy = null;
	private boolean ascending = false;
	
	public DB(Context context, String dbname, int version, String tableName, String fieldId, String orderBy, boolean ascending)
	{
		super(context, dbname, null, version);
		this.tableName = tableName;
		this.fieldId = fieldId;
		this.orderBy = orderBy;
		this.ascending = ascending;
	}
	
	protected void createTable(SQLiteDatabase db, String[][] fields)
	{
		// the create query will be created here
		StringBuilder struct = new StringBuilder();
		for(int idx = 0; idx < fields.length; idx++)
		{
			if(idx > 0)
				struct.append(',');
			// create the query with the provided struct
			String[] field = fields[idx];
			struct.append(String.format(field[0], field[1]));
		}
		// here it is
		String query = String.format(SQL_CREATE, tableName, struct.toString());
		db.execSQL(query);
	}
	
	protected abstract ContentValues fromRecord(T record);
	protected abstract T fromCursor(Cursor cursor);
	
	public final boolean add(T record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		// insert entry
		long inserted = db.insert(this.tableName, null, this.fromRecord(record));
		db.close();
		return inserted > 0;
	}
	
	public final T get(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(
				this.tableName,
				new String[] { }, // KEY_URL, KEY_NAME, KEY_POSTS_API, KEY_POOLS_API, KEY_COMMENTS_API, KEY_NOTES_API, KEY_ARTISTS_API, KEY_TAGS_API },
				String.format("%s=?", this.fieldId),
				new String[]{ String.valueOf(id) },
				null,
				null,
				String.format("%s %s", this.orderBy, this.ascending ? "ASC" : "DESC" )
			);
		
		if(cursor != null && cursor.moveToFirst())
		{
			T site = this.fromCursor(cursor);
			return site;
		}
		
		return null;
	}
	
	public final List<T> getAll()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		List<T> sites = new ArrayList<T>();
		
		Cursor cursor = db.query(
				this.tableName,
				new String[] { },
				null,
				null,
				null,
				null,
				String.format("%s %s", this.orderBy, this.ascending ? "ASC" : "DESC")
			);
		
		if(cursor.moveToFirst())
		{
			while(true)
			{
				T site = this.fromCursor(cursor);
				sites.add(site);
				
				if(!cursor.moveToNext())
					break;
			}
		}
		
		db.close();
		return sites;
	}
	
	public final int getCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		//Cursor cursor = db.rawQuery("SELECT * FROM ?", new String[]{ TABLE_SITES });
		Cursor cursor = db.query(this.tableName, new String[]{ this.fieldId }, null, null, null, null, null);
		
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	
	public final boolean update(T record, long id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rowsAffected = db.update(
				this.tableName,
				this.fromRecord(record),
				String.format("%s=?", this.fieldId),
				new String[]{ String.valueOf(id) }
			);
		db.close();
		return rowsAffected > 0;
	}
	
	public final boolean delete(T record, long id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int affectedRows = db.delete(
				this.tableName,
				String.format("%s=?", this.fieldId),
				new String[]{ String.valueOf(id) }
			);
		db.close();
		return affectedRows > 0;
	}
}
