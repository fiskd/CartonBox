package org.shujito.cartonbox.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DanbooruJsonResponseParser implements JsonParser<Response>
{
	private static String TAG_REASON = "reason";
	private static String TAG_SUCCESS = "success";
	
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
			.setReason(this.jsonobject.optString(TAG_REASON))
			.setSuccess(this.jsonobject.optBoolean(TAG_SUCCESS));
		
		return r;
	}
}
