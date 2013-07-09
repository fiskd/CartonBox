package org.shujito.cartonbox.model.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.model.BlogEntry;
import org.shujito.cartonbox.model.listeners.CustomEndElement;
import org.shujito.cartonbox.model.listeners.CustomEndElementListener;
import org.shujito.cartonbox.model.listeners.CustomEndTextElement;
import org.shujito.cartonbox.model.listeners.CustomEndTextElementListener;
import org.shujito.cartonbox.model.listeners.CustomStartElement;
import org.shujito.cartonbox.model.listeners.CustomStartElementListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.RootElement;
import android.util.Xml;
import android.util.Xml.Encoding;

public class BlogRssXmlParser implements XmlParser<BlogEntry>,
	CustomStartElementListener, CustomEndElementListener, CustomEndTextElementListener
{
	private static final String ROOT = "rss";
	private static final String CHANNEL = "channel";
	private static final String ITEM = "item";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String LINK = "link";
	private static final String PUBDATE = "pubDate";
	private static final String CATEGORY = "category";
	
	// hold a temp blog entry here
	private BlogEntry entry = null;
	// put blog entries here
	private List<BlogEntry> entries = null;
	
	public BlogRssXmlParser(InputStream is) throws SAXException, IOException
	{
		RootElement root = new RootElement(ROOT);
		// rss channel
		Element channel = root.getChild(CHANNEL);
		// chanel items
		Element item = channel.getChild(ITEM);
		// item fields
		Element title = item.getChild(TITLE);
		Element description = item.getChild(DESCRIPTION);
		Element link = item.getChild(LINK);
		Element pubdate = item.getChild(PUBDATE);
		// item categories
		Element category = item.getChild(CATEGORY);
		
		// it's cleaner!
		root.setStartElementListener(new CustomStartElement(ROOT, this));
		channel.setStartElementListener(new CustomStartElement(CHANNEL, this));
		item.setStartElementListener(new CustomStartElement(ITEM, this));
		item.setEndElementListener(new CustomEndElement(ITEM, this));
		title.setEndTextElementListener(new CustomEndTextElement(TITLE, this));
		description.setEndTextElementListener(new CustomEndTextElement(DESCRIPTION, this));
		link.setEndTextElementListener(new CustomEndTextElement(LINK, this));
		pubdate.setEndTextElementListener(new CustomEndTextElement(PUBDATE, this));
		category.setEndTextElementListener(new CustomEndTextElement(CATEGORY, this));
		
		Xml.parse(is, Encoding.UTF_8, root.getContentHandler());
		entry = null;
	}
	
	@Override
	public List<BlogEntry> get()
	{
		return this.entries;
	}
	
	@Override
	public void start(Attributes attrs, String elementTag)
	{
		if(elementTag.equals(ROOT))
		{
			// what, xml starts here
		}
		if(elementTag.equals(CHANNEL))
		{
			// create array list of entries here
			this.entries = new ArrayList<BlogEntry>();
		}
		if(elementTag.equals(ITEM))
		{
			// create a temporal entry to work with
			entry = new BlogEntry();
		}
	}
	
	@Override
	public void end(String elementTag)
	{
		if(elementTag.equals(ITEM))
		{
			// add the build entry to the entries list
			entries.add(entry);
		}
	}
	
	@Override
	public void end(String elementTag, String body)
	{
		if(elementTag.equals(TITLE))
		{
			entry.setTitle(body);
		}
		if(elementTag.equals(DESCRIPTION))
		{
			entry.setContent(body);
		}
		if(elementTag.equals(LINK))
		{
			entry.setLink(body);
		}
		if(elementTag.equals(PUBDATE))
		{
			entry.setDate(body);
		}
		if(elementTag.equals(CATEGORY))
		{
			entry.getCategories().add(body);
		}
	}
}
