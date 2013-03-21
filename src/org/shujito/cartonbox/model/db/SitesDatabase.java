package org.shujito.cartonbox.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SitesDatabase extends SQLiteOpenHelper
{
	private static int VERSION = 0x00000001;
	
	private static String TABLE_SITES = "sites";

	private static String KEY_ID = "id";
	private static String KEY_URL = "url";
	private static String KEY_POSTS_API = "posts_api";
	private static String KEY_POOLS_API = "pools_api";
	private static String KEY_COMMENTS_API = "comments_api";
	private static String KEY_NOTES_API = "notes_api";
	private static String KEY_ARTISTS_API = "artists_api";
	private static String KEY_TAGS_API = "tags_api";
	
	// DO NOT EDIT
	private static final String SQL_CREATE = "CREATE TABLE %s( %s );";
	private static final String SQL_DROP = "DROP TABLE IF EXISTS %s;";
	private static final String SQL_TEXT = "%s TEXT";
	private static final String SQL_PK = "%s INTEGER PRIMARY_KEY UNIQUE";
	
	public SitesDatabase(Context context)
	{
		super(context, null, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String[][] fields =
			{
				{ SQL_PK, KEY_ID },
				{ SQL_TEXT, KEY_URL },
				{ SQL_TEXT, KEY_POSTS_API },
				{ SQL_TEXT, KEY_POOLS_API },
				{ SQL_TEXT, KEY_COMMENTS_API },
				{ SQL_TEXT, KEY_NOTES_API },
				{ SQL_TEXT, KEY_ARTISTS_API },
				{ SQL_TEXT, KEY_TAGS_API }
			};
		
		StringBuilder struct = new StringBuilder();
		for(int idx = 0; idx < fields.length; idx++)
		{
			if(idx > 0)
				struct.append(',');
			
			String[] field = fields[idx];
			struct.append(String.format(field[0], field[1]));
		}
		
		String query = String.format(SQL_CREATE, TABLE_SITES, struct.toString());
		db.execSQL(query);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(String.format(SQL_DROP, TABLE_SITES));
		this.onCreate(db);
	}
}
