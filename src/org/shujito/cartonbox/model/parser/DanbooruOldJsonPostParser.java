package org.shujito.cartonbox.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.Tag.Category;


public class DanbooruOldJsonPostParser implements JsonParser<Post>
{
	// this helps a lot
	// http://www.androidhive.info/2012/01/android-json-parsing-tutorial/

	// post json tags
	private static final String TAG_ID = "id";
	private static final String TAG_PARENT_ID = "parent_id";
	private static final String TAG_MD5 = "md5";

	//private static final String TAG_PREVIEW_WIDTH = "preview_width";
	//private static final String TAG_PREVIEW_HEIGHT = "preview_height";
	private static final String TAG_PREVIEW_FILE_URL = "preview_url";

	//private static final String TAG_SAMPLE_WIDTH = "sample_width";
	//private static final String TAG_SAMPLE_HEIGHT = "sample_height";
	private static final String TAG_SAMPLE_FILE_URL = "sample_url";

	private static final String TAG_WIDTH = "width";
	private static final String TAG_HEIGHT = "height";
	private static final String TAG_FILE_URL = "file_url";

	private static final String TAG_RATING = "rating";
	private static final String TAG_STATUS = "status";

	private static final String TAG_HAS_CHILDREN = "has_children";
	private static final String TAG_HAS_COMMENTS = "has_comments";
	private static final String TAG_HAS_NOTES = "has_notes";
	
	private static final String TAG_TAGS = "tags";
	
	JSONArray jsonarray = null;
	
	public DanbooruOldJsonPostParser(String s) throws JSONException
	{
		this.jsonarray = new JSONArray(s);
	}
	
	@Override
	public Post getAtIndex(int index)
	{
		JSONObject jobj = this.jsonarray.optJSONObject(index);
		if(jobj == null) return null;
		
		// direct fields
		Post p = new Post()
			.setId(jobj.optInt(TAG_ID))
			.setParentId(jobj.optInt(TAG_PARENT_ID))
			.setMd5(jobj.optString(TAG_MD5))
			.setWidth(jobj.optInt(TAG_WIDTH))
			.setHeight(jobj.optInt(TAG_HEIGHT))
			.setSampleUrl(jobj.optString(TAG_SAMPLE_FILE_URL))
			.setPreviewUrl(jobj.optString(TAG_PREVIEW_FILE_URL))
			.setUrl(jobj.optString(TAG_FILE_URL))
			.setHasChildren(jobj.optBoolean(TAG_HAS_CHILDREN))
			.setLastCommentedAt(jobj.optBoolean(TAG_HAS_COMMENTS) ? "?" : null)
			.setLastNotedAt(jobj.optBoolean(TAG_HAS_NOTES) ? "?" : null)
			.setTagString(jobj.optString(TAG_TAGS));
		
		// parse tags (don't parse tags)
		String[] tagString = p.getTagString().split("\\s+");
		
		List<Tag> tags = new ArrayList<Tag>();
		// can't categorize these (should be possible but that would
		// involve querying tags or storing them into a database
		// and then etc...)
		for(String tag : tagString)
		{
			tags.add(new Tag()
				.setCategory(Category.General)
				.setName(tag));
		}
		
		p.setTags(tags);
		
		// evaluate rating
		String rating = jobj.optString(TAG_RATING);
		if(rating.equals("e"))
			p.setRating(Rating.Explicit);
		else if(rating.equals("q"))
			p.setRating(Rating.Questionable);
		else if(rating.equals("s"))
			p.setRating(Rating.Safe);
		
		// evaluate status
		String status = jobj.optString(TAG_STATUS);
		p.setDeleted(status.equals("deleted"));
		p.setFlagged(status.equals("flagged"));
		p.setPending(status.equals("pending"));
		
		return p;
	}
}
