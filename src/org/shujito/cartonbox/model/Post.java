package org.shujito.cartonbox.model;

public class Post
{
	// original
	public static String URL_FILE_FORMAT = "/data/%d.%s";
	// scaled down
	public static String URL_SAMPLE_FORMAT = "/data/sample/sample-%d.%s";
	// thumbnail
	public static String URL_PREVIEW_FORMAT = "/ssd/data/preview/%d.%s";
	
	// sexual rating
	public enum Rating
	{
		// clean and clothed
		Safe,
		// boobs butts nudity
		Questionable,
		// dong pussy sex
		Explicit,
	}
	
	/* fields */
	
	// meta
	int id;
	int parentId;
	String md5;
	// actual image
	String url;
	int width;
	int height;
	// sample
	String sampleUrl;
	// preview (thumbnail)
	String previewUrl;
	// post properties
	Rating rating;
	boolean deleted;
	boolean flagged;
	boolean pending;
	boolean hasChildren;
	// has comments
	String lastCommentedAt;
	// has notes
	String lastNotedAt;
	// useful when the url lacks on information
	Site site;

	/* getters */
	public int getId()
	{ return this.id; }
	public int getParentId()
	{ return this.parentId; }
	public String getMd5()
	{ return this.md5; }
	public String getUrl()
	{ return this.url; }
	public int getWidth()
	{ return this.width; }
	public int getHeight()
	{ return this.height; }
	public String getSampleUrl()
	{ return this.sampleUrl; }
	public String getPreviewUrl()
	{ return this.previewUrl; }
	public Rating getRating()
	{ return this.rating; }
	public boolean isDeleted()
	{ return this.deleted; }
	public boolean isFlagged()
	{ return this.flagged; }
	public boolean isPending()
	{ return this.pending; }
	public boolean isHasChildren()
	{ return this.hasChildren; }
	public String getLastCommentedAt()
	{ return this.lastCommentedAt; }
	public String getLastNotedAt()
	{ return this.lastNotedAt; }
	public Site getSite()
	{ return this.site; }
	
	/* setters */
	public Post setId(int i)
	{
		this.id = i;
		return this;
	}
	public Post setParentId(int i)
	{
		this.parentId = i;
		return this;
	}
	public Post setMd5(String s)
	{
		this.md5 = s;
		return this;
	}
	public Post setUrl(String s)
	{
		this.url = s;
		return this;
	}
	public Post setWidth(int i)
	{
		this.width = i;
		return this;
	}
	public Post setHeight(int i)
	{
		this.height = i;
		return this;
	}
	public Post setSampleUrl(String s)
	{
		this.sampleUrl = s;
		return this;
	}
	public Post setPreviewUrl(String s)
	{
		this.previewUrl = s;
		return this;
	}
	public Post setRating(Rating s)
	{
		this.rating = s;
		return this;
	}
	public Post setDeleted(boolean b)
	{
		this.deleted = b;
		return this;
	}
	public Post setFlagged(boolean b)
	{
		this.flagged = b;
		return this;
	}
	public Post setPending(boolean b)
	{
		this.pending = b;
		return this;
	}
	public Post setHasChildren(boolean b)
	{
		this.hasChildren = b;
		return this;
	}
	public Post setLastCommentedAt(String s)
	{
		this.lastCommentedAt = s;
		return this;
	}
	public Post setLastNotedAt(String s)
	{
		this.lastNotedAt = s;
		return this;
	}
	public Post setSite(Site site)
	{
		this.site = site;
		return this;
	}
	
	/* meth */
}
