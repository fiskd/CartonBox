package org.shujito.cartonbox.model.parser;

import java.io.InputStream;
import java.util.List;

import org.shujito.cartonbox.model.BlogEntry;

public class BlogRssXmlParser implements XmlParser<BlogEntry>
{
	public BlogRssXmlParser(InputStream is)
	{
		
	}
	
	@Override
	public List<BlogEntry> get()
	{
		return null;
	}
}
