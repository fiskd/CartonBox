package org.shujito.cartonbox.model;

public class Tag
{
	public enum Category
	{
		General(0),
		Artist(1),
		Reserved3(2),
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
	}
	
	int id;
	int count;
	String name;
	Category category; // legacy: type
	
	public int getId()
	{ return this.id; }
	public int getCount()
	{ return this.count; }
	public String getName()
	{ return this.name; }
	
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
}
