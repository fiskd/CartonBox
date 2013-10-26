package org.shujito.cartonbox.controller.task;

import org.shujito.cartonbox.model.parser.JsonParser;
import org.shujito.cartonbox.model.parser.SitesJsonParser;

public class SitesJsonDownloader extends JsonDownloader
{
	static final String SITES_ADDRESS = "http://www.shujito.org/cartonbox/boorus/";
	
	public SitesJsonDownloader()
	{
		super(SITES_ADDRESS);
	}
	
	@Override
	protected JsonParser<?> parse(String s) throws Exception
	{
		return new SitesJsonParser(s);
	}
	
	@Override
	protected JsonParser<?> parseError(String s) throws Exception
	{
		return null;
	}
}
