package org.shujito.cartonbox.model.db;

import org.shujito.cartonbox.model.Site;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// so useful:
// http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
public final class SitesDB extends DB<Site>
{
	/* static */
	static final String DB_NAME = "sites.db3";
	static final int VERSION = 0x00000002;
	
	static final String TABLE_SITES = "sites";
	
	static final String KEY_ID = "id";
	static final String KEY_ICON = "icon";
	static final String KEY_URL = "url";
	static final String KEY_TYPE = "type";
	static final String KEY_NAME = "name";
	static final String KEY_POST_VIEW_API = "post_view_api";
	static final String KEY_POSTS_API = "posts_api";
	static final String KEY_POOLS_API = "pools_api";
	static final String KEY_COMMENTS_API = "comments_api";
	static final String KEY_NOTES_API = "notes_api";
	static final String KEY_ARTISTS_API = "artists_api";
	static final String KEY_TAGS_API = "tags_api";
	
	/* constructor */
	public SitesDB(Context context)
	{
		super(context, DB_NAME, VERSION, TABLE_SITES, KEY_ID, KEY_NAME, true);
	}
	
	/* meth */
	@Override
	protected ContentValues fromRecord(Site record)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_ID, record.getId());
		values.put(KEY_URL, record.getUrl());
		values.put(KEY_TYPE, record.getType().getValue());
		values.put(KEY_ICON, record.getIconFile());
		values.put(KEY_NAME, record.getName());
		values.put(KEY_POST_VIEW_API, record.getPostViewApi());
		values.put(KEY_POSTS_API, record.getPostsApi());
		values.put(KEY_POOLS_API, record.getPoolsApi());
		values.put(KEY_COMMENTS_API, record.getCommentsApi());
		values.put(KEY_NOTES_API, record.getNotesApi());
		values.put(KEY_ARTISTS_API, record.getArtistsApi());
		values.put(KEY_TAGS_API, record.getTagsApi());
		return values;
	}
	
	@Override
	protected Site fromCursor(Cursor cursor)
	{
		// Good, now THIS looks DRY
		int iType = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));
		Site.Type type = null;
		
		switch(iType)
		{
			case 1: type = Site.Type.Danbooru1; break;
			case 2: type = Site.Type.Danbooru2; break;
			case 3: type = Site.Type.Gelbooru; break;
		}
		
		Site site = new Site()
			.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)))
			.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)))
			.setType(type)
			.setIconFile(cursor.getString(cursor.getColumnIndex(KEY_ICON)))
			.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)))
			.setPostViewApi(cursor.getString(cursor.getColumnIndex(KEY_POST_VIEW_API)))
			.setPostsApi(cursor.getString(cursor.getColumnIndex(KEY_POSTS_API)))
			.setPoolsApi(cursor.getString(cursor.getColumnIndex(KEY_POOLS_API)))
			.setCommentsApi(cursor.getString(cursor.getColumnIndex(KEY_COMMENTS_API)))
			.setNotesApi(cursor.getString(cursor.getColumnIndex(KEY_NOTES_API)))
			.setArtistsApi(cursor.getString(cursor.getColumnIndex(KEY_ARTISTS_API)))
			.setTagsApi(cursor.getString(cursor.getColumnIndex(KEY_TAGS_API)));
		
		return site;
	}

	/* override */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String[][] fields =
			{
				{ SQL_PK, KEY_ID },
				{ SQL_TEXT, KEY_URL },
				{ SQL_INTEGER, KEY_TYPE },
				{ SQL_TEXT, KEY_ICON }, // filename
				{ SQL_TEXT, KEY_NAME },
				{ SQL_TEXT, KEY_POST_VIEW_API },
				{ SQL_TEXT, KEY_POSTS_API },
				{ SQL_TEXT, KEY_POOLS_API },
				{ SQL_TEXT, KEY_COMMENTS_API },
				{ SQL_TEXT, KEY_NOTES_API },
				{ SQL_TEXT, KEY_ARTISTS_API },
				{ SQL_TEXT, KEY_TAGS_API }
			};
		
		this.createTable(db, fields);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(String.format(SQL_DROP, TABLE_SITES));
		this.onCreate(db);
	}
	
	public boolean delete(Site site)
	{
		return this.delete(site, site.getId());
	}
	
	public void update(Site site)
	{
		this.update(site, site.getId());
	}
}
