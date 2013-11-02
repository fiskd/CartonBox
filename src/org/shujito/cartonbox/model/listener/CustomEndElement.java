package org.shujito.cartonbox.model.listener;

import android.sax.EndElementListener;

public class CustomEndElement implements EndElementListener
{
	CustomEndElementListener ceel = null;
	String elementTag = null;
	
	public CustomEndElement(String elementTag, CustomEndElementListener ceel)
	{
		this.elementTag = elementTag;
		this.ceel = ceel;
	}
	
	@Override
	public void end()
	{
		this.ceel.end(this.elementTag);
	}
}
