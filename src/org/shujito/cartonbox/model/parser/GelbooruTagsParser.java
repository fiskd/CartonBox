package org.shujito.cartonbox.model.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.Tag.Category;
import org.shujito.cartonbox.model.listener.CustomEndElement;
import org.shujito.cartonbox.model.listener.CustomEndElementListener;
import org.shujito.cartonbox.model.listener.CustomStartElement;
import org.shujito.cartonbox.model.listener.CustomStartElementListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.RootElement;
import android.util.Xml;
import android.util.Xml.Encoding;

public class GelbooruTagsParser implements XmlParser<Tag>,
	CustomStartElementListener, CustomEndElementListener
{
	private static final String TAGS = "tags";
	private static final String TAG = "tag";
	private static final String TYPE = "type";
	private static final String COUNT = "count";
	private static final String NAME = "name";
	//private static final String AMBIGUOUS = "ambiguous";
	private static final String ID = "id";
	
	// hold a temp tag here
	private Tag tempTag = null;
	private List<Tag> tagsList = null;

	public GelbooruTagsParser(InputStream is) throws SAXException, IOException
	{
		// root of the xml file
		RootElement tags = new RootElement(TAGS);
		// tag items
		Element tag = tags.getChild(TAG);
		
		tags.setStartElementListener(new CustomStartElement(TAGS, this));
		tag.setStartElementListener(new CustomStartElement(TAG, this));
		tag.setEndElementListener(new CustomEndElement(TAG, this));
		
		Xml.parse(is, Encoding.UTF_8, tags.getContentHandler());
	}
	
	@Override
	public List<Tag> get()
	{
		return this.tagsList;
	}
	
	@Override
	public void start(Attributes attrs, String elementTag)
	{
		if(elementTag.equals(TAGS))
		{
			// root started, create list
			this.tagsList = new ArrayList<Tag>();
		}
		if(elementTag.equals(TAG))
		{
			// parse tag here
			String type = attrs.getValue(TYPE);
			String count = attrs.getValue(COUNT);
			String name = attrs.getValue(NAME);
			//String ambiguous = attrs.getValue(AMBIGUOUS);
			String id = attrs.getValue(ID);
			
			this.tempTag = new Tag()
				.setId(Integer.parseInt(id))
				.setCount(Integer.parseInt(count))
				.setName(name)
				.setCategory(Category.fromInt(Integer.parseInt(type)));
			
			// done
		}
	}
	
	@Override
	public void end(String elementTag)
	{
		if(elementTag.equals(TAG))
		{
			// tag item, it is closing, add the tag to the list
			this.tagsList.add(this.tempTag);
		}
	}
}
