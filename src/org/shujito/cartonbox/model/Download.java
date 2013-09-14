package org.shujito.cartonbox.model;

import java.io.Serializable;

public class Download implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String source;
	private String location;
	
	public long getId()
	{ return this.id; }
	public String getName()
	{ return this.name; }
	public String getSource()
	{ return this.source; }
	public String getLocation()
	{ return this.location; }
	
	public Download setId(long i)
	{
		this.id = i;
		return this;
	}
	public Download setName(String s)
	{
		this.name = s;
		return this;
	}
	public Download setSource(String s)
	{
		this.source = s;
		return this;
	}
	public Download setLocation(String s)
	{
		this.location = s;
		return this;
	}
}
