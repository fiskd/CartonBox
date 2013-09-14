package org.shujito.cartonbox.model.db;

import java.util.List;

import org.shujito.cartonbox.model.Download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DownloadsDB extends DB<Download>
{
	static final String DB_NAME = "downloads.db3";
	static final int VERSION = 0x00000001;
	
	static final String KEY_ID = "id";
	
	public DownloadsDB(Context context)
	{
		super(context, DB_NAME, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}
	
	@Override
	public boolean add(Download record)
	{
		return false;
	}
	
	@Override
	public Download get(int id)
	{
		return null;
	}
	
	@Override
	public List<Download> getAll()
	{
		return null;
	}
	
	@Override
	public int getCount()
	{
		return 0;
	}
	
	@Override
	public boolean update(Download record)
	{
		return false;
	}
	
	@Override
	public boolean delete(Download record)
	{
		return false;
	}
}
