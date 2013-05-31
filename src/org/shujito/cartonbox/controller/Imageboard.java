package org.shujito.cartonbox.controller;

import java.security.MessageDigest;

import org.shujito.cartonbox.model.Site;

public abstract class Imageboard
{
	/* Static */
	
	public static final String API_KEY = "api_key=%s";
	public static final String API_LIMIT = "limit=%d";
	public static final String API_PAGE = "page=%d";
	public static final String API_TAGS = "tags=%s";
	public static final String API_LOGIN = "login=%s";
	public static final String API_PASSWORD_HASH = "password_hash=%s";
	public static final String API_DANBOORU_PASSWORD = "choujin-steiner--%s--";
	
	/* Fields */
	
	String username = null;
	String password = null;
	Site site = null;
	
	/* Constructor */
	
	public Imageboard(Site site)
	{
		this.site = site;
	}
	
	/* Getters */
	
	public String getUsername()
	{
		return this.username;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public Site getSite()
	{
		return this.site;
	}
	
	/* Setters */
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setPassword(String password)
	{
		MessageDigest msgdig = null;
		
		try
		{ msgdig = MessageDigest.getInstance("SHA-1"); }
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		byte[] buff = String.format(API_DANBOORU_PASSWORD, password).getBytes();
		buff = msgdig.digest(buff);
		
		StringBuffer sbuff = new StringBuffer();
		for(int idx = 0; idx < buff.length; idx++)
		{
			String part = Integer.toHexString(0xff & buff[idx]);
			if(part.length() == 1)
				sbuff.append("0");
			sbuff.append(part);
		}
		this.password = sbuff.toString();
		
		//this.password = password;
	}
	
	public void setPasswordHash(String passwordHash)
	{
		this.password = passwordHash;
	}
	
	public void setSite(Site site)
	{
		this.site = site;
	}
	
	/* meth */
	protected abstract Downloader<?> createDownloader();
	public abstract void clear();
	public abstract void request();
}
