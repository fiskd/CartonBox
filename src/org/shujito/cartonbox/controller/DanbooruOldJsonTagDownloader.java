package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.parser.DanbooruOldJsonResponseParser;
import org.shujito.cartonbox.model.parser.DanbooruOldJsonTagsParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public class DanbooruOldJsonTagDownloader extends JsonDownloader
{
	public DanbooruOldJsonTagDownloader(String url)
	{
		super(url);
	}

	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new DanbooruOldJsonTagsParser(s);
	}
	
	@Override
	protected JsonParser<?> parseError(String s) throws Exception
	{
		return new DanbooruOldJsonResponseParser(s);
	}
}
