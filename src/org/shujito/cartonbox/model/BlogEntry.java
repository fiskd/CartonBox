package org.shujito.cartonbox.model;

import java.util.ArrayList;
import java.util.List;

public class BlogEntry
{
	private String title;
	private String content;
	private String date;
	private String link;
	private List<String> categories;
	
	public BlogEntry()
	{
		this.title = null;
		this.content = null;
		this.date = null;
		this.link = null;
		this.categories = new ArrayList<String>();
	}
	
	public String getTitle()
	{ return this.title; }
	public String getContent()
	{ return this.content; }
	public String getDate()
	{ return this.date; }
	public String getLink()
	{ return this.link; }
	public List<String> getCategories()
	{ return this.categories; }
	
	public BlogEntry setTitle(String title)
	{
		this.title = title;
		return this;
	}
	public BlogEntry setContent(String content)
	{
		this.content = content;
		return this;
	}
	public BlogEntry setDate(String date)
	{
		this.date = date;
		return this;
	}
	public BlogEntry setLink(String link)
	{
		this.link = link;
		return this;
	}
	public BlogEntry setCategories(List<String> categories)
	{
		this.categories = categories;
		return this;
	}
}