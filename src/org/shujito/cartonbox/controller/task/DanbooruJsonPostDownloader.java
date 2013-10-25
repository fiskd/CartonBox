package org.shujito.cartonbox.controller.task;

import org.shujito.cartonbox.model.parser.DanbooruJsonPostParser;
import org.shujito.cartonbox.model.parser.DanbooruJsonResponseParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public class DanbooruJsonPostDownloader extends JsonDownloader
{
	public DanbooruJsonPostDownloader(String url)
	{
		super(url);
	}
	
	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new DanbooruJsonPostParser(s);
	}
	
	@Override
	protected JsonParser<?> parseError(String s) throws Exception
	{
		return new DanbooruJsonResponseParser(s);
	}
}
