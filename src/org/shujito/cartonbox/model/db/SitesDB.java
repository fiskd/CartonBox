package org.shujito.cartonbox.model.db;

import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.model.Site;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SitesDB extends DB<Site>
{
	/* static */
	static String TABLE_SITES = "sites";
	
	static final String KEY_ID = "id";
	static final String KEY_ICONID = "iconid";
	static final String KEY_URL = "url";
	static final String KEY_TYPE = "type";
	static final String KEY_NAME = "name";
	static final String KEY_POSTS_API = "posts_api";
	static final String KEY_POOLS_API = "pools_api";
	static final String KEY_COMMENTS_API = "comments_api";
	static final String KEY_NOTES_API = "notes_api";
	static final String KEY_ARTISTS_API = "artists_api";
	static final String KEY_TAGS_API = "tags_api";
	
	/* constructor */
	public SitesDB(Context context)
	{
		super(context);
	}
	
	/* meth */
	
	/* override */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String[][] fields =
			{
				{ SQL_PK, KEY_ID },
				{ SQL_INTEGER, KEY_ICONID },
				{ SQL_TEXT, KEY_URL },
				{ SQL_INTEGER, KEY_TYPE },
				{ SQL_TEXT, KEY_NAME },
				{ SQL_TEXT, KEY_POSTS_API },
				{ SQL_TEXT, KEY_POOLS_API },
				{ SQL_TEXT, KEY_COMMENTS_API },
				{ SQL_TEXT, KEY_NOTES_API },
				{ SQL_TEXT, KEY_ARTISTS_API },
				{ SQL_TEXT, KEY_TAGS_API }
			};
		
		this.createTable(db, fields, TABLE_SITES);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(String.format(SQL_DROP, TABLE_SITES));
		this.onCreate(db);
	}
	
	@Override
	public void add(Site record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		//values.put(KEY_ID, record.getId());
		values.put(KEY_ICONID, record.getIconid());
		values.put(KEY_URL, record.getUrl());
		values.put(KEY_TYPE, record.getType().getValue());
		values.put(KEY_NAME, record.getName());
		values.put(KEY_POSTS_API, record.getPostsApi());
		values.put(KEY_POOLS_API, record.getPoolsApi());
		values.put(KEY_COMMENTS_API, record.getCommentsApi());
		values.put(KEY_NOTES_API, record.getNotesApi());
		values.put(KEY_ARTISTS_API, record.getArtistsApi());
		values.put(KEY_TAGS_API, record.getTagsApi());
		
		// insert entry
		db.insert(TABLE_SITES, null, values);
		db.close();
	}
	
	@Override
	public Site get(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(
				TABLE_SITES,
				new String[] { }, // KEY_URL, KEY_NAME, KEY_POSTS_API, KEY_POOLS_API, KEY_COMMENTS_API, KEY_NOTES_API, KEY_ARTISTS_API, KEY_TAGS_API },
				String.format("%s=?", KEY_ID),
				new String[]{ String.valueOf(id) },
				null,
				null,
				String.format("%s ASC", KEY_NAME)
			);
		
		if(cursor != null && cursor.moveToFirst())
		{
			int iType = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));
			Site.Type type = null;
			
			switch(iType)
			{
				case 1: type = Site.Type.Danbooru1; break;
				case 2: type = Site.Type.Danbooru2; break;
				case 3: type = Site.Type.Gelbooru; break;
			}
			
			Site site = new Site(id)
				//.setId(id)
				.setIconid(cursor.getInt(cursor.getColumnIndex(KEY_ICONID)))
				.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)))
				.setType(type)
				.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)))
				.setPostsApi(cursor.getString(cursor.getColumnIndex(KEY_POSTS_API)))
				.setPoolsApi(cursor.getString(cursor.getColumnIndex(KEY_POOLS_API)))
				.setCommentsApi(cursor.getString(cursor.getColumnIndex(KEY_COMMENTS_API)))
				.setNotesApi(cursor.getString(cursor.getColumnIndex(KEY_NOTES_API)))
				.setArtistsApi(cursor.getString(cursor.getColumnIndex(KEY_ARTISTS_API)))
				.setTagsApi(cursor.getString(cursor.getColumnIndex(KEY_TAGS_API)));
			
			return site;
		}
		
		return null;
	}
	
	@Override
	public List<Site> getAll()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		List<Site> sites = new ArrayList<Site>();
		
		Cursor cursor = db.query(
				TABLE_SITES,
				new String[] { },
				null,
				null,
				null,
				null,
				String.format("%s ASC", KEY_NAME)
			);
		
		if(cursor.moveToFirst())
		{
			while(true)
			{
				int iType = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));
				Site.Type type = null;
				
				switch(iType)
				{
					case 1: type = Site.Type.Danbooru1; break;
					case 2: type = Site.Type.Danbooru2; break;
					case 3: type = Site.Type.Gelbooru; break;
				}
				
				Site site = new Site(cursor.getInt(cursor.getColumnIndex(KEY_ID)))
					//.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)))
					.setIconid(cursor.getInt(cursor.getColumnIndex(KEY_ICONID)))
					.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)))
					.setType(type)
					.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)))
					.setPostsApi(cursor.getString(cursor.getColumnIndex(KEY_POSTS_API)))
					.setPoolsApi(cursor.getString(cursor.getColumnIndex(KEY_POOLS_API)))
					.setCommentsApi(cursor.getString(cursor.getColumnIndex(KEY_COMMENTS_API)))
					.setNotesApi(cursor.getString(cursor.getColumnIndex(KEY_NOTES_API)))
					.setArtistsApi(cursor.getString(cursor.getColumnIndex(KEY_ARTISTS_API)))
					.setTagsApi(cursor.getString(cursor.getColumnIndex(KEY_TAGS_API)));
				
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
		
		//Cursor cursor = db.rawQuery("SELECT * FROM ?", new String[]{ TABLE_SITES });
		Cursor cursor = db.query(TABLE_SITES, new String[]{ KEY_NAME }, null, null, null, null, null);
		
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	
	@Override
	public void update(Site record)
	{
	}
	
	@Override
	public void delete(Site record)
	{
	}
}
