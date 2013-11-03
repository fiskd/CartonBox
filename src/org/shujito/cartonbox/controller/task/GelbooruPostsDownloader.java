package org.shujito.cartonbox.controller.task;

import java.io.InputStream;

import org.shujito.cartonbox.model.parser.GelbooruPostsParser;
import org.shujito.cartonbox.model.parser.XmlParser;

public class GelbooruPostsDownloader extends XmlDownloader
{
	public GelbooruPostsDownloader(String url)
	{
		super(url);
	}
	
	@Override
	public XmlParser<?> parse(InputStream is) throws Exception
	{
		return new GelbooruPostsParser(is);
	}
	
	@Override
	public XmlParser<?> parseError(InputStream is) throws Exception
	{
		return null;
	}
}
