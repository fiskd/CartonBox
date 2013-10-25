package org.shujito.cartonbox.controller.task;

import org.shujito.cartonbox.model.parser.DanbooruOldJsonPostParser;
import org.shujito.cartonbox.model.parser.DanbooruOldJsonResponseParser;
import org.shujito.cartonbox.model.parser.JsonParser;

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
	
	@Override
	protected JsonParser<?> parseError(String s) throws Exception
	{
		return new DanbooruOldJsonResponseParser(s);
	}
}
