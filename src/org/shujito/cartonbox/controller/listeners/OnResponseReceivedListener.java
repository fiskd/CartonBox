package org.shujito.cartonbox.controller.listeners;

import org.shujito.cartonbox.model.JsonParser;

public interface OnResponseReceivedListener
{ public void onResponseReceived(JsonParser<?> jp); }