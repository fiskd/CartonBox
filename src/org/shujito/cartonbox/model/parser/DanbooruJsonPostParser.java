package org.shujito.cartonbox.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;
import org.shujito.cartonbox.model.Tag;

public class DanbooruJsonPostParser implements JsonParser<Post>
{
	private static final String TAG_ID = "id";
	private static final String TAG_PARENT_ID = "parent_id";
	private static final String TAG_MD5 = "md5";
	
	private static final String TAG_HAS_LARGE = "has_large";
	private static final String TAG_IMAGE_WIDTH = "image_width";
	private static final String TAG_IMAGE_HEIGHT = "image_height";
	
	private static final String TAG_RATING = "rating";
	
	private static final String TAG_HAS_CHILDREN = "has_children";
	private static final String TAG_LAST_COMMENTED_AT = "last_commented_at";
	private static final String TAG_LAST_NOTED_AT = "last_noted_at";
	
	private static final String TAG_IS_DELETED = "is_deleted";
	private static final String TAG_IS_FLAGGED = "is_flagged";
	private static final String TAG_IS_PENDING = "is_pending";
	
	private static final String TAG_FILE_EXT = "file_ext";
	private static final String TAG_FILE_SIZE = "file_size";
	private static final String TAG_TAG_STRING = "tag_string";
	
	private static final String TAG_TAG_ARTIST = "tag_string_artist";
	private static final String TAG_TAG_CHARACTER = "tag_string_character";
	private static final String TAG_TAG_COPYRIGHT = "tag_string_copyright";
	private static final String TAG_TAG_GENERAL = "tag_string_general";

	private static final String TAG_FILE_URL = "file_url";
	private static final String TAG_LARGE_FILE_URL = "large_file_url";
	private static final String TAG_PREVIEW_FILE_URL = "preview_file_url";
	
	private JSONArray jsonarray = null;
	
	public DanbooruJsonPostParser(String s) throws JSONException
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
			.setWidth(jobj.optInt(TAG_IMAGE_WIDTH))
			.setHeight(jobj.optInt(TAG_IMAGE_HEIGHT))
			.setHasChildren(jobj.optBoolean(TAG_HAS_CHILDREN))
			.setHasLarge(jobj.optBoolean(TAG_HAS_LARGE))
			.setLastCommentedAt(jobj.optString(TAG_LAST_COMMENTED_AT))
			.setLastNotedAt(jobj.optString(TAG_LAST_NOTED_AT))
			.setDeleted(jobj.optBoolean(TAG_IS_DELETED))
			.setFlagged(jobj.optBoolean(TAG_IS_FLAGGED))
			.setPending(jobj.optBoolean(TAG_IS_PENDING))
			.setFileExt(jobj.optString(TAG_FILE_EXT))
			.setFileSize(jobj.optInt(TAG_FILE_SIZE))
			.setTagString(jobj.optString(TAG_TAG_STRING))
			.setUrl(jobj.optString(TAG_FILE_URL))
			.setSampleUrl(jobj.optString(TAG_LARGE_FILE_URL))
			.setPreviewUrl(jobj.optString(TAG_PREVIEW_FILE_URL));
		
		// parse tags
		//String tagString = jobj.optString(TAG_TAG_STRING);
		String[] tagArtist = jobj.optString(TAG_TAG_ARTIST).split("\\s+");
		String[] tagCharacter = jobj.optString(TAG_TAG_CHARACTER).split("\\s+");
		String[] tagCopyright = jobj.optString(TAG_TAG_COPYRIGHT).split("\\s+");
		String[] tagGeneral = jobj.optString(TAG_TAG_GENERAL).split("\\s+");
		
		List<Tag> tags = new ArrayList<Tag>();
		for(String tag : tagArtist)
		{
			tags.add(new Tag()
				.setCategory(Tag.Category.Artist)
				.setName(tag));
		}
		for(String tag : tagCharacter)
		{
			tags.add(new Tag()
				.setCategory(Tag.Category.Character)
				.setName(tag));
		}
		for(String tag : tagCopyright)
		{
			tags.add(new Tag()
				.setCategory(Tag.Category.Copyright)
				.setName(tag));
		}
		for(String tag : tagGeneral)
		{
			tags.add(new Tag()
				.setCategory(Tag.Category.General)
				.setName(tag));
		}
		
		p.setTags(tags);
		
		// evaluate ratings
		String rating = jobj.optString(TAG_RATING);
		if(rating.equals("e"))
			p.setRating(Rating.Explicit);
		else if(rating.equals("q"))
			p.setRating(Rating.Questionable);
		else if(rating.equals("s"))
			p.setRating(Rating.Safe);
		
		return p;
	}
	
}
