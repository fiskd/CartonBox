package org.shujito.cartonbox.model.listeners;

import org.xml.sax.Attributes;

import android.sax.StartElementListener;

public class CustomStartElement implements StartElementListener
{
	CustomStartElementListener csel = null;
	String elementTag = null;
	
	public CustomStartElement(String elementTag, CustomStartElementListener csel)
	{
		this.elementTag = elementTag;
		this.csel = csel;
	}
	
	@Override
	public void start(Attributes attributes)
	{
		this.csel.start(attributes, this.elementTag);
	}
}