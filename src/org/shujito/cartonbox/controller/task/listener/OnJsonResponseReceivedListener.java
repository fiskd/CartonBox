package org.shujito.cartonbox.controller.task.listener;

import org.shujito.cartonbox.model.parser.JsonParser;

public interface OnJsonResponseReceivedListener
{ public void onResponseReceived(JsonParser<?> jp); }