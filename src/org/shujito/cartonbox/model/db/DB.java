package org.shujito.cartonbox.model.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DB<T> extends SQLiteOpenHelper
{
	/* static */
	
	// DO NOT EDIT
	static final String SQL_CREATE = "CREATE TABLE %s(%s);";
	static final String SQL_DROP = "DROP TABLE IF EXISTS %s;";
	static final String SQL_TEXT = "%s TEXT";
	static final String SQL_INTEGER = "%s INTEGER";
	static final String SQL_BLOB = "%s BLOB";
	static final String SQL_PK = "%s INTEGER PRIMARY KEY AUTOINCREMENT";
	
	public DB(Context context, String dbname, int version)
	{
		super(context, dbname, null, version);
	}
	
	protected void createTable(SQLiteDatabase db, String[][] fields, String tableName)
	{
		StringBuilder struct = new StringBuilder();
		for(int idx = 0; idx < fields.length; idx++)
		{
			if(idx > 0)
				struct.append(',');
			
			String[] field = fields[idx];
			struct.append(String.format(field[0], field[1]));
		}
		
		String query = String.format(SQL_CREATE, tableName, struct.toString());
		db.execSQL(query);
	}
	
	public abstract void add(T record);
	public abstract T get(int id);
	public abstract List<T> getAll();
	public abstract int getCount();
	public abstract void update(T record);
	public abstract void delete(T record);
}
