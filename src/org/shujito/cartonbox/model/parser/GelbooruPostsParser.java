package org.shujito.cartonbox.model.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.Post.Rating;
import org.shujito.cartonbox.model.Tag;
import org.shujito.cartonbox.model.Tag.Category;
import org.shujito.cartonbox.model.listener.CustomEndElement;
import org.shujito.cartonbox.model.listener.CustomEndElementListener;
import org.shujito.cartonbox.model.listener.CustomStartElement;
import org.shujito.cartonbox.model.listener.CustomStartElementListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.RootElement;
import android.util.Xml;
import android.util.Xml.Encoding;

public class GelbooruPostsParser implements XmlParser<Post>,
	CustomStartElementListener, CustomEndElementListener
{
	private static final String POSTS = "posts";
	private static final String POST = "post";
	private static final String HEIGHT = "height";
	//private static final String SCORE = "score";
	private static final String FILE_URL = "file_url";
	private static final String PARENT_ID = "parent_id";
	private static final String SAMPLE_URL = "sample_url";
	//private static final String SAMPLE_WIDTH = "sample_width";
	//private static final String SAMPLE_HEIGHT = "sample_height";
	private static final String PREVIEW_URL = "preview_url";
	private static final String RATING = "rating";
	private static final String TAGS = "tags";
	private static final String ID = "id";
	private static final String WIDTH = "width";
	//private static final String CHANGE = "change";
	private static final String MD5 = "md5";
	//private static final String CREATOR_ID = "creator_id";
	private static final String HAS_CHILDREN = "has_children";
	//private static final String CREATED_AT = "created_at";
	private static final String STATUS = "status";
	//private static final String SOURCE = "source";
	private static final String HAS_NOTES = "has_notes";
	private static final String HAS_COMMENTS = "has_comments";
	//private static final String PREVIEW_WIDTH = "preview_width";
	//private static final String PREVIEW_HEIGHT = "preview_height";
	
	// hold a temp post here
	private Post tempPost = null;
	private List<Post> postsList = null;
	
	public GelbooruPostsParser(InputStream is) throws SAXException, IOException
	{
		// root of the xml file
		RootElement posts = new RootElement(POSTS);
		// post items
		Element post = posts.getChild(POST);
		
		posts.setStartElementListener(new CustomStartElement(POSTS, this));
		post.setStartElementListener(new CustomStartElement(POST, this));
		post.setEndElementListener(new CustomEndElement(POST, this));
		
		Xml.parse(is, Encoding.UTF_8, posts.getContentHandler());
	}
	
	@Override
	public List<Post> get()
	{
		return this.postsList;
	}
	
	@Override
	public void start(Attributes attrs, String elementTag)
	{
		if(elementTag.equals(POSTS))
		{
			// xml starts here
			this.postsList = new ArrayList<Post>();
		}
		if(elementTag.equals(POST))
		{
			// it is a post item
			// gather properties
			String height = attrs.getValue(HEIGHT);
			//String score = attrs.getValue(SCORE);
			String fileUrl = attrs.getValue(FILE_URL);
			String parentId = attrs.getValue(PARENT_ID);
			String sampleUrl = attrs.getValue(SAMPLE_URL);
			//String sampleWidth = attrs.getValue(SAMPLE_WIDTH);
			//String sampleHeight = attrs.getValue(SAMPLE_HEIGHT);
			String previewUrl = attrs.getValue(PREVIEW_URL);
			String rating = attrs.getValue(RATING);
			String tags = attrs.getValue(TAGS);
			String id = attrs.getValue(ID);
			String width = attrs.getValue(WIDTH);
			//String change = attrs.getValue(CHANGE);
			String md5 = attrs.getValue(MD5);
			//String creatorId = attrs.getValue(CREATOR_ID);
			String hasChildren = attrs.getValue(HAS_CHILDREN);
			//String createdAt = attrs.getValue(CREATED_AT);
			String status = attrs.getValue(STATUS);
			//String source = attrs.getValue(SOURCE);
			String hasNotes = attrs.getValue(HAS_NOTES);
			String hasComments = attrs.getValue(HAS_COMMENTS);
			//String previewWidth = attrs.getValue(PREVIEW_WIDTH);
			//String previewHeight = attrs.getValue(PREVIEW_HEIGHT);
			
			// make one
			this.tempPost = new Post()
				.setId(Integer.parseInt(id))
				.setParentId("".equals(parentId) ? 0 : Integer.parseInt(parentId))
				.setMd5(md5)
				.setWidth(Integer.parseInt(width))
				.setHeight(Integer.parseInt(height))
				.setSampleUrl(sampleUrl)
				.setPreviewUrl(previewUrl)
				.setUrl(fileUrl)
				.setHasChildren(Boolean.parseBoolean(hasChildren))
				.setLastCommentedAt(Boolean.parseBoolean(hasComments) ? "?" : null)
				.setLastNotedAt(Boolean.parseBoolean(hasNotes) ? "?" : null)
				.setTagString(tags);
			
			String[] vsTags = this.tempPost.getTagString().split("\\s+");
			List<Tag> lsTags = new ArrayList<Tag>();
			// like with danbooru 1.18.x, these tags are hard to categorize
			// just add them all as general
			for(String tag : vsTags)
			{
				// split error, ignore there
				if(tag.isEmpty()) continue;
				
				lsTags.add(new Tag()
					.setCategory(Category.General)
					.setName(tag));
			}
			
			this.tempPost.setTags(lsTags);
			
			// evaluate rating
			if(rating.equals("e"))
				this.tempPost.setRating(Rating.Explicit);
			else if(rating.equals("q"))
				this.tempPost.setRating(Rating.Questionable);
			else if(rating.equals("s"))
				this.tempPost.setRating(Rating.Safe);
			
			// evaluate status
			this.tempPost.setDeleted(status.equals("deleted"));
			this.tempPost.setFlagged(status.equals("flagged"));
			this.tempPost.setPending(status.equals("pending"));
			
			// we're done with this item
		}
	}
	
	@Override
	public void end(String elementTag)
	{
		if(elementTag.equals(POST))
		{
			// it is a post item, it is closing, add it to the array
			this.postsList.add(this.tempPost);
		}
	}
}
