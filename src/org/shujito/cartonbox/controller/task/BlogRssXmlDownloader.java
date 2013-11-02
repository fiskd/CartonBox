package org.shujito.cartonbox.controller.task;

import java.io.InputStream;

import org.shujito.cartonbox.model.parser.BlogRssXmlParser;
import org.shujito.cartonbox.model.parser.XmlParser;

public class BlogRssXmlDownloader extends XmlDownloader
{
	static final String BLOG_ADDRESS = "http://www.shujito.org/tagged/cartonbox/rss";
	
	public BlogRssXmlDownloader()
	{
		super(BLOG_ADDRESS);
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
