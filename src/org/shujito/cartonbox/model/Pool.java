package org.shujito.cartonbox.model;

import java.util.List;

public class Pool
{
	List<Integer> postIds;
	List<Post> posts;
	// meta
	int id;
	String name;
	String description;
	int postCount;
	// useful when the posts lack on information
	Site site;
}
