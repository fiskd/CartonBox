package org.shujito.cartonbox.model.listeners;

import android.sax.EndTextElementListener;

public class CustomEndTextElement implements EndTextElementListener
{
	CustomEndTextElementListener cetel = null;
	String elementTag = null;
	
	public CustomEndTextElement(String elementTag, CustomEndTextElementListener cetel)
	{
		this.elementTag = elementTag;
		this.cetel = cetel;
	}
	
	@Override
	public void end(String body)
	{
		this.cetel.end(this.elementTag, body);
	}

}
