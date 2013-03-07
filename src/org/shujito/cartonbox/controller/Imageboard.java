package org.shujito.cartonbox.controller;

public abstract class Imageboard
{
	/* fields */
	protected String username;
	protected String password;
	protected String passwordHash;
	
	public Imageboard()
	{
		
	}
	
	//protected abstract void 
	
	public abstract void requestPosts(int page);
}
