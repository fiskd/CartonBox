package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.JsonParser;
import org.shujito.cartonbox.model.JsonPostParser;

public class DanbooruJsonPostDownloader extends JsonDownloader
{
	public DanbooruJsonPostDownloader(String url)
	{
		super(url);
	}
	
	@Override
	protected JsonParser<?> makeParser(String s) throws Exception
	{
		return new JsonPostParser(s);
	}
}
