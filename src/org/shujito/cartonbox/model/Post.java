package org.shujito.cartonbox.model;

import java.io.Serializable;

public class Post implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// original
	public static final String URL_FILE_FORMAT = "/data/%s.%s";
	// scaled down
	public static final String URL_SAMPLE_FORMAT = "/data/sample/sample-%s.jpg";
	// thumbnail
	public static final String URL_PREVIEW_FORMAT = "/ssd/data/preview/%s.jpg";
	
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
	private int id;
	private int parentId;
	private String md5;
	private String fileExt;
	private int fileSize;
	// actual image
	private String url;
	private int width;
	private int height;
	// sample
	private String sampleUrl;
	// preview (thumbnail)
	private String previewUrl;
	// post properties
	private Rating rating;
	private boolean deleted;
	private boolean flagged;
	private boolean pending;
	private boolean hasChildren;
	private boolean hasLarge;
	// has comments
	private String lastCommentedAt;
	// has notes
	private String lastNotedAt;
	// dem tags
	private String tags;
	// useful when the post lacks on information
	private Site site;
	
	public Post()
	{
		// assume both original and sampled images
		this.hasLarge = true;
	}
	
	/* getters */
	public int getId()
	{ return this.id; }
	public int getParentId()
	{ return this.parentId; }
	public String getMd5()
	{ return this.md5; }
	public String getFileExt()
	{ return this.fileExt; }
	public int getFileSize()
	{ return this.fileSize; }
	public String getUrl()
	{
		if(this.url == null && this.site != null)
		{
			this.url = this.site.getUrl().concat(
				String.format(URL_FILE_FORMAT, this.md5, this.fileExt));
		}
		else if((this.url != null && this.url.charAt(0) == '/') && this.site != null)
		{
			// XXX: was I supposed to put something here??
		}
		
		return this.url;
	}
	public int getWidth()
	{ return this.width; }
	public int getHeight()
	{ return this.height; }
	public String getSampleUrl()
	{
		if(!this.hasLarge)
			return this.getUrl();
		
		if(this.sampleUrl == null && this.site != null)
		{
			this.sampleUrl = this.site.getUrl().concat(
				String.format(URL_SAMPLE_FORMAT, this.md5));
		}
		
		return this.sampleUrl;
	}
	public String getPreviewUrl()
	{
		if(this.previewUrl == null && this.site != null)
		{
			this.previewUrl = this.site.getUrl().concat(
				String.format(URL_PREVIEW_FORMAT, this.md5));
		}
		
		return this.previewUrl;
	}
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
	public boolean isHasLarge()
	{ return this.hasLarge; }
	public String getLastCommentedAt()
	{ return this.lastCommentedAt; }
	public String getLastNotedAt()
	{ return this.lastNotedAt; }
	public String getTags()
	{ return this.tags; }
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
	public Post setFileExt(String s)
	{
		this.fileExt = s;
		return this;
	}
	public Post setFileSize(int i)
	{
		this.fileSize = i;
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
	public Post setRating(Rating r)
	{
		this.rating = r;
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
	public Post setHasLarge(boolean b)
	{
		this.hasLarge = b;
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
	public Post setTags(String tags)
	{
		this.tags = tags;
		return this;
	}
	public Post setSite(Site s)
	{
		this.site = s;
		return this;
	}
	
	/* meth */
	@Override
	public String toString()
	{
		if(this.site != null)
		{
			StringBuilder postUrl = new StringBuilder(this.site.getUrl());
			postUrl.append(this.site.getPostViewApi());
			postUrl.append('/');
			postUrl.append(this.id);
			return postUrl.toString();
		}
		return super.toString();
	}
}
