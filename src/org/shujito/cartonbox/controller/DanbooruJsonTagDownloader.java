package org.shujito.cartonbox.controller;

import org.shujito.cartonbox.model.parser.DanbooruJsonTagsParser;
import org.shujito.cartonbox.model.parser.JsonParser;

public class DanbooruJsonTagDownloader extends JsonDownloader
{
	public DanbooruJsonTagDownloader(String url)
	{
		super(url);
	}

	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new DanbooruJsonTagsParser(s);
	}
}
