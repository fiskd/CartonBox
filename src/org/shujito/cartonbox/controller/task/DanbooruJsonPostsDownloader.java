package org.shujito.cartonbox.controller.task;

import org.shujito.cartonbox.model.parser.DanbooruJsonPostsParser;
import org.shujito.cartonbox.model.parser.DanbooruJsonResponseParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public class DanbooruJsonPostsDownloader extends JsonDownloader
{
	public DanbooruJsonPostsDownloader(String url)
	{
		super(url);
	}
	
	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new DanbooruJsonPostsParser(s);
	}
	
	@Override
	protected JsonParser<?> parseError(String s) throws Exception
	{
		return new DanbooruJsonResponseParser(s);
	}
}
