package org.shujito.cartonbox.controller.task;

import java.io.InputStream;

import org.shujito.cartonbox.model.parser.GelbooruTagsParser;
import org.shujito.cartonbox.model.parser.XmlParser;

public class GelbooruTagsDownloader extends XmlDownloader
{
	public GelbooruTagsDownloader(String url)
	{
		super(url);
	}
	
	@Override
	public XmlParser<?> parse(InputStream is) throws Exception
	{
		return new GelbooruTagsParser(is);
	}
	
	@Override
	public XmlParser<?> parseError(InputStream is) throws Exception
	{
		return null;
	}
}
