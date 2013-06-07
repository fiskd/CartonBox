package org.shujito.cartonbox.model.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.Tag.Category;

public class DanbooruJsonTagsParser implements JsonParser<Tag>
{
	private static String TAG_ID = "id";
	private static String TAG_NAME = "name";
	private static String TAG_CATEGORY = "category";
	private static String TAG_POST_COUNT = "post_count";
	
	private JSONArray jsonarray = null;
	
	public DanbooruJsonTagsParser(String s) throws JSONException
	{
		this.jsonarray = new JSONArray(s);
	}
	
	@Override
	public Tag getAtIndex(int index)
	{
		JSONObject jobj = this.jsonarray.optJSONObject(index);
		if(jobj == null) return null;
		
		Tag t = new Tag()
			.setId(jobj.optInt(TAG_ID))
			.setName(jobj.optString(TAG_NAME))
			.setCount(jobj.optInt(TAG_POST_COUNT))
			.setCategory(Category.fromInt(jobj.optInt(TAG_CATEGORY)));
		
		return t;
	}
}
