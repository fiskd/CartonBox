package org.shujito.cartonbox.model.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.shujito.cartonbox.model.Response;

public class DanbooruOldJsonResponseParser implements JsonParser<Response>
{
	private static final String TAG_REASON = "reason";
	private static final String TAG_SUCCESS = "success";
	
	private JSONObject jsonobject = null;
	
	public DanbooruOldJsonResponseParser(String s) throws JSONException
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
