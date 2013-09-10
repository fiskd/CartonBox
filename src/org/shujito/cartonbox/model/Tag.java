package org.shujito.cartonbox.model;

import java.io.Serializable;

public class Tag implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public enum Category
	{
		General(0),
		Artist(1),
		Reserved(2),
		Copyright(3),
		Character(4),
		Other1(5),
		Other2(6),
		;
		
		private int value;
		
		Category(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{ return this.value; }
		public void setValue(int value)
		{ this.value = value; }
		
		public static Category fromInt(int value)
		{
			switch(value)
			{
				case 0: return General;
				case 1: return Artist;
				case 2: return Reserved;
				case 3: return Copyright;
				case 4: return Character;
				case 5: return Other1;
				case 6: return Other2;
				default: return null;
			}
		}
		
	}
	
	int id;
	int count;
	String name;
	// this also means type
	Category category;
	
	public int getId()
	{ return this.id; }
	public int getCount()
	{ return this.count; }
	public String getName()
	{ return this.name; }
	public Category getCategory()
	{ return this.category; }
	
	public Tag setId(int id)
	{
		this.id = id;
		return this;
	}
	public Tag setCount(int count)
	{
		this.count = count;
		return this;
	}
	public Tag setName(String name)
	{
		this.name = name;
		return this;
	}
	public Tag setCategory(Category category)
	{
		this.category = category;
		return this;
	}
}
