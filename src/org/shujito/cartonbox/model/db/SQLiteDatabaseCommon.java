package org.shujito.cartonbox.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class SQLiteDatabaseCommon extends SQLiteOpenHelper
{
	/* static */
	static final int VERSION = 0x00000001;
	static final String DB_NAME = "sites.db3";
	
	// DO NOT EDIT
	static final String SQL_CREATE = "CREATE TABLE %s(%s);";
	static final String SQL_DROP = "DROP TABLE IF EXISTS %s;";
	static final String SQL_TEXT = "%s TEXT";
	static final String SQL_INTEGER = "%s INTEGER";
	static final String SQL_PK = "%s INTEGER PRIMARY KEY AUTOINCREMENT";
	
	public SQLiteDatabaseCommon(Context context)
	{
		super(context, DB_NAME, null, VERSION);
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
}
