package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.DanbooruOldJsonPostParser;
import org.shujito.cartonbox.model.JsonParser;

public class DanbooruOldJsonPostDownloader extends JsonDownloader
{
	public DanbooruOldJsonPostDownloader(String url)
	{
		super(url);
	}
	
	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new DanbooruOldJsonPostParser(s);
	}
}