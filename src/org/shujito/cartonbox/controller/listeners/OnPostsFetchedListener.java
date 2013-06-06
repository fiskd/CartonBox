package org.shujito.cartonbox.controller.listeners;

import org.shujito.cartonbox.model.Post;

import android.util.SparseArray;

public interface OnPostsFetchedListener
{ public void onPostsFetched(SparseArray<Post> posts); }