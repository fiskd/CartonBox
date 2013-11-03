package org.shujito.cartonbox.model.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.shujito.cartonbox.model.Post;
import org.shujito.cartonbox.model.listener.CustomEndElement;
import org.shujito.cartonbox.model.listener.CustomEndElementListener;
import org.shujito.cartonbox.model.listener.CustomStartElement;
import org.shujito.cartonbox.model.listener.CustomStartElementListener;
import org.xml.sax.Attributes;

import android.sax.Element;
import android.sax.RootElement;

public class GelbooruPostsParser implements XmlParser<Post>,
	CustomStartElementListener, CustomEndElementListener
{
	private static final String POSTS = "posts";
	private static final String POST = "post";
	private static final String HEIGHT = "height";
	private static final String SCORE = "score";
	private static final String FILE_URL = "file_url";
	private static final String PARENT_ID = "parent_id";
	private static final String SAMPLE_URL = "sample_url";
	private static final String SAMPLE_WIDTH = "sample_width";
	private static final String SAMPLE_HEIGHT = "sample_height";
	private static final String PREVIEW_URL = "preview_url";
	private static final String RATING = "rating";
	private static final String TAGS = "tags";
	private static final String ID = "id";
	private static final String WIDTH = "width";
	private static final String CHANGE = "change";
	private static final String MD5 = "md5";
	private static final String CREATOR_ID = "creator_id";
	private static final String HAS_CHILDREN = "has_children";
	private static final String CREATED_AT = "created_at";
	private static final String STATUS = "status";
	private static final String SOURCE = "source";
	private static final String HAS_NOTES = "has_notes";
	private static final String HAS_COMMENTS = "has_comments";
	private static final String PREVIEW_WIDTH = "preview_width";
	private static final String PREVIEW_HEIGHT = "preview_height";
	
	// hold a temp post here
	private Post tempPost = null;
	private List<Post> postsList = null;
	
	public GelbooruPostsParser(InputStream is)
	{
		// root of the xml file
		RootElement posts = new RootElement(POSTS);
		// posts items
		Element post = posts.getChild(POST);
		
		posts.setStartElementListener(new CustomStartElement(POSTS, this));
		post.setStartElementListener(new CustomStartElement(POST, this));
		post.setEndElementListener(new CustomEndElement(POST, this));
	}
	
	@Override
	public List<Post> get()
	{
		return null;
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
			// it is a post item, make one
			this.tempPost = new Post();
			String id = attrs.getValue(ID);
			String parentId = attrs.getValue(PARENT_ID);
			String md5 = attrs.getValue(MD5);
		}
	}
	
	@Override
	public void end(String elementTag)
	{
		if(elementTag.equals(POST))
		{
			// it is a post item, it is closing, add it to the array
		}
	}
}
