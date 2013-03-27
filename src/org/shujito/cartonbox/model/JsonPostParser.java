package org.shujito.cartonbox.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonPostParser implements JsonParser<Post>
{
	JSONArray jsonarray = null;

	static String TAG_ID = "id";
	static String TAG_PARENT_ID = "parent_id";
	static String TAG_MD5 = "md5";

	static String TAG_HAS_LARGE = "has_large";
	static String TAG_IMAGE_WIDTH = "image_width";
	static String TAG_IMAGE_HEIGHT = "image_height";

	static String TAG_RATING = "rating";

	static String TAG_HAS_CHILDREN = "has_children";
	static String TAG_LAST_COMMENTED_AT = "last_commented_at";
	static String TAG_LAST_NOTED_AT = "last_noted_at";

	static String TAG_IS_DELETED = "is_deleted";
	static String TAG_IS_FLAGGED = "is_flagged";
	static String TAG_IS_PENDING = "is_pending";

	static String TAG_FILE_EXT = "file_ext";
	static String TAG_FILE_SIZE = "file_size";
	
	public JsonPostParser(String s) throws JSONException
	{
		this.jsonarray = new JSONArray(s);
	}
	
	@Override
	public Post getAtIndex(int index)
	{
		JSONObject jobj = this.jsonarray.optJSONObject(index);
		if(jobj == null) return null;
		
		Post p = new Post()
			.setId(jobj.optInt(TAG_ID))
			.setParentId(jobj.optInt(TAG_PARENT_ID))
			.setMd5(jobj.optString(TAG_MD5))
			.setWidth(jobj.optInt(TAG_IMAGE_WIDTH))
			.setHeight(jobj.optInt(TAG_IMAGE_HEIGHT))
			.setDeleted(jobj.optBoolean(TAG_IS_DELETED))
			.setFlagged(jobj.optBoolean(TAG_IS_FLAGGED))
			.setPending(jobj.optBoolean(TAG_IS_PENDING))
			.setLastCommentedAt(jobj.optString(TAG_LAST_COMMENTED_AT))
			.setLastNotedAt(jobj.optString(TAG_LAST_NOTED_AT));
		
		return p;
	}
	
}
