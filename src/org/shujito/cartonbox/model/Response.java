package org.shujito.cartonbox.model;

public class Response
{
	String reason;
	boolean success;
	
	/* constructor */
	public Response()
	{
		this.reason = null;
		this.success = false;
	}
	
	/* getters */
	public String getReason()
	{ return reason; }
	public boolean isSuccess()
	{ return success; }
	
	/* setters */
	public Response setReason(String s)
	{
		this.reason = s;
		return this;
	}
	public Response setSuccess(boolean b)
	{
		this.success = b;
		return this;
	}
}
