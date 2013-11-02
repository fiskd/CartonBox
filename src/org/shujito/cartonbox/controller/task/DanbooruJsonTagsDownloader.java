package org.shujito.cartonbox.controller.task;

import org.shujito.cartonbox.model.parser.DanbooruJsonResponseParser;
import org.shujito.cartonbox.model.parser.DanbooruJsonTagsParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public class DanbooruJsonTagsDownloader extends JsonDownloader
{
	public DanbooruJsonTagsDownloader(String url)
	{
		super(url);
	}

	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new DanbooruJsonTagsParser(s);
	}
	
	@Override
	protected JsonParser<?> parseError(String s) throws Exception
	{
		return new DanbooruJsonResponseParser(s);
	}
}
