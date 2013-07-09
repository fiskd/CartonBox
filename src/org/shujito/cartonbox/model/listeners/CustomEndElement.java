package org.shujito.cartonbox.model.listeners;

import android.sax.EndElementListener;

public class CustomEndElement implements EndElementListener
{
	CustomEndElementListener customEndElementListener = null;
	String elementTag = null;
	
	public CustomEndElement(String elementTag, CustomEndElementListener ceel)
	{
		this.elementTag = elementTag;
		this.customEndElementListener = ceel;
	}
	
	@Override
	public void end()
	{
		this.customEndElementListener.end(this.elementTag);
	}
}
