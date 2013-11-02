package org.shujito.cartonbox.controller.task;

import org.shujito.cartonbox.model.parser.DanbooruOldJsonResponseParser;
import org.shujito.cartonbox.model.parser.DanbooruOldJsonTagsParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public class DanbooruOldJsonTagsDownloader extends JsonDownloader
{
	public DanbooruOldJsonTagsDownloader(String url)
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
