package org.shujito.cartonbox.model.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shujito.cartonbox.model.Site;

public class SitesJsonParser implements JsonParser<Site>
{
	private static String TAG_NAME = "name";
	private static String TAG_ICON_WEB = "icon_web";
	private static String TAG_ICON_FILE = "icon_file";
	private static String TAG_DESCRIPTION = "description";
	private static String TAG_URL = "url";
	private static String TAG_TYPE = "type";
	private static String TAG_POSTS = "posts";
	private static String TAG_POOLS = "pools";
	private static String TAG_COMMENTS = "comments";
	private static String TAG_NOTES = "notes";
	private static String TAG_TAGS = "tags";

	private JSONArray jsonArray = null;
	
	public SitesJsonParser(String s) throws JSONException
	{
		this.jsonArray = new JSONArray(s);
	}
	
	@Override
	public Site getAtIndex(int index)
	{
		JSONObject jobj = this.jsonArray.optJSONObject(index);
		if(jobj == null) return null;
		
		Site s = new Site()
			.setId(System.currentTimeMillis())
			.setName(jobj.optString(TAG_NAME))
			.setIconWeb(jobj.optString(TAG_ICON_WEB))
			.setIconFile(jobj.optString(TAG_ICON_FILE))
			.setUrl(jobj.optString(TAG_URL))
			.setType(Site.Type.fromInt(jobj.optInt(TAG_TYPE)))
			.setPostsApi(jobj.optString(TAG_POSTS))
			.setPoolsApi(jobj.optString(TAG_POOLS))
			.setCommentsApi(jobj.optString(TAG_COMMENTS))
			.setNotesApi(jobj.optString(TAG_NOTES))
			.setTagsApi(jobj.optString(TAG_TAGS))
			.setDescription(jobj.optString(TAG_DESCRIPTION));
		
		return s;
	}
}
