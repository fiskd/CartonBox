package org.shujito.cartonbox.controller;

import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.controller.listeners.OnAccessDeniedListener;
import org.shujito.cartonbox.controller.listeners.OnErrorListener;
import org.shujito.cartonbox.controller.listeners.OnInternalServerErrorListener;
import org.shujito.cartonbox.controller.listeners.OnRequestListener;
import org.shujito.cartonbox.model.Response;
import org.shujito.cartonbox.model.Site;
import org.shujito.cartonbox.model.parser.JsonParser;

public abstract class Imageboard implements
	OnErrorListener,
	OnAccessDeniedListener,
	OnInternalServerErrorListener
{
	/* Static */
	
	public static final String API_KEY = "api_key=%s";
	public static final String API_LIMIT = "limit=%d";
	public static final String API_PAGE = "page=%d";
	public static final String API_TAGS = "tags=%s";
	public static final String API_LOGIN = "login=%s";
	public static final String API_PASSWORD_HASH = "password_hash=%s";
	public static final String API_DANBOORU_PASSWORD = "choujin-steiner--%s--";
	
	/* Listeners */
	List<OnRequestListener> onRequestListeners = null;
	List<OnErrorListener> onErrorListeners = null;

	public void addOnPostsRequestListener(OnRequestListener l)
	{
		if(this.onRequestListeners == null)
			this.onRequestListeners = new ArrayList<OnRequestListener>();
		this.onRequestListeners.add(l);
	}
	public void addOnErrorListener(OnErrorListener l)
	{
		if(this.onErrorListeners == null)
			this.onErrorListeners = new ArrayList<OnErrorListener>();
		this.onErrorListeners.add(l);
	}
	
	public void removeOnPostsRequestListener(OnRequestListener l)
	{
		if(this.onRequestListeners == null)
			this.onRequestListeners = new ArrayList<OnRequestListener>();
		this.onRequestListeners.remove(l);
	}
	public void removeOnErrorListener(OnErrorListener l)
	{
		if(this.onErrorListeners == null)
			this.onErrorListeners = new ArrayList<OnErrorListener>();
		this.onErrorListeners.remove(l);
	}
	
	/* Fields */
	
	protected String username = null;
	protected String password = null;
	protected Site site = null;
	protected Downloader<?> downloader = null;
	protected boolean working = false;
	
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

	@Override
	public void onError(int errCode, String message)
	{
		if(this.onErrorListeners != null)
		{
			for(OnErrorListener l : this.onErrorListeners)
			{
				l.onError(errCode, message);
			}
		}
		
		this.working = false;
	}
	
	@Override
	public void onAccessDenied()
	{
		if(this.onErrorListeners != null)
		{
			for(OnErrorListener l : this.onErrorListeners)
			{
				l.onError(HttpURLConnection.HTTP_FORBIDDEN, "Access denied");
			}
		}
		
		this.working = false;
	}
	
	@Override
	public void onInternalServerError(JsonParser<?> jarr)
	{
		Response response = (Response)jarr.getAtIndex(0);
		
		if(this.onErrorListeners != null)
		{
			for(OnErrorListener l : this.onErrorListeners)
			{
				l.onError(HttpURLConnection.HTTP_INTERNAL_ERROR, response.getReason());
			}
		}
		
		this.working = false;
	}
	
	/* abstract meth */
	protected abstract Downloader<?> createDownloader();
	public abstract void request();
	public abstract void clear();
}
