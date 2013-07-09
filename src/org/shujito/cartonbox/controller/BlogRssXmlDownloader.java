package org.shujito.cartonbox.controller;

import java.io.InputStream;

import org.shujito.cartonbox.model.parser.BlogRssXmlParser;
import org.shujito.cartonbox.model.parser.XmlParser;

public class BlogRssXmlDownloader extends XmlDownloader
{
	public BlogRssXmlDownloader(String url)
	{
		super(url);
	}
	
	@Override
	public XmlParser<?> parse(InputStream is) throws Exception
	{
		return new BlogRssXmlParser(is);
	}

	@Override
	public XmlParser<?> parseError(InputStream is) throws Exception
	{
		return null;
	}
}
