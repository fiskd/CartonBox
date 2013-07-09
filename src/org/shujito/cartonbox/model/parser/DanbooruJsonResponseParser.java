package org.shujito.cartonbox.model.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.shujito.cartonbox.model.Response;

public class DanbooruJsonResponseParser implements JsonParser<Response>
{
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_SUCCESS = "success";
	
	private JSONObject jsonobject = null;
	
	public DanbooruJsonResponseParser(String s) throws JSONException
	{
		this.jsonobject = new JSONObject(s);
	}
	
	@Override
	public Response getAtIndex(int index)
	{
		if(this.jsonobject == null)
			return null;
		
		Response r = new Response()
			.setReason(this.jsonobject.optString(TAG_MESSAGE))
			.setSuccess(this.jsonobject.optBoolean(TAG_SUCCESS));
		
		return r;
	}
}
